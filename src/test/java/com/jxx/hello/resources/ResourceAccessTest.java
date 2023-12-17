package com.jxx.hello.resources;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;


import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class ResourceAccessTest {

    @Test
    void find_resource_by_class_path() throws IOException {
        ClassPathResource resource = new ClassPathResource("customer.txt");

        log.info("=======================================================");
        log.info("resource URL {}", resource.getURL());
        log.info("resource Name {}", resource.getFilename());
        log.info("resource path {}", resource.getPath());
        log.info("=======================================================");

        assertThat(resource.exists()).isTrue();
    }
}
