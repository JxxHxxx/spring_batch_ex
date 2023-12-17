package com.jxx.hello.batch.job.config.write;

import com.jxx.hello.domain.WriteCustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class FileWriteBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job formatJob() {
        return jobBuilderFactory.get("formatJob")
                .start(formatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step formatStep() {
        return stepBuilderFactory.get("formatStep")
                .<WriteCustomer, WriteCustomer>chunk(10)
                .reader(customerFileReader(null))
                .writer(customerItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<WriteCustomer> customerFileReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<WriteCustomer>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName","middleInitial","lastName","address","city","state","zip"})
                .targetType(WriteCustomer.class)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<WriteCustomer> customerItemWriter(@Value("#{jobParameters['outputFile']}") Resource outputFile) {

        return new FlatFileItemWriterBuilder<WriteCustomer>()
                .name("customerItemWriter")
                .resource(outputFile)
                .formatted()
                .format("%s %s lives at %s %s in %s, %s.")
                .names(new String[]{"firstName", "lastName","address","city","state","zip"})
                .build();
    }
}
