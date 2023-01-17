package com.flz.downloadandupload;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJdbcRepositories(basePackages = "com.flz.downloadandupload")
@MapperScan(basePackages = {"com.flz.downloadandupload.persist.repository.mapper"})
public class SpringbootDownloadAndUploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDownloadAndUploadApplication.class, args);
	}

}
