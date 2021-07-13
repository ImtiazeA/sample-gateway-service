package com.musala.test.samplegatewayservice.dtos.peripheral;

import lombok.Data;

@Data
public class CreatePeripheralRequestDTO {

    private String name;

    private String vendor;

    private String gatewayId;

    private String status;

}
