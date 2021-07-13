package com.musala.test.samplegatewayservice.dtos.peripheral;

import lombok.Data;

@Data
public class PeripheralResponseDTO {

    private Long id;

    private String name;

    private String vendor;

    private String gatewayId;

    private String status;

}
