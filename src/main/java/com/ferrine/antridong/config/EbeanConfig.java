package com.ferrine.antridong.config;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EbeanConfig {

    @Bean(destroyMethod = "")
    public Database database() {
        // Return the global Ebean database initialized by DatabaseManager
        return com.ferrine.antridong.database.DatabaseManager.getDatabase();
    }
}
