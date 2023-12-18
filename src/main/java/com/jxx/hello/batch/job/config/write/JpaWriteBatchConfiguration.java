package com.jxx.hello.batch.job.config.write;

import com.jxx.hello.domain.WriteCustomerV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;


@Slf4j
//@Configuration
@RequiredArgsConstructor
public class JpaWriteBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jpaWriteJob() {
        return jobBuilderFactory.get("hibernateFormatJob")
                .start(jpaFormatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jpaFormatStep() {
        return this.stepBuilderFactory.get("hibernateFormatStep")
                .<WriteCustomerV2, WriteCustomerV2>chunk(10)
                .reader(customerFileReader(null))
                .writer(jpaItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<WriteCustomerV2> customerFileReader(@Value("#{JobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<WriteCustomerV2>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName","middleInitial","lastName","address","city","state","zip"})
                .targetType(WriteCustomerV2.class)
                .build();

    }

    @Bean
    @StepScope
    public JpaItemWriter<WriteCustomerV2> jpaItemWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<WriteCustomerV2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
