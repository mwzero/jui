package it.my.framework;
import com.google.gson.Gson;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcSessionManager implements SessionManager {
    private final String jdbcUrl;
    private final String user;
    private final String password;
    private final Gson gson = new Gson();

    public JdbcSessionManager(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        initDb();
    }
    private void initDb() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS SESSIONS (session_id VARCHAR(255), widget_id VARCHAR(255), value_json CLOB, value_type VARCHAR(255), PRIMARY KEY (session_id, widget_id))";
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public Map<String, Object> getState(String sessionId) {
        Map<String, Object> state = new ConcurrentHashMap<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement("SELECT widget_id, value_json, value_type FROM SESSIONS WHERE session_id = ?")) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        state.put(rs.getString("widget_id"), gson.fromJson(rs.getString("value_json"), Class.forName(rs.getString("value_type"))));
                    } catch (Exception e) {}
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return state;
    }
    @Override
    public void updateState(String sessionId, String widgetId, Object value) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement("MERGE INTO SESSIONS KEY (session_id, widget_id) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, sessionId);
            ps.setString(2, widgetId);
            ps.setString(3, gson.toJson(value));
            ps.setString(4, value.getClass().getName());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public void removeSession(String sessionId) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM SESSIONS WHERE session_id = ?")) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}