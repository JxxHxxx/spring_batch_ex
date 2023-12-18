package com.jxx.hello.batch.job.config.write;

import com.jxx.hello.domain.WriteCustomerV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 구성이 잘 안됨 일단 스킵
 */

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class HibernateWriteBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job JdbcWriteJob() {
        return jobBuilderFactory.get("hibernateFormatJob")
                .start(hibernateFormatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step hibernateFormatStep() {
        return this.stepBuilderFactory.get("hibernateFormatStep")
                .<WriteCustomerV2, WriteCustomerV2>chunk(10)
                .reader(customerFileReader(null))
                .writer(hibernateItemWriter(null))
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
    public HibernateItemWriter<WriteCustomerV2> hibernateItemWriter(EntityManagerFactory entityManager) {
        return new HibernateItemWriterBuilder<WriteCustomerV2>()
                .sessionFactory(entityManager.unwrap(SessionFactory.class))
                .build();

    }
}
