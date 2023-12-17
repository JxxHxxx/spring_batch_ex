package com.jxx.hello.batch.job.processor.application;

import com.jxx.hello.domain.CustomerV5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * itemProcessAdapter 에서 사용하는 클래스
 * reflection 사용
 */

@Slf4j
@Service
public class UpperCaseNameService {

    public CustomerV5 upperCase(CustomerV5 customer) {
        log.info("UpperCaseNameService call customer {}", customer.getLastName());
        CustomerV5 newCustomer = new CustomerV5(customer);

        newCustomer.setFirstName(newCustomer.getFirstName().toUpperCase());
        newCustomer.setMiddleInitial(newCustomer.getMiddleInitial().toUpperCase());
        newCustomer.setLastName(newCustomer.getLastName().toUpperCase());

        return newCustomer;
    }
}
