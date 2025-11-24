
package it.maurizio.duckdb.df;

import java.sql.*;
import java.util.List;

/**
 * DuckDataFrame: wrapper per operazioni stile Pandas su DuckDB.
 * Permette di registrare CSV come vista, eseguire SELECT, FILTER, GROUP AGG
 * e convertire i risultati in HTML.
 */
public class DuckDataFrame implements AutoCloseable {
    private final Connection conn;

    public DuckDataFrame() {
        try {
            this.conn = DriverManager.getConnection("jdbc:duckdb:");
        } catch (SQLException e) {
            throw new RuntimeException("Errore connessione DuckDB", e);
        }
    }

    /** Registra un CSV come vista */
    public void registerCsv(String viewName, String path) {
        String sql = "CREATE OR REPLACE VIEW " + viewName +
                     " AS SELECT * FROM read_csv_auto('" + path.replace("'", "''") + "')";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Errore registrazione CSV", e);
        }
    }

    /** SELECT su colonne specifiche */
    public ResultSet select(String viewName, List<String> cols) {
        String columns = String.join(",", cols);
        String sql = "SELECT " + columns + " FROM " + viewName;
        try {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Errore SELECT", e);
        }
    }

    /** Filtro con WHERE */
    public ResultSet filter(String viewName, String whereExpr) {
        String sql = "SELECT * FROM " + viewName + " WHERE " + whereExpr;
        try {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Errore FILTER", e);
        }
    }

    /** GROUP BY con aggregazione */
    public ResultSet groupAgg(String viewName, List<String> groupCols, String aggExpr) {
        String g = String.join(",", groupCols);
        String sql = "SELECT " + g + ", " + aggExpr + " FROM " + viewName + " GROUP BY " + g;
        try {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Errore GROUP AGG", e);
        }
    }

    /** Converte ResultSet in tabella HTML */
    public String toHtmlTable(ResultSet rs) {
        try {
            StringBuilder sb = new StringBuilder();
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            sb.append("<table border='1' cellspacing='0' cellpadding='4'><thead><tr>");
            for (int i = 1; i <= n; i++) {
                sb.append("<th>").append(md.getColumnLabel(i)).append("</th>");
            }
            sb.append("</tr></thead><tbody>");
            int rows = 0;
            while (rs.next() && rows < 1000) {
                sb.append("<tr>");
                for (int i = 1; i <= n; i++) {
                    sb.append("<td>").append(String.valueOf(rs.getObject(i))).append("</td>");
                }
                sb.append("</tr>");
                rows++;
            }
            sb.append("</tbody></table>");
            return sb.toString();
        } catch (SQLException e) {
            throw new RuntimeException("Errore conversione HTML", e);
        }
    }

    @Override
    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignored) {}
    }
}
