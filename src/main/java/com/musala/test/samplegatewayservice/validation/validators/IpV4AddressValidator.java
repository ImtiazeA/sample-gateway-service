package com.musala.test.samplegatewayservice.validation.validators;

import com.musala.test.samplegatewayservice.validation.annotations.IpV4Address;
import org.apache.commons.validator.routines.InetAddressValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IpV4AddressValidator implements ConstraintValidator<IpV4Address, String> {

    private static final InetAddressValidator validator = InetAddressValidator.getInstance();

    @Override
    public boolean isValid(String ipV4Address, ConstraintValidatorContext constraintValidatorContext) {

        return validator.isValid(ipV4Address);

    }
}