package su.litvak.justdlna.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewLog {
    private static final Logger LOG = LoggerFactory.getLogger(ViewLog.class);
    private final static String DB_FILE = System.getProperty("user.dir") + File.separatorChar + "logs" + File.separatorChar + "view.log";
    private final static String TBL_VIEW_HISTORY = "VIEW_HISTORY";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException cnfe) {
            LOG.error("h2 driver is not available", cnfe);
        }
    }

    public static void init() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            rs = conn.getMetaData().getTables(null, "PUBLIC", TBL_VIEW_HISTORY, null);
            if (rs.next()) {
                return;
            }
            LOG.info("Creating " + TBL_VIEW_HISTORY + " table");
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE " + TBL_VIEW_HISTORY + " (view_date TIMESTAMP NOT NULL, filepath VARCHAR(1024) NOT NULL, format VARCHAR(10) NOT NULL)");
            stmt.executeUpdate("CREATE INDEX " + TBL_VIEW_HISTORY + "_fmt_vd ON " + TBL_VIEW_HISTORY + "(format, view_date)");
        } catch (SQLException sqe) {
            LOG.error("Error occurred while creating view log database", sqe);
        } finally {
            close(conn, stmt, rs);
        }
    }

    public static void log(File file, Class<? extends MediaFormat> formatClass) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO " + TBL_VIEW_HISTORY + " (view_date, filepath, format) VALUES (?, ?, ?)");
            stmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(2, file.getAbsolutePath());
            stmt.setString(3, Formats.toString(formatClass));
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException sqe) {
            LOG.error("Error occurred while saving log record for: " + file.getAbsolutePath(), sqe);
        } finally {
            close(conn, stmt, null);
        }
    }

    public static <T extends Enum<T> & MediaFormat> List<ItemNode> getLastViewItems(int limit, Class<T> formatClass, ContainerNode container) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            List<ItemNode> result = new ArrayList<ItemNode>(limit);
            int offset = 0;
            while (result.size() < limit) {
                rs = stmt.executeQuery("SELECT DISTINCT view_date, filepath FROM " + TBL_VIEW_HISTORY + " WHERE format='" + Formats.toString(formatClass) +  "' ORDER BY view_date DESC LIMIT " + limit + " OFFSET " + offset);

                int resultCount = 0;
                while (rs.next()) {
                    resultCount++;
                    ItemNode item = container.getItem(new File(rs.getString(2)));
                    if (item != null) {
                        result.add(item);
                    }
                }
                if (resultCount < limit - 1) {
                    return result;
                }
                offset += limit;
            }
            return result;
        } catch (SQLException sqe) {
            LOG.error("Error occurred while listing log records", sqe);
        } finally {
            close(conn, stmt, rs);
        }
        return Collections.emptyList();
    }

    public static void clear() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("TRUNCATE TABLE " + TBL_VIEW_HISTORY);
        } catch (SQLException sqe) {
            LOG.error("Error occurred while clearing view history database", sqe);
        } finally {
            close(conn, stmt, null);
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
