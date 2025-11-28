package com.onboarding.admin.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.onboarding.admin.repository.kyc",
    entityManagerFactoryRef = "kycEntityManagerFactory",
    transactionManagerRef = "kycTransactionManager"
)
public class KycDataSourceConfig {

    @Value("${spring.datasource.kyc.jdbc-url}")
    private String kycJdbcUrl;
    
    @Value("${spring.datasource.kyc.username}")
    private String kycUsername;
    
    @Value("${spring.datasource.kyc.password}")
    private String kycPassword;

    @Bean(name = "kycDataSource")
    @ConfigurationProperties("spring.datasource.kyc.hikari")
    public DataSource kycDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(kycJdbcUrl);
        dataSource.setUsername(kycUsername);
        dataSource.setPassword(kycPassword);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }

    @Bean(name = "kycEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean kycEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("kycDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        // Use standard naming - no conversion, use exact field names as column names
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        properties.put("hibernate.implicit_naming_strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");
        
        return builder
                .dataSource(dataSource)
                .packages("com.onboarding.admin.entity.kyc")
                .persistenceUnit("kyc")
                .properties(properties)
                .build();
    }

    @Bean(name = "kycTransactionManager")
    public PlatformTransactionManager kycTransactionManager(
            @Qualifier("kycEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
