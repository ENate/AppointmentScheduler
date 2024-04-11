package com.example.slabiak.appointmentscheduler.validation;

// import com.example.slabiak.appointmentscheduler.entity.user.User;
import com.example.slabiak.appointmentscheduler.service.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// import org.springframework.beans.factory.annotation.Autowired;

//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, Object> {


    private UserService userService;

    public UniqueUsernameValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(final UniqueUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        String userName = (String) obj;
        return !userService.userExists(userName);
    }

}