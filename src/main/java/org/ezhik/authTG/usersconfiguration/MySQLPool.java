package org.ezhik.authTG.usersconfiguration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public final class MySQLPool {
    private final HikariDataSource dataSource;

    public MySQLPool(String host, String database, String user, String pass,
                     int maximumPoolSize, long connectionTimeoutMs, long idleTimeoutMs, long maxLifetimeMs,
                     String jdbcParams) {

        String jdbcUrl = "jdbc:mysql://" + host + "/" + database;
        if (jdbcParams != null && !jdbcParams.isBlank()) {
            if (!jdbcParams.startsWith("?")) jdbcParams = "?" + jdbcParams;
            jdbcUrl += jdbcParams;
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

        cfg.setMaximumPoolSize(Math.max(1, maximumPoolSize));
        cfg.setConnectionTimeout(Math.max(1000, connectionTimeoutMs));
        cfg.setIdleTimeout(Math.max(1000, idleTimeoutMs));
        cfg.setMaxLifetime(Math.max(1000, maxLifetimeMs));
        cfg.setPoolName("AuthTG-Hikari");

        this.dataSource = new HikariDataSource(cfg);
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public void close() {
        dataSource.close();
    }
}
