package su.litvak.justdlna.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

public class ViewLog {
    private static final Logger LOG = LoggerFactory.getLogger(ViewLog.class);
    private final static String DB_FILE = System.getProperty("user.dir") + File.separatorChar + "logs" + File.separatorChar + "view.log";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException cnfe) {
            LOG.error("h2 driver is not available", cnfe);
        }
    }

    public static void createDatabase() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            rs = conn.getMetaData().getTables(null, "PUBLIC", null, null);
            while (rs.next()) {
                LOG.info("TABLE: " + rs.getString("TABLE_NAME"));
            }
        } catch (SQLException sqe) {
            close(conn, null, rs);
            LOG.error("Error occurred while creating view log database", sqe);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:" + DB_FILE);
    }

    private static void close(Connection conn) {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException sqe) {
            LOG.error("Error while closing connection", sqe);
        }
    }

    private static void close(Statement stmt) {
        if (stmt == null) return;
        try {
            stmt.close();
        } catch (SQLException sqe) {
            LOG.error("Error while closing statement", sqe);
        }
    }

    private static void close(ResultSet rs) {
        if (rs == null) return;
        try {
            rs.close();
        } catch (SQLException sqe) {
            LOG.error("Error while closing result set", sqe);
        }
    }

    private static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(conn);
        close(stmt);
        close(rs);
    }
}
