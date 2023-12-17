package com.jxx.hello.batch.job.config;

import com.jxx.hello.domain.CustomerV4;
import com.jxx.hello.batch.job.reader.itemreader.CustomerFileReaderV4;
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
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.util.HashMap;

/**
 * 레코드 포맷이 N개인 파일 처리
 */


@Slf4j
//@Configuration
@RequiredArgsConstructor
public class BatchConfigurationV4 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final FieldSetMapper transactionFieldSetMapper;
    private final FieldSetMapper customerV4FieldSetMapper;

    private final LineTokenizer transactionLineTokenizer;
    private final LineTokenizer customerFileLineTokenizer;


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
                .<CustomerV4, CustomerV4>chunk(10)
//                .reader(customerItemReader(null))
                .reader(customerFileReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader customerItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<CustomerV4>()
                .name("customerItemReader")
                .resource(inputFile)
                .lineMapper(lineMapper())
                .build();
    }

    /**
     * customerItemReader bean 을 래핑한 bean
     */

    @Bean
    public CustomerFileReaderV4 customerFileReader() {
        return new CustomerFileReaderV4(customerItemReader(null));
    }

    @Bean
    public PatternMatchingCompositeLineMapper lineMapper() {
        HashMap<String, LineTokenizer> lineTokenizers = new HashMap<>(2);

        lineTokenizers.put("CUST*", customerFileLineTokenizer);
        lineTokenizers.put("TRANS*", transactionLineTokenizer);

        HashMap<String, FieldSetMapper> fieldSetMappers = new HashMap<>(2);


        fieldSetMappers.put("CUST*", customerV4FieldSetMapper);
        fieldSetMappers.put("TRANS*", transactionFieldSetMapper);

        PatternMatchingCompositeLineMapper lineMappers = new PatternMatchingCompositeLineMapper<>();
        lineMappers.setTokenizers(lineTokenizers);
        lineMappers.setFieldSetMappers(fieldSetMappers);

        return lineMappers;
    }

    @Bean
    public ItemWriter<Object> itemWriter() {
        return items -> items.forEach(item -> log.info("item : {}" , item));
    }
}