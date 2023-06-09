package de.uscoutz.nexus.database;


import de.uscoutz.nexus.NexusPlugin;

import java.sql.*;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class DatabaseAdapter {

    private NexusPlugin plugin;

    private MySQL mySQL;

    private ExecutorService executorService;

    private String username, password, hostname, database;

    public DatabaseAdapter(ExecutorService executorService, NexusPlugin plugin) {
        username = plugin.getConfig().getString("mysql.username");
        password = plugin.getConfig().getString("mysql.password");
        hostname = plugin.getConfig().getString("mysql.hostname");
        database = plugin.getConfig().getString("mysql.database");
        this.plugin = plugin;

        this.executorService = executorService;

        this.mySQL = new MySQL(username, password, hostname, database, executorService, plugin).connect();
        createTables();
    }

    public ResultSet query(String query) {
        return this.mySQL.query(query);
    }

    public boolean keyExists(String table, String whereKey, Object setKey) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        boolean value = false;

        ResultSet resultSet = this.mySQL.query("SELECT * FROM `" + table + "` WHERE `" + whereKey + "`='" + setKey + "'");

        try {
            if(resultSet.next()) {
                value = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return value;
    }

    public boolean keyExistsAsync(String table, String whereKey, Object setKey) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        Future<Boolean> future = this.executorService.submit(() -> keyExists(table, whereKey, setKey));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean keyExistsTwo(String table, String whereKey, Object setKey, String whereKey2, Object setKey2) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        boolean value = false;

        ResultSet resultSet = this.mySQL.query("SELECT * FROM `" + table + "` WHERE `" + whereKey + "`='" + setKey + "' AND " + whereKey2 + " = '" + setKey2 + "'");

        try {
            if(resultSet.next()) {
                value = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return value;
    }

    public boolean keyExistsThree(String table, String whereKey, Object setKey, String whereKey2, Object setKey2, String whereKey3, Object setKey3) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        boolean value = false;

        ResultSet resultSet = this.mySQL.query("SELECT * FROM `" + table + "` WHERE `" + whereKey + "`='" + setKey + "' AND " + whereKey2 + " = '" + setKey2 + "' AND " + whereKey3 + " = '" + setKey3 + "'");

        try {
            if(resultSet.next()) {
                value = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return value;
    }

    public boolean keyExistsTwoAsync(String table, String whereKey, Object setKey, String whereKey2, Object setKey2) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        Future<Boolean> future = this.executorService.submit(() -> keyExistsTwo(table, whereKey, setKey, whereKey2, setKey2));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean keyExistsThreeAsync(String table, String whereKey, Object setKey, String whereKey2, Object setKey2, String whereKey3, Object setKey3) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        Future<Boolean> future = this.executorService.submit(() -> keyExistsThree(table, whereKey, setKey, whereKey2, setKey2, whereKey3, setKey3));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Insert data in your database
     * @param table -> Table where you want insert
     * @param value -> Data for insert
     */
    public void set(String table, Object... value) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO `" + table + "` VALUES (");

        short i = 1;

        for(Object data : value) {
            if(value.length == i) {
                stringBuilder.append("'").append(data.toString()).append("')");
            } else {
                stringBuilder.append("'").append(data.toString()).append("', ");
            }
            i++;
        }

        //System.out.println(Arrays.toString(value));

        //System.out.println(stringBuilder);

        this.mySQL.queryUpdate(stringBuilder.toString());
    }

    /**
     * Insert data in your database
     * @param table -> Table where you want insert
     * @param value -> Data for insert
     */
    public void setAsync(String table, Object... value) {
        this.executorService.execute(() -> set(table, value));
    }

    /**
     * Update already exisiting data in your database
     * @param table -> Table you want to update
     * @param whereKey -> Key definition for update
     * @param setKey -> In this place
     * @param databases -> Updates
     */
    public void update(String table, String whereKey, Object setKey, DatabaseUpdate... databases) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        StringBuilder stringBuilder = new StringBuilder("UPDATE `" + table + "` SET ");

        short i = 1;

        for(DatabaseUpdate data : databases) {
            if(databases.length == i) {
                stringBuilder.append("`").append(data.getKey()).append("`='").append(data.getValue()).append("' ");
            } else {
                stringBuilder.append("`").append(data.getKey()).append("`='").append(data.getValue()).append("', ");
            }
            i++;
        }

        stringBuilder.append("WHERE `").append(whereKey).append("`='").append(setKey).append("'");

        //System.out.println(stringBuilder);

        this.mySQL.queryUpdate(stringBuilder.toString());
    }

    /**
     * Update already exisiting data in your database
     * @param table -> Table you want to update
     * @param whereKey -> Key definition for update
     * @param setKey -> In this place
     * @param databases -> Updates
     */
    public void updateAsync(String table, String whereKey, Object setKey, DatabaseUpdate... databases) {
        this.executorService.execute(() -> update(table, whereKey, setKey, databases));
    }

    public void updateTwo(String table, String whereKey, Object setKey, String secWhereKey, Object secSetKey, DatabaseUpdate... databases) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        StringBuilder stringBuilder = new StringBuilder("UPDATE `" + table + "` SET ");

        short i = 1;

        for(DatabaseUpdate data : databases) {
            if(databases.length == i) {
                stringBuilder.append("`").append(data.getKey()).append("`='").append(data.getValue()).append("' ");
            } else {
                stringBuilder.append("`").append(data.getKey()).append("`='").append(data.getValue()).append("', ");
            }
            i++;
        }

        stringBuilder.append("WHERE `").append(whereKey).append("`='").append(setKey).append("' AND `").append(secWhereKey).append("`='").append(secSetKey).append("'");

        //System.out.println(stringBuilder);

        this.mySQL.queryUpdate(stringBuilder.toString());
    }

    public void updateTwoAsync(String table, String whereKey, Object setKey, String secWhereKey, Object secSetKey, DatabaseUpdate... databases) {
        this.executorService.execute(() -> updateTwo(table, whereKey, setKey, secWhereKey, secSetKey, databases));
    }

    public void updateThree(String table, String whereKey, Object setKey, String secWhereKey, Object secSetKey, String thirdWhereKey, Object thirdSetKey, DatabaseUpdate... databases) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        StringBuilder stringBuilder = new StringBuilder("UPDATE `" + table + "` SET ");

        short i = 1;

        for(DatabaseUpdate data : databases) {
            if(databases.length == i) {
                stringBuilder.append("`").append(data.getKey()).append("`='").append(data.getValue()).append("' ");
            } else {
                stringBuilder.append("`").append(data.getKey()).append("`='").append(data.getValue()).append("', ");
            }
            i++;
        }

        stringBuilder.append("WHERE `").append(whereKey).append("`='").append(setKey).append("' AND `").append(secWhereKey).append("`='").append(secSetKey).append("'").append(" AND `").append(thirdWhereKey).append("`='").append(thirdSetKey).append("'");

        System.out.println(stringBuilder);

        this.mySQL.queryUpdate(stringBuilder.toString());
    }

    public void updateThreeAsync(String table, String whereKey, Object setKey, String secWhereKey, Object secSetKey, String thirdWhereKey, Object thirdSetKey, DatabaseUpdate... databases) {
        this.executorService.execute(() -> updateThree(table, whereKey, setKey, secWhereKey, secSetKey, thirdWhereKey, thirdSetKey, databases));
    }

    public void delete(String table, String whereKey, Object setKey) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        //System.out.println(table + " " + whereKey + " " + setKey);

        this.mySQL.queryUpdate("DELETE FROM `" + table + "` WHERE `" + whereKey + "`='" + setKey + "'");
    }

    public void deleteTwo(String table, String whereKey, Object setKey, String whereKey2, Object setKey2) {
        if(setKey == null || table == null || whereKey == null) throw new NullPointerException("setKey, whereKey or table cannot be null");

        //System.out.println(table + " " + whereKey + " " + setKey + " " + whereKey2 + " " + setKey2);

        this.mySQL.queryUpdate("DELETE FROM " + table + " WHERE " + whereKey + " = '" + setKey + "' AND " + whereKey2 + " = '" + setKey2 + "'");
    }

    public void deleteAsync(String table, String whereKey, Object setKey) {
        this.executorService.execute(() -> delete(table, whereKey, setKey));
    }

    public void deleteTwoAsync(String table, String whereKey, Object setKey, String whereKey2, String setKey2) {
        this.executorService.execute(() -> deleteTwo(table, whereKey, setKey, whereKey2, setKey2));
    }

    public ResultSet get(String table, String orderBy, int limit) {
        return this.mySQL.query("SELECT * FROM " + table + " ORDER BY " + orderBy + " LIMIT " + limit);
    }

    public ResultSet get(String table, String whereKey, String setkey) {
        return this.mySQL.query("SELECT * FROM " + table + " WHERE " + whereKey + "='" + setkey + "'");
    }

    public ResultSet get(String table, String whereKey, String setkey, String orderBy) {
        return this.mySQL.query("SELECT * FROM " + table + " WHERE " + whereKey + "='" + setkey + "' ORDER BY " + orderBy);
    }

    public ResultSet getTwo(String table, String whereKey, String setkey, String secWhereKey, String secSetKey) {
        return this.mySQL.query("SELECT * FROM " + table + " WHERE " + whereKey + "='" + setkey + "' AND " + secWhereKey + "='" + secSetKey + "'");
    }

    public ResultSet getThreeAsync(String table, String whereKey, String setKey, String secWhereKey, String secSetKey, String thirdWhereKey, String thirdSetKey) {
        Future<ResultSet> result = this.executorService.submit(() -> getThree(table, whereKey, setKey, secWhereKey, secSetKey, thirdWhereKey, thirdSetKey));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet getThree(String table, String whereKey, String setkey, String secWhereKey, String secSetKey, String thirdWhereKey, String thirdSetKey) {
        return this.mySQL.query("SELECT * FROM " + table + " WHERE " + whereKey + "='" + setkey + "' AND " + secWhereKey + "='" + secSetKey + "' AND " + thirdWhereKey + "='" + thirdSetKey + "'");
    }

    public ResultSet getTwoAsync(String table, String whereKey, String setKey, String secWhereKey, String secSetKey) {
        Future<ResultSet> result = this.executorService.submit(() -> getTwo(table, whereKey, setKey, secWhereKey, secSetKey));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet getAsync(String table, String whereKey, String setKey) {
        Future<ResultSet> result = this.executorService.submit(() -> get(table, whereKey, setKey));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet getAsync(String table, String whereKey, String setKey, String orderBy) {
        Future<ResultSet> result = this.executorService.submit(() -> get(table, whereKey, setKey, orderBy));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createTables() {
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS profiles (profileId VARCHAR(36), owner VARCHAR(36), nexusLevel int, start bigint, lastActivity bigint, souls bigint)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS playerProfiles (player VARCHAR(36), profileId VARCHAR(36), slot int, joinedProfile bigint, playtime bigint, inventory text, money bigint)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS players (player VARCHAR(36), currentProfile int, firstLogin bigint, playtime bigint, gameprofile text)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS schematics (profileId VARCHAR(36), schematicId VARCHAR(36), schematicType text, level int, rotation int, location text, placed bigint, damage double)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS collectors (schematicId VARCHAR(36), neededItems text, intact boolean)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS storages (profileId VARCHAR(36), storageId text, inventory text)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS raids (profileId VARCHAR(36), raidType text, won boolean, kills int, ended bigint)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS quests (profileId VARCHAR(36), task text, progress bigint, begun bigint, finished bigint)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS profileStats (profileId VARCHAR(36), lostRaids int, wonRaids int)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS playerStats (player VARCHAR(36), profileId VARCHAR(36), deaths int, kills int)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS brokenBlocks (player VARCHAR(36), profileId VARCHAR(36), material text, amount int)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS boughtItems (profileId VARCHAR(36), item text, amount int)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS skills (profileId VARCHAR(36), player VARCHAR(36), skill text, level int, xp int)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS autoMiners (profileId VARCHAR(36), schematicId VARCHAR(36), autoMinerType text, inventory text)");
        mySQL.queryUpdate("CREATE TABLE IF NOT EXISTS coopInvitations (profileId VARCHAR(36), sender VARCHAR(36), receiver VARCHAR(36))");
    }
}

class MySQL {

    private NexusPlugin plugin;

    private String username, password, hostname, database;

    private ExecutorService executorService;

    private Connection connection;

    public MySQL(String username, String password, String hostname, String database, ExecutorService executorService, NexusPlugin plugin) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.database = database;
        this.executorService = executorService;
        this.plugin = plugin;
    }

    public MySQL connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + "3306" + "/" + database + "?autoReconnect=true", username, password);
            System.out.println("[Database] Es konnte erfolgreich mit der Datenbank verbunden werden");
        } catch (SQLException e) {
            System.out.println("[Database] Es konnte nicht mit der Datenbank verbunden werden: " + e);
        }

        return this;
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(PreparedStatement statement) {
        checkConnection();
        this.executorService.execute(() -> this.queryUpdate(statement));
    }

    public void update(String statement) {
        checkConnection();
        this.executorService.execute(() -> this.queryUpdate(statement));
    }

    public void query(PreparedStatement statement, Consumer<ResultSet> consumer) {
        checkConnection();
        this.executorService.execute(() -> {
            ResultSet result = this.query(statement);
            consumer.accept(result);
        });
    }

    public void query(String statement, Consumer<ResultSet> consumer) {
        checkConnection();
        this.executorService.execute(() -> {
            ResultSet result = this.query(statement);
            consumer.accept(result);
        });
    }

    public PreparedStatement prepare(String query) {
        checkConnection();
        try {
            return this.connection.prepareStatement(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void queryUpdate(String query) {
        checkConnection();
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            queryUpdate(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queryUpdate(PreparedStatement preparedStatement) {
        checkConnection();
        try {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet query(String query) {
        checkConnection();
        try {
            return query(this.connection.prepareStatement(query));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet query(PreparedStatement statement) {
        checkConnection();
        try {
            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkConnection() {
        try {
            //this.connection.isValid(10);
            if (this.connection == null || this.connection.isClosed()) connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connection != null;
    }
}
