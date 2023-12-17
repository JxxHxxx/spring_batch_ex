package com.jxx.hello.domain;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerV5 {

    @NotNull(message = "First name is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "First name must be alphabetical")
    private String firstName;

    @Size(min = 1, max = 1)
    @Pattern(regexp = "[a-zA-Z]+", message = "Middle initial must be alphabetical")
    private String middleInitial;

    @NotNull(message = "Last name is required")
    @Pattern(regexp = "[a-zA-Z]+", message = "Last name must be alphabetical")
    private String lastName;

    @NotNull(message = "address Number is required")
    @Pattern(regexp = "[0-9]+", message = "address Number must be number")
    private String addressNumber;

    @NotNull(message = "street is required")
    @Pattern(regexp = "[0-9a-zA-Z\\. ]+", message = "street must be alphabetical")
    private String street;

    @NotNull(message = "city is required")
    @Pattern(regexp = "[a-zA-Z\\. ]+", message = "city must be alphabetical")
    private String city;

    @NotNull(message = "state is required")
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[A-Z]{2}", message = "state must be alphabetical")
    private String state;

    @NotNull(message = "zipcode is required")
    @Size(min = 5, max = 5)
    @Pattern(regexp = "\\d{5}", message = "zipcode must be alphabetical")
    private String zipcode;

    public CustomerV5(CustomerV5 customer) {
        this.firstName = customer.firstName;
        this.middleInitial = customer.middleInitial;
        this.lastName = customer.lastName;
        this.addressNumber = customer.addressNumber;
        this.street = customer.street;
        this.city = customer.city;
        this.state = customer.state;
        this.zipcode = customer.zipcode;
    }
}
