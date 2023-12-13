package com.jxx.hello.reader.job.config;

import com.jxx.hello.reader.domain.CustomerV5;
import com.jxx.hello.reader.job.processor.UniqueLastNameValidator;
import com.jxx.hello.reader.job.processor.application.UpperCaseNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ValidationJobConfigurationV3 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean(name = "validationJob")
    public Job validationJob() throws Exception {
        return jobBuilderFactory.get("validationJob")
                .start(copyFileStepV5())
                .build();
    }

    @Bean
    public Step copyFileStepV5() {
        return stepBuilderFactory.get("copyFileJobV2")
                .<CustomerV5, CustomerV5> chunk(2)
                .reader(customerItemReaderV5(null))
                .processor(compositeItemProcessor())
                .writer(itemWriterV5())
                .stream(uniqueLastNameValidator())
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
    public CompositeItemProcessor<CustomerV5, CustomerV5> compositeItemProcessor() {
        CompositeItemProcessor<CustomerV5, CustomerV5> compositeItemProcessor = new CompositeItemProcessor<>();
        List<? extends ItemProcessor<CustomerV5, CustomerV5>> itemProcessors = List.of(validatingItemProcessor(), itemProcessorAdapter(null));
//        List<? extends ItemProcessor<CustomerV5, CustomerV5>> itemProcessors = List.of(itemProcessorAdapter(null), validatingItemProcessor());

        compositeItemProcessor.setDelegates(itemProcessors);

        return compositeItemProcessor;
    }

    @Bean
    public ItemProcessorAdapter<CustomerV5, CustomerV5> itemProcessorAdapter(UpperCaseNameService service) {
        ItemProcessorAdapter<CustomerV5, CustomerV5> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(service);
        adapter.setTargetMethod("upperCase");

        return adapter;
    }

    @Bean
    public ValidatingItemProcessor<CustomerV5> validatingItemProcessor() {
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
