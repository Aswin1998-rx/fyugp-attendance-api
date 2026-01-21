package com.fyugp.fyugp_attendance_api;

import com.fyugp.fyugp_attendance_api.repositories.factory.CustomQueryExecutorFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class.
 */
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomQueryExecutorFactoryBean.class)
@SpringBootApplication
@ConfigurationPropertiesScan
public class FyugpAttendanceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FyugpAttendanceApiApplication.class, args);
	}

}
