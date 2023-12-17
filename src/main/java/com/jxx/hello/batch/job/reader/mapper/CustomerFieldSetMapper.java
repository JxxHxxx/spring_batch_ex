package com.jxx.hello.batch.job.reader.mapper;

import com.jxx.hello.domain.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    /**
     *     private String firstName;
     *     private String middleInitial;
     *     private String lastName;
     *     private String addressNumber;
     *     private String street;
     *     private String city;
     *     private String state;
     *     private String zipcode;
     */

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        String firstName = fieldSet.readString("firstName");
        String middleInitial = fieldSet.readString("middleInitial");
        String lastName = fieldSet.readString("lastName");
        String addressNumber = fieldSet.readString("addressNumber");
        String street = fieldSet.readString("street");
        String city = fieldSet.readString("city");
        String state = fieldSet.readString("state");
        String zipcode = fieldSet.readString("zipcode");

        return new Customer(firstName, middleInitial, lastName, addressNumber, street, city, state, zipcode);
    }
}
