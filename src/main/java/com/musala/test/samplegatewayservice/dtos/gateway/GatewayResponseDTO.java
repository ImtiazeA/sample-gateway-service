package com.musala.test.samplegatewayservice.dtos.gateway;

import com.musala.test.samplegatewayservice.dtos.peripheral.PeripheralResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GatewayResponseDTO {

    private String id;

    private String name;

    private String ipV4Address;

    private List<PeripheralResponseDTO> peripherals = new ArrayList<>();

}
