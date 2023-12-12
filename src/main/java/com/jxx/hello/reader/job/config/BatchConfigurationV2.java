package com.jxx.hello.reader.job.config;

import com.jxx.hello.reader.domain.Customer;
import com.jxx.hello.reader.job.reader.tokenizer.CustomerFileLineTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

/**
 * 구분자로 구분된 파일
 */


@Slf4j
//@Configuration
@RequiredArgsConstructor
public class BatchConfigurationV2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final CustomerFileLineTokenizer lineTokenizer;

    @Bean
    public Job job() {
        log.info("start job");
        return jobBuilderFactory.get("job")
                .start(copyFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step copyFileStep() {
        log.info("start step");
        return stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null))
                .writer(itemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReaderV2(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(inputFile)
                .lineTokenizer(lineTokenizer)
                .targetType(Customer.class)
                .strict(false)
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(inputFile)
                .delimited() // DelimitedLineTokenizer
                .names(new String[]{"firstName", "middleInitial", "lastName", "addressNumber",
                        "street", "city", "State", "zipCode"}) // Tokenizer 추가 설정
                .targetType(Customer.class)
                .strict(false)
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return items -> items.forEach(customer -> log.info("item : {}" , customer));
    }
}