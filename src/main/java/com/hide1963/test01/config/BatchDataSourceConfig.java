package com.hide1963.test01.config;

import javax.sql.DataSource;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.hide1963.test01.config.props.BatchMetadataDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Configuration
@EnableConfigurationProperties(BatchMetadataDataSourceProperties.class)
@SuppressWarnings("null")
public class BatchDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchDataSourceConfig.class);

    // アプリ用 DataSourceProperties
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties applicationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource applicationDataSource() {
        return applicationDataSourceProperties().initializeDataSourceBuilder().build();
    }

    // バッチメタデータ用 DataSource（H2 ファイルDB）
    @Bean("batchMetadataDataSource")
    @Order(1) // 優先的に作成
    public DataSource batchMetadataDataSource(BatchMetadataDataSourceProperties props) {
        log.info("=== Creating Batch Metadata DataSource ===");
        log.info("URL: {}", props.getUrl());
        log.info("Username: {}", props.getUsername());
        log.info("Driver: {}", props.getDriverClassName());

        HikariDataSource ds = props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        var h = props.getHikari();
        if (h.getMaximumPoolSize() != null)
            ds.setMaximumPoolSize(h.getMaximumPoolSize());
        if (h.getMinimumIdle() != null)
            ds.setMinimumIdle(h.getMinimumIdle());
        if (h.getPoolName() != null)
            ds.setPoolName(h.getPoolName());
        if (h.getIdleTimeout() != null)
            ds.setIdleTimeout(h.getIdleTimeout());

        log.info("Hikari Pool Name: {}", ds.getPoolName());
        log.info("JDBC URL: {}", ds.getJdbcUrl());
        log.info("==========================================");

        return ds;
    }

    @Bean("batchTransactionManager")
    @Order(2)
    public PlatformTransactionManager batchTransactionManager(
            @Qualifier("batchMetadataDataSource") DataSource batchMetadataDataSource) {
        log.info("=== Creating Batch TransactionManager with H2 ===");
        return new DataSourceTransactionManager(batchMetadataDataSource);
    }

    @Bean
    @Order(3)
    public JobRepository jobRepository(
            @Qualifier("batchMetadataDataSource") DataSource batchMetadataDataSource,
            @Qualifier("batchTransactionManager") PlatformTransactionManager batchTransactionManager) throws Exception {
        log.info("=== Creating JobRepository with H2 ===");

        // 追加: 既存テーブルの存在チェック（存在すればスキーマ投入をスキップ）
        if (!batchTablesExist(batchMetadataDataSource)) {
            log.info("Spring Batch metadata tables not found. Initializing schema for H2...");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-h2.sql"));
            populator.setContinueOnError(true); // 既存エラー等があっても続行
            DatabasePopulatorUtils.execute(populator, batchMetadataDataSource);
            log.info("Spring Batch metadata tables created successfully");
        } else {
            log.info("Spring Batch metadata tables already exist. Skipping schema initialization.");
        }

        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(batchMetadataDataSource);
        factory.setTransactionManager(batchTransactionManager);
        factory.afterPropertiesSet();
        log.info("JobRepository created successfully with H2");
        return factory.getObject();
    }

    // 追加: メタデータテーブル存在チェック
    private boolean batchTablesExist(DataSource dataSource) {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData md = con.getMetaData();
            try (ResultSet rs = md.getTables(null, null, "BATCH_JOB_INSTANCE", null)) {
                return rs.next();
            }
        } catch (Exception e) {
            log.warn("Failed to check existing Spring Batch tables. Falling back to always initialize.", e);
            return false;
        }
    }

    @Bean
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(4);
        exec.setQueueCapacity(16);
        exec.setThreadNamePrefix("batch-");
        exec.initialize();
        return exec;
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository, TaskExecutor batchTaskExecutor) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(batchTaskExecutor);
        launcher.afterPropertiesSet();
        return launcher;
    }

}
