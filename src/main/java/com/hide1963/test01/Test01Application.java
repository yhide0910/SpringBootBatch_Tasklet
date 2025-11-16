package com.hide1963.test01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;
import java.nio.file.Paths;

@SpringBootApplication
@MapperScan("com.hide1963.test01.mapper")
public class Test01Application {

	@Autowired
	private Environment env;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job test01Job;

	public static void main(String[] args) {
		SpringApplication.run(Test01Application.class, args);
	}

	@Bean
	public CommandLineRunner runBatchJob() {
		return _ -> {
			// デバッグ: 設定値を確認
			System.out.println("=== Configuration Check ===");
			System.out.println("batch.metadata.datasource.url: " + env.getProperty("batch.metadata.datasource.url"));
			System.out.println("spring.datasource.url: " + env.getProperty("spring.datasource.url"));

			// H2データベースファイルのフルパスを表示
			String dbPath = Paths.get("./data/batch_metadata.mv.db").toAbsolutePath().toString();
			System.out.println("H2 Database Full Path: " + dbPath);
			System.out.println("===========================");

			System.out.println("Starting batch job...");
			JobParameters params = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(test01Job, params);
		};
	}

}
