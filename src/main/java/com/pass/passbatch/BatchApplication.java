package com.pass.passbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BatchApplication {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    public BatchApplication(final JobBuilderFactory jobBuilderFactory, final StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step passStep() {
        return this.stepBuilderFactory.get("passStep").tasklet((contribution, chunkContext) -> {
            System.out.println("Excute PassStep");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Job passJob() {
        return this.jobBuilderFactory.get("passJob").start(passStep()).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

}
