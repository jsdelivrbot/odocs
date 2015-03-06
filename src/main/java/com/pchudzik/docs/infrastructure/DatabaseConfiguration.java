package com.pchudzik.docs.infrastructure;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.SneakyThrows;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
* Created by pawel on 04.01.15.
*/
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DatabaseConfiguration {
	@Autowired
	ApplicationPropertiesConfiguration.DatabaseSettings databaseSettings;

	@SneakyThrows
	@Bean
	DataSource dataSource() {
		final ComboPooledDataSource ds = new ComboPooledDataSource();
		ds.setDriverClass("org.h2.Driver");
		ds.setUser("sa");
		ds.setPassword("");
		ds.setJdbcUrl(databaseSettings.getJdbcUrl());
		return ds;
	}

	@Bean
	EntityManagerFactory entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setPackagesToScan("com.pchudzik.docs");
		factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factoryBean.setJpaPropertyMap(Stream.of(
				entry("hibernate.ejb.naming_strategy", new ImprovedNamingStrategy()),
				entry("hibernate.hbm2ddl.auto", "create"),
				entry("hibernate.dialect", "org.hibernate.dialect.H2Dialect"),
				entry("jadira.usertype.autoRegisterUserTypes", "true"),
				entry("jadira.usertype.javaZone", "UTC"),
				entry("jadira.usertype.databaseZone", "UTC")
		).collect(toMap(entry -> (String) entry.getKey(), Entry::getValue)));
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	@Bean
	PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory());
	}

	private Entry<String, Object> entry(String name, Object value) {
		return new AbstractMap.SimpleEntry<>(name, value);
	}
}
