package com.kaiasia.app.core.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ConditionalOnExpression("${spring.database.postgres:true}")
public class PostgreDatabaseConfiguration {

    @Autowired
    Environment env;

    public PostgreDatabaseConfiguration() {
    }

    @Primary
    @Bean({"postgreDataSource"})
    @ConfigurationProperties("spring.postgredatasource")
    public HikariDataSource postgreDataSource() {
        DataSourceProperties a = this.postgreDataSourceProperties();
        return (HikariDataSource)this.postgreDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean({"jdbcTemplatePostgre"})
    public JdbcTemplate jdbcTemplatePostgre(@Qualifier("postgreDataSource") DataSource postgreDataSource) {
        return new JdbcTemplate(postgreDataSource);
    }

    @Bean({"namedParameterJdbcTemplatePostgres"})
    public NamedParameterJdbcTemplate namedParameterJdbcTemplatePostgres(@Qualifier("postgreDataSource") DataSource postgreDataSource) {
        return new NamedParameterJdbcTemplate(postgreDataSource);
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.postgredatasource")
    public DataSourceProperties postgreDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean({"entityManagerFactory"})
    public LocalContainerEntityManagerFactoryBean postgreEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean postgreEm = new LocalContainerEntityManagerFactoryBean();
        postgreEm.setDataSource(this.postgreDataSource());
        String packagesToScan = this.env.getProperty("spring.postgredatasource.packagesToScan");
        if (StringUtils.isEmpty(packagesToScan)) {
            packagesToScan = "com.kaiasia.app.entity";
        }

        String[] pck2Scan = packagesToScan.split(",");
        postgreEm.setPackagesToScan(pck2Scan);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        postgreEm.setJpaVendorAdapter(vendorAdapter);
        postgreEm.setJpaProperties(this.additionalProperties());
        postgreEm.afterPropertiesSet();
        return postgreEm;
    }

    final Properties additionalProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        String showSql = this.env.getProperty("spring.postgredatasource.showSql");
        if (StringUtils.isEmpty(showSql)) {
            showSql = "false";
        }

        hibernateProperties.setProperty("hibernate.show-sql", showSql);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        return hibernateProperties;
    }

    @Bean(
            name = {"transactionManager"}
    )
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(this.postgreEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(
            name = {"sessionFactory"}
    )
    public SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setHibernateProperties(this.additionalProperties());
        factoryBean.afterPropertiesSet();
        SessionFactory sessionFactory = factoryBean.getObject();
        return sessionFactory;
    }

}
