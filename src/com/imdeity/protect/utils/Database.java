package com.imdeity.protect.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import com.avaje.ebeaninternal.server.lib.sql.DataSourceException;
import com.imdeity.deityapi.Deity;
import com.imdeity.protect.DeityProtect;

/**
 * MySQL Database Class
 * 
 * @author vanZeben
 * 
 */
public class Database {

    private Connection conn;
    private boolean databaseOutput = false;

    public Database(boolean check) {
        // Load the driver instance
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw new DataSourceException("[DeityAPI] Failed to initialize JDBC driver");
        }

        this.connect();
        this.databaseOutput = check;
    }

    // check if its closed
    private void connect() {
        try {
            this.conn = DriverManager.getConnection(this.getConnectionString());
            System.out.println("[DeityAPI] Connection success!");
        } catch (SQLException ex) {
            System.out.println("[DeityAPI] Connection to MySQL failed! Check status of MySQL server!");
            this.dumpSqlException(ex);
        }
    }

    private void dumpSqlException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
        ex.printStackTrace();
    }

    private void ensureConnection() {
        try {
            if (!this.conn.isValid(5)) {
                this.connect();
            }
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
        }
    }

    public Connection getConn() {
        return this.conn;
    }

    private String getConnectionString() {
        return "jdbc:mysql://" + DeityProtect.plugin.config.getString(DeityProtectionConfigHelper.MYSQL_SERVER_ADDRESS) + ":" + DeityProtect.plugin.config.getInt(DeityProtectionConfigHelper.MYSQL_SERVER_PORT) + "/"
                + DeityProtect.plugin.config.getString(DeityProtectionConfigHelper.MYSQL_DATABASE_NAME) + "?user=" + DeityProtect.plugin.config.getString(DeityProtectionConfigHelper.MYSQL_DATABASE_USERNAME) + "&password="
                + DeityProtect.plugin.config.getString(DeityProtectionConfigHelper.MYSQL_DATABASE_PASSWORD);
    }

    // Get Int
    // only return first row / first field
    public Integer GetInt(String sql) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer result = 0;

        /*
         * Double check connection to MySQL
         */
        try {
            if (!this.conn.isValid(5)) {
                this.connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt = this.conn.prepareStatement(sql);
            if (stmt.executeQuery() != null) {
                stmt.executeQuery();
                rs = stmt.getResultSet();
                if (rs.next()) {
                    result = rs.getInt(1);
                } else {
                    result = 0;
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return result;
    }

    private PreparedStatement prepareSqlStatement(String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = this.conn.prepareStatement(sql);

        int counter = 1;

        for (Object param : params) {
            if (param instanceof Integer) {
                stmt.setInt(counter++, (Integer) param);
            } else if (param instanceof Short) {
                stmt.setShort(counter++, (Short) param);
            } else if (param instanceof Long) {
                stmt.setLong(counter++, (Long) param);
            } else if (param instanceof Double) {
                stmt.setDouble(counter++, (Double) param);
            } else if (param instanceof String) {
                stmt.setString(counter++, (String) param);
            } else if (param == null) {
                stmt.setNull(counter++, Types.NULL);
            } else if (param instanceof Object) {
                stmt.setObject(counter++, param);
            } else {
                System.out.printf("Database -> Unsupported data type %s", param.getClass().getSimpleName());
            }
        }
        if (this.databaseOutput) {
            Deity.chat.out("[DeitySQL]", stmt.toString());
            // try {
            // throw new Exception();
            // } catch (Exception ex) {
            // ex.printStackTrace();
            // }
        }

        return stmt;
    }

    // read query
    public HashMap<Integer, ArrayList<Object>> read(String sql, Object... params) {
        try {
            if (!this.conn.isValid(5)) {
                this.connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<Integer, ArrayList<Object>> Rows = new HashMap<Integer, ArrayList<Object>>();

        try {
            stmt = this.prepareSqlStatement(sql, params);
            if (stmt.executeQuery() != null) {
                stmt.executeQuery();
                rs = stmt.getResultSet();
                while (rs.next()) {
                    ArrayList<Object> Col = new ArrayList<Object>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Col.add(rs.getString(i));
                    }
                    Rows.put(rs.getRow(), Col);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            // release dataset
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore
                stmt = null;
            }
            if (Rows.isEmpty() || (Rows == null) || (Rows.get(1) == null)) { return null; }
        }
        return Rows;
    }

    public DatabaseResults read2(String sql, Object... params) {

        this.ensureConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        DatabaseResults results = null;

        try {
            stmt = this.prepareSqlStatement(sql, params);
            rs = stmt.executeQuery();
            if (rs != null) {
                ResultSetMetaData meta = rs.getMetaData();
                results = new DatabaseResults(meta);
                while (rs.next()) {
                    results.addRow(rs);
                }
            }
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
        } finally {
            // release dataset
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {/* ignore */
                }
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {/* ignore */
                }
                stmt = null;
            }
            if ((results == null) || !results.hasRows() || (results.rawResults == null)) { return null; }
        }
        return results;
    }

    public String tableName(String prefix, String nameOfTable) {
        return (String.format("`%s`.`%s`", DeityProtect.plugin.config.getString(DeityProtectionConfigHelper.MYSQL_DATABASE_NAME), prefix + nameOfTable));
    }

    // write query
    public boolean write(String sql, Object... params) {
        try {
            this.ensureConnection();
            PreparedStatement stmt = this.prepareSqlStatement(sql, params);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
            return false;
        }
    }

    // write query
    public boolean writeNoError(String sql) {
        try {
            PreparedStatement stmt = null;
            stmt = this.conn.prepareStatement(sql);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

}
