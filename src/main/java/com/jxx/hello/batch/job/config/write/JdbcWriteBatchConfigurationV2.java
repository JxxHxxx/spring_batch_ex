package com.jxx.hello.batch.job.config.write;

import com.jxx.hello.batch.job.writer.WriterCustomerItemPreparedStatementSetter;
import com.jxx.hello.domain.WriteCustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcWriteBatchConfigurationV2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job JdbcWriteJob() {
        return jobBuilderFactory.get("JdbcWriteJob")
                .start(JdbcWriteStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step JdbcWriteStep() {
        return stepBuilderFactory.get("JdbcWriteStep")
                .<WriteCustomer, WriteCustomer>chunk(10)
                .reader(customerFileReaderV2(null))
                .writer(jdbcCustomerWriter(dataSource))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<WriteCustomer> customerFileReaderV2(@Value("#{JobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<WriteCustomer>()
                .name("customerFileReaderV2")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName","middleInitial","lastName","address","city","state","zip"})
                .targetType(WriteCustomer.class)
                .build();

    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<WriteCustomer> jdbcCustomerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<WriteCustomer>()
                .dataSource(dataSource)
                .sql("INSERT INTO WRITE_CUSTOMER (first_name, middle_initial, last_name, address, city, state, zip) " +
                        "VALUES (:firstName,:middleInitial,:lastName,:address,:city,:state,:zip)")
                .beanMapped()
                .build();
    }
}
