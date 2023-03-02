package me.gorenjec.spedupfurnaces.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.gorenjec.spedupfurnaces.SpedupFurnaces;
import me.gorenjec.spedupfurnaces.models.CustomFurnace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLStorage {
    private HikariDataSource dataSource;
    private final SpedupFurnaces instance;
    private final FileConfiguration config;
    private static final String PLAYERDATA_TABLE = "furnace_data";

    public SQLStorage(SpedupFurnaces instance) {
        this.instance = instance;
        this.config = instance.getConfig();

        boolean useMySQL = config.getBoolean("data_storage.use_mysql");
        String path = instance.getDataFolder().getPath();

        HikariConfig hikariConfig = new HikariConfig();

        if (useMySQL) {
            hikariConfig.setDriverClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikariConfig.setUsername(config.getString("data_storage.username"));
            hikariConfig.setPassword(config.getString("data_storage.password"));
            String hostname = config.getString("data_storage.ip");
            String port = config.getString("data_storage.port");
            String database = config.getString("data_storage.database");
            String useSSL = config.getBoolean("data_storage.database") ? "true" : "false";
            hikariConfig.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=" + useSSL);
        } else {
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + path + "/database.sqlite");
        }

        hikariConfig.setPoolName("SpedupFurnacesPlugin");
        hikariConfig.setMaxLifetime(60000);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.addDataSourceProperty("database", config.getString("data_storage.database"));

        this.dataSource = new HikariDataSource(hikariConfig);

        constructTables();
    }

    private void constructTables() {
        boolean mysql = instance.getConfig().getBoolean("data_storage.use_mysql");
        String autoInc = mysql ? "AUTO_INCREMENT" : "AUTOINCREMENT";

        try (Connection connection = dataSource.getConnection()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS " + PLAYERDATA_TABLE + "("
                    + "id INTEGER PRIMARY KEY " + autoInc + ","
                    + "type INT, "
                    + "level INT, "
                    + "loc_x INT, "
                    + "loc_y INT, "
                    + "loc_z INT, "
                    + "loc_world INT"
                    + ")";
            PreparedStatement statement = connection.prepareStatement(createTableSql);
            statement.execute();

            instance.getLogger().info("Verified data tables.");
        } catch (SQLException e) {
            instance.getLogger().severe("Could not create tables!");
            e.printStackTrace();
        }
    }

    /*
    id
    type
    level
    loc_x
    loc_y
    loc_z
    loc_world
     */

    public void addFurnace(CustomFurnace customFurnace) {
        String sql = "INSERT INTO " + PLAYERDATA_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(2, customFurnace.getMaterial().name());
            statement.setInt(3, customFurnace.getLevel());
            statement.setInt(4, customFurnace.getLocation().getBlockX());
            statement.setInt(5, customFurnace.getLocation().getBlockY());
            statement.setInt(6, customFurnace.getLocation().getBlockZ());
            statement.setString(7, customFurnace.getLocation().getWorld().getName());

            statement.execute();
        } catch (SQLException e) {
            instance.getLogger().severe("Could not store furnace!");
            e.printStackTrace();
        }
    }

    public Map<Location, CustomFurnace> getFurnaces() {
        String sql = "SELECT * FROM " + PLAYERDATA_TABLE;
        Map<Location, CustomFurnace> customFurnaceMap = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet playerDataSet = statement.executeQuery();

            while (playerDataSet.next()) {
                int x = playerDataSet.getInt("loc_x");
                int y = playerDataSet.getInt("loc_y");
                int z = playerDataSet.getInt("loc_z");
                String worldName = playerDataSet.getString("loc_world");
                World world = Bukkit.getWorld(worldName);
                Location location = new Location(world, x, y, z);
                String type = playerDataSet.getString("type");
                int level = playerDataSet.getInt("level");
                Material material = Material.valueOf(type.toUpperCase());
                CustomFurnace customFurnace = new CustomFurnace(location, material, level);

                customFurnaceMap.put(
                        location, customFurnace
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customFurnaceMap;
    }

    public void clearFurnaces() {
        String sql = "DELETE FROM " + PLAYERDATA_TABLE;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.execute();
        } catch (SQLException e) {
            instance.getLogger().severe("Could not store furnace!");
            e.printStackTrace();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}

