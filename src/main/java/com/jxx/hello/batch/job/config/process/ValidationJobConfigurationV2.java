package com.jxx.hello.batch.job.config.process;

import com.jxx.hello.domain.CustomerV5;
import com.jxx.hello.batch.job.processor.application.UpperCaseNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class ValidationJobConfigurationV2 {

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
                .processor(itemProcessorAdapter(null))
                .writer(itemWriterV5())
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
    public ItemProcessorAdapter<CustomerV5, CustomerV5> itemProcessorAdapter(UpperCaseNameService service) {
        ItemProcessorAdapter<CustomerV5, CustomerV5> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(service);
        adapter.setTargetMethod("upperCase");

        return adapter;
    }

    @Bean
    public ItemWriter<CustomerV5> itemWriterV5() {
        return (items) -> items.forEach(System.out::println);
    }
}
