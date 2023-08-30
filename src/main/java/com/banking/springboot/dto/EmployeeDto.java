package com.banking.springboot.dto;

import lombok.Data;
import lombok.Generated;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
@Generated
public class EmployeeDto {
    private Integer id;

    @Length(min = 2, max = 45, message = "First name must be between 2 and 45 characters long.")
    private String firstName;

    @Length(min = 2, max = 45, message = "Last name must be between 2 and 45 characters long.")
    private String lastName;

    @NotEmpty(message = "Email is required.")
    @Length(max = 256, message = "Email must be less than 256 characters.")
    private String email;

    private String startDate;

    @NotEmpty(message = "Title is required.")
    @Length(max = 45, message = "Title must be less than 45 characters.")
    private String title;

    @NotEmpty(message = "Branch is required.")
    private String branch;

    private String department;

    private String password;
}
