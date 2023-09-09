package net.monsterhit.zbackhome;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static Database instance;

    private Database() {
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    File f = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("HuskHomes")).getDataFolder(), "config.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
    private final String mySqlHost = config.getString("database.mysql.credentials.host");
    private final String mySqlPort = config.getString("database.mysql.credentials.port");
    private final String mySqlDatabase = config.getString("database.mysql.credentials.database");
    private final String mySqlUsername = config.getString("database.mysql.credentials.username");
    private final String mySqlPassword = config.getString("database.mysql.credentials.password");
    private final String url = "jdbc:mysql://" + mySqlHost + ":" + mySqlPort + "/" + mySqlDatabase;

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private final long CACHE_DURATION = 30000;

    public String getLatestServerNameFromDatabase(UUID playerUUID) {
        try {
            Connection connection = DriverManager.getConnection(url, mySqlUsername, mySqlPassword);
            Statement statement = connection.createStatement();
            String lastPosition = "SELECT last_position FROM huskhomes_users WHERE uuid = '" + playerUUID.toString() + "'";
            ResultSet lastPositionResult = statement.executeQuery(lastPosition);
            int lastPositionId = -1;
            if (lastPositionResult.next()) {
                lastPositionId = lastPositionResult.getInt("last_position");
            }

            if (lastPositionId != -1) {
                String query = "SELECT server_name FROM huskhomes_position_data WHERE id = '" + lastPositionId + "' ORDER BY id DESC LIMIT 1";
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    return resultSet.getString("server_name");
                }

                resultSet.close();
            }

            lastPositionResult.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String searchServerName(UUID playerUUID) {
        String playerUUIDStr = String.valueOf(playerUUID);

        if(cache.containsKey(playerUUIDStr)) {
            long difference = System.currentTimeMillis() - cacheTimestamps.get(playerUUIDStr);
            if(difference < CACHE_DURATION) {
                return cache.get(playerUUIDStr);
            } else {
            }
        }

        // Se l'UUID non è nella cache, cerca l'ultimo server_name nel database
        String serverName = getLatestServerNameFromDatabase(playerUUID);

        if (serverName != null) {
            cache.put(playerUUIDStr, serverName);
            cacheTimestamps.put(playerUUIDStr, System.currentTimeMillis());
        }

        return serverName;
    }
}
