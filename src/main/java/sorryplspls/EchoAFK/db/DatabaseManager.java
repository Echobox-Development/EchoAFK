package sorryplspls.EchoAFK.db;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.io.File;
import java.util.List;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:plugins/EchoAFK/afkstats.db";

    private ConnectionSource connectionSource;
    private Dao<AfkStats, String> afkStatsDao;

    public void initialize() throws SQLException {
        File dataFolder = new File("plugins/EchoAFK");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        connectionSource = new JdbcConnectionSource(DATABASE_URL);

        TableUtils.createTableIfNotExists(connectionSource, AfkStats.class);

        afkStatsDao = DaoManager.createDao(connectionSource, AfkStats.class);
    }

    public Dao<AfkStats, String> getAfkStatsDao() {
        return afkStatsDao;
    }

    public AfkStats getOrCreateStats(String playerUUID) throws SQLException {
        AfkStats stats = afkStatsDao.queryForId(playerUUID);
        if (stats == null) {
            stats = new AfkStats(playerUUID);
            afkStatsDao.create(stats);
        }
        return stats;
    }

    public void updateStats(AfkStats stats) throws SQLException {
        afkStatsDao.update(stats);
    }

    public List<AfkStats> loadAllStats() throws SQLException {
        return afkStatsDao.queryForAll();
    }

    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
