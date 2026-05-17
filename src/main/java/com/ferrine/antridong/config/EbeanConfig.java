package com.ferrine.antridong.config;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EbeanConfig {

    @Bean
    public Database database() {
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
        config.addPackage("com.ferrine.antridong.model");

        // Create and return the Ebean Database instance
        return DatabaseFactory.create(config);
    }
}
