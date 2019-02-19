package bot.sql;

import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceBean {

    @Bean
    @ConfigurationProperties(prefix="bot.datasource")
    public SimpleDataSource dataSource(){
        return JdbcConnectionPool.create();
    }

}
