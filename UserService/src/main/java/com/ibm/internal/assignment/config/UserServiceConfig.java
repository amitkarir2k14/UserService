package com.ibm.internal.assignment.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.ibm.internal.assignment.repository.UserRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
//@ComponentScan(basePackages = { "com.ibm.internal.assignment" })
public class UserServiceConfig {

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
		ds.setUrl("jdbc:db2://localhost:50000/USERDB");
		ds.setUsername("amit");
		ds.setPassword("amit1976$");

		return ds;
	}

	private Properties additionalProperties() {
		Properties properties = new Properties();
//		properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		properties.setProperty("show_sql","true");
		return properties;
	}

	/*
	 * @Bean public JpaVendorAdapter jpaVendorAdapter() { OpenJpaVendorAdapter
	 * adapter = new OpenJpaVendorAdapter();
	 * adapter.setDatabase(Database.MYSQL); adapter.setShowSql(true);
	 * adapter.setGenerateDdl(false); return adapter; }
	 */

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
		emfb.setDataSource(dataSource);
		emfb.setJpaVendorAdapter(jpaVendorAdapter);
		emfb.setPackagesToScan("com.ibm.internal.assignment.entity");
		 emfb.setJpaProperties(additionalProperties());
		return emfb;
	}

	// @Bean
	// public JndiObjectFactoryBean entityManagerFactory() {
	// JndiObjectFactoryBean jndiObjectFB = new JndiObjectFactoryBean();
	// jndiObjectFB.setJndiName("jdbc/SpittrDS");
	// return jndiObjectFB;
	// }
}
