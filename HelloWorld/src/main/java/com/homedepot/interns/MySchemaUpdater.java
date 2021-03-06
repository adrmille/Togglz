package com.homedepot.interns;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.togglz.core.activation.UsernameActivationStrategy;
import org.togglz.core.repository.util.MapSerializer;
import org.togglz.core.util.DbUtils;
import org.togglz.core.util.Strings;

/**
 * Class that creates and migrates the database table required by {@link JDBCStateRepository}.
 * 
 * @author Christian Kaltepoth, RXS6631, DXR3590
 */
class MySchemaUpdater {

    private final Connection connection;

    private final String tableName;

    private final MapSerializer serializer;

    protected MySchemaUpdater(Connection connection, String tableName, MapSerializer serializer) {
        this.connection = connection;
        this.tableName = tableName;
        this.serializer = serializer;
    }

    protected boolean doesTableExist() throws SQLException {
        return isSuccessful("SELECT * FROM %TABLE%");
    }
/**
 * Creates the Togglz table with the proper feature switch attributes. We edited this to include specific Home Depot field names.
 * 
 * @throws SQLException
 */
    protected void migrateToVersion1() throws SQLException {
        execute("CREATE TABLE %TABLE% (FEATURE_ID CHAR(16),"
        		+ "FEATURE_NAME VARCHAR(100) PRIMARY KEY,"
        		+ "FEATURE_ENABLED INTEGER,"
        		+ "LAST_UPD_TS DATETIME,"
        		+ "LAST_UPD_SYSUSR_ID VARCHAR(50),"
        		+ "APP_ENV CHAR(4),"
        		+ "APP_OWNER VARCHAR(50),"
        		+ "STRATEGY_ID VARCHAR(200), "
        		+ "STRATEGY_PARAMS VARCHAR(2000))");
    }

    protected boolean isSchemaVersion1() throws SQLException {
        return columnExists(MyColumns.FEATURE_NAME) && !columnExists(MyColumns.STRATEGY_ID);
    }
/**
 * Function as provided by Togglz. Its called when database connection is made to update the cache
 * @throws SQLException
 */
    protected void migrateToVersion2() throws SQLException {

        /*
         * step 1: add new MyColumns
         */
        execute("ALTER TABLE %TABLE% ADD STRATEGY_ID VARCHAR(200)");
        execute("ALTER TABLE %TABLE% ADD STRATEGY_PARAMS VARCHAR(2000)");

        /*
         * step 2: migrate the existing data
         */
        Statement updateDataStmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        try {

            ResultSet resultSet = null;
            try {

                resultSet = updateDataStmt.executeQuery(substitute(
                    "SELECT FEATURE_NAME, FEATURE_USERS, STRATEGY_ID, STRATEGY_PARAMS,FROM %TABLE%"));

                while (resultSet.next()) {

                    // migration is only required if there is data in the users column
                    String users = resultSet.getString(MyColumns.FEATURE_USERS);
                    if (Strings.isNotBlank(users)) {

                        // convert the user list to the new parameters format
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(UsernameActivationStrategy.PARAM_USERS, users);
                        String paramData = serializer.serialize(params);
                        resultSet.updateString(MyColumns.STRATEGY_PARAMS, paramData);

                        // only overwrite strategy ID if it is not set yet
                        String strategyId = resultSet.getString(MyColumns.STRATEGY_ID);
                        if (Strings.isBlank(strategyId)) {
                            resultSet.updateString(MyColumns.STRATEGY_ID, UsernameActivationStrategy.ID);
                        }

                        // perform the update
                        resultSet.updateRow();

                    }

                }

            } finally {
                DbUtils.closeQuietly(resultSet);
            }
        } finally {
            DbUtils.closeQuietly(updateDataStmt);
        }

        /*
         * step 3: remove the deprecated column
         */
        execute("ALTER TABLE %TABLE% DROP COLUMN FEATURE_USERS");

    }

    /**
     * Returns <code>true</code> if the given column exists in the features table.
     * 
     * @throws SQLException
     */
    private boolean columnExists(String column) throws SQLException {
        return isSuccessful("SELECT " + column + " FROM %TABLE%");
    }

    /**
     * Returns <code>true</code> if the supplied statement returned without any error
     */
    private boolean isSuccessful(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(substitute(sql));
        } catch (SQLException e) {
            return false;
        } finally {
            DbUtils.closeQuietly(statement);
        }
        return true;
    }

    /**
     * Executes the given statement.
     */
    private void execute(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(substitute(sql));
        } finally {
            DbUtils.closeQuietly(statement);
        }
    }

    /**
     * Replaces <code>%TABLE%</code> with the table name configured.
     */
    private String substitute(String s) {
        return s.replace("%TABLE%", tableName);
    }

}