package com.green.machine.disposer.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jurol on 3/20/2018.
 */
public final class DataSource {
    protected final static String CLASS_FOR_NAME = "com.mysql.cj.jdbc.Driver";
    protected final static String HOST_NAME = "jdbc:mysql://localhost:3306";
    protected String USER = "greenmachine";
    protected String PASS = "jerjer360";

    protected HikariDataSource hikariDataSource;
    protected static DataSource dataSource;

    private DataSource() {
        hikariDataSource = new HikariDataSource(hikariConfig());
        hikariDataSource.setInitializationFailFast(true);
    }

    public static DataSource getInstance(){
        if(dataSource == null){
            dataSource = new DataSource();
        }
        return dataSource;
    }

    public Connection getConnection() throws SQLException, IOException, PropertyVetoException {
        return dataSource.getConnection();
    }

    private HikariConfig hikariConfig(){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(CLASS_FOR_NAME);
        config.setJdbcUrl(HOST_NAME);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setLeakDetectionThreshold(TimeUnit.MINUTES.toMillis(1));
        config.setValidationTimeout(TimeUnit.MINUTES.toMillis(1));
        config.setMaximumPoolSize(40);
        config.setMinimumIdle(0);
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(5)); // 120 seconds max life time
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(1)); // minutes
        config.setConnectionTimeout(TimeUnit.MINUTES.toMillis(1)); // millis
        config.setConnectionTestQuery("/* ping */ SELECT 1");
        return config;
    }
}
