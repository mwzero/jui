package it.my.data;
import java.sql.*;
import java.util.*;
public class DuckDataFrame {
    private Connection c;
    public DuckDataFrame() {
        try { Class.forName("org.duckdb.DuckDBDriver"); c = DriverManager.getConnection("jdbc:duckdb::memory:"); c.createStatement().execute("INSTALL 'csv'; LOAD 'csv';"); }
        catch(Exception e) { e.printStackTrace(); }
    }
    public void registerCsv(String path, String tbl) throws SQLException {
        c.createStatement().execute(String.format("CREATE OR REPLACE TABLE %s AS SELECT * FROM read_csv_auto('%s')", tbl, path.replace("\\", "/")));
    }
    public String query(String sql) throws SQLException {
        try(ResultSet rs = c.createStatement().executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            StringBuilder html = new StringBuilder("<table class='min-w-full border'><thead><tr class='bg-gray-200'>");
            for(int i=1;i<=cols;i++) html.append("<th class='p-2'>").append(md.getColumnLabel(i)).append("</th>");
            html.append("</tr></thead><tbody>");
            while(rs.next()) {
                html.append("<tr>");
                for(int i=1;i<=cols;i++) html.append("<td class='p-2 border'>").append(rs.getString(i)).append("</td>");
                html.append("</tr>");
            }
            return html.append("</tbody></table>").toString();
        }
    }
    public void close() { try { if(c!=null) c.close(); } catch(Exception e){} }
}