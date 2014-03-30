/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.app;

import com.freedomotic.api.API;
import com.freedomotic.api.APIStandardImpl;
import com.freedomotic.api.Plugin;
import com.freedomotic.bus.BusService;
import com.freedomotic.bus.impl.BusServiceImpl;
import com.freedomotic.environment.EnvironmentDAOXstream;
import com.freedomotic.environment.EnvironmentPersistence;
import com.freedomotic.model.ds.Config;
import com.freedomotic.objects.EnvObjectPersistence;
import com.freedomotic.plugins.ClientStorage;
import com.freedomotic.plugins.ClientStorageInMemory;
import com.freedomotic.reactions.TriggerPersistence;
import com.freedomotic.security.Auth;
import com.freedomotic.security.AuthImpl;
import com.freedomotic.util.I18n.I18n;
import com.freedomotic.util.I18n.I18nImpl;
import com.freedomotic.util.Info;
import java.io.File;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 *
 * @author nicoletti
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.freedomotic"})
@ComponentScan(basePackages = {"com.freedomotic"})
@EnableTransactionManagement
public class SpringConfig implements TransactionManagementConfigurer {

    private String driver = "org.postgresql.Driver";

    private String url = "jdbc:postgresql://localhost/freedomotic";

    private String username = "nicoletti";

    private String password = "nicoletti";

    private String dialect = "org.hibernate.dialect.PostgreSQLDialect";

    private String hbm2ddlAuto = "create";

    @Bean
    public Freedomotic freedomotic() {
        return new Freedomotic();
    }

    @Bean
    public ClientStorage clientStorage() {
        return new ClientStorageInMemory();
    }

    @Bean
    public AppConfig appConfig() {
        AppConfigImpl appConfigImpl = new AppConfigImpl();
        appConfigImpl.load();
        return appConfigImpl;
    }

    @Bean
    public Config config() {
        return new Config();
    }

    @Bean
    public BusService busService() {
        BusServiceImpl busServiceImpl = new BusServiceImpl();
        busServiceImpl.init();
        return busServiceImpl;
    }

//    @Bean
//    public BusMessagesListener busMessagesListener() {
//        BusMessagesListener listener = new BusMessagesListener(busService(), this);
//        return listener;
//    }

    @Bean
    public Auth auth() {
        return new AuthImpl();
    }

    @Bean
    public TriggerPersistence triggerPersistence() {
        return new TriggerPersistence();
    }

//    @Bean
//    public Config configPersistence(File manifest) {
//        try {
//            return ConfigPersistence.deserialize(manifest);
//        } catch (IOException ex) {
//            Logger.getLogger(SpringConfig.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ConversionException ex) {
//            Logger.getLogger(SpringConfig.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    @Bean
//    public PluginsManager pluginsManager() {
//        return new PluginsManagerImpl();
//    }
    
        @Bean
    public Plugin plugin() {
        return new Plugin();
    }

    @Bean
    public API api() {
        return new APIStandardImpl();
    }

    @Bean
    public I18n i18n() {
        return new I18nImpl(appConfig());
    }

    @Bean
    public EnvironmentDAOXstream environmentDAO() {
        return new EnvironmentDAOXstream(new File(Info.PATH_DATA_FOLDER + "/furn/default/"));
    }

    @Bean
    public EnvironmentPersistence environmentPersistence() {
        return new EnvironmentPersistence(clientStorage());
    }

    @Bean
    public EnvObjectPersistence envObjectPersistence() {
        return new EnvObjectPersistence();
    }

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setLocation(new ClassPathResource("/persistence.properties"));
        return ppc;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource configureDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(configureDataSource());
        entityManagerFactory.setPackagesToScan("com.freedomotic");
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, dialect);
        jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, hbm2ddlAuto);
        entityManagerFactory.setJpaProperties(jpaProperties);

        return entityManagerFactory;
    }

    @Bean(name = "transactionManager")
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new JpaTransactionManager();
    }
}
