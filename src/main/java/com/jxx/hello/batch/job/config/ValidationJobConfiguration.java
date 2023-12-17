package com.jxx.hello.batch.job.config;

import com.jxx.hello.domain.CustomerV5;
import com.jxx.hello.batch.job.processor.UniqueLastNameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class ValidationJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean(name = "validationJob")
    public Job validationJob() throws Exception {
        return jobBuilderFactory.get("validationJob")
                .start(copyFileStepV5())
//                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step copyFileStepV5() {
        return stepBuilderFactory.get("copyFileJobV2")
                .<CustomerV5, CustomerV5> chunk(2)
                .reader(customerItemReaderV5(null))
                .processor(customerV5BeanValidatingItemProcessor())
                .writer(itemWriterV5())
                .stream(uniqueLastNameValidator()) // 재시작 시 사용되는 듯함.
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerV5> customerItemReaderV5(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<CustomerV5>()
                .name("customerItemReaderV5")
                .delimited()
                .names(new String[]{"firstName", "middleInitial", "lastName", "addressNumber",
                        "street", "city", "state", "zipcode"})
                .targetType(CustomerV5.class)
                .resource(inputFile)
                .build();
    }

    @Bean
    public ValidatingItemProcessor<CustomerV5> customerV5BeanValidatingItemProcessor() {
        return new ValidatingItemProcessor<>(uniqueLastNameValidator());
    }

    @Bean
    public UniqueLastNameValidator uniqueLastNameValidator() {
        UniqueLastNameValidator uniqueLastNameValidator = new UniqueLastNameValidator();
        uniqueLastNameValidator.setName("uniqueLastNameValidator");
        return uniqueLastNameValidator;
    }

    @Bean
    public ItemWriter<CustomerV5> itemWriterV5() {
        return (items) -> items.forEach(System.out::println);
    }
}
