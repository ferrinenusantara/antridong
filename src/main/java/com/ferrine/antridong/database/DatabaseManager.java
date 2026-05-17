package com.ferrine.antridong.database;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;

public class DatabaseManager {
    private static Database database;

    public static synchronized void init() {
        if (database != null) {
            return;
        }

        DatabaseConfig config = new DatabaseConfig();
        config.setName("db");
        config.setDefaultServer(true);

        // DataSource configuration for SQLite
        DataSourceConfig ds = new DataSourceConfig();
        ds.setUrl("jdbc:sqlite:antridong.db");
        ds.setDriver("org.sqlite.JDBC");
        ds.setUsername("sa");
        ds.setPassword("");
        config.setDataSourceConfig(ds);

        // Auto DDL Generation and Migration
        config.setDdlGenerate(true);
        config.setDdlRun(true);
        config.setDdlHeader("-- Auto-Generated DDL for SQLite --");

        // Register package for scanning Ebean model entities
        config.addPackage("com.ferrine.antridong.database.models");

        // Create the Ebean Database instance
        database = DatabaseFactory.create(config);
    }

    public static Database getDatabase() {
        if (database == null) {
            init();
        }
        return database;
    }
}
