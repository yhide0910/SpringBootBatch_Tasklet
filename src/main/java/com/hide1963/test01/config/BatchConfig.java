package com.hide1963.test01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import com.hide1963.test01.tasklet.Test01Tasklet;

import org.springframework.batch.core.launch.support.RunIdIncrementer;

@Configuration
@SuppressWarnings("null")
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final Test01Tasklet tasklet;

    public BatchConfig(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            Test01Tasklet tasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.tasklet = tasklet;
    }

    @Bean
    public Job test01Job() {
        return new JobBuilder("test01Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(test01Step())
                .build();
    }

    @Bean
    public Step test01Step() {
        return new StepBuilder("test01Step", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}