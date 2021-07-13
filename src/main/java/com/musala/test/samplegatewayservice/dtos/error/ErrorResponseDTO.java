package com.musala.test.samplegatewayservice.dtos.error;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorResponseDTO {

    private String message;

    private List<ErrorDetails> errors = new ArrayList<>();

}
