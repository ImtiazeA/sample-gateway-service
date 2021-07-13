package com.musala.test.samplegatewayservice.dtos.gateway;

import com.musala.test.samplegatewayservice.validation.annotations.IpV4Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGatewayDTO {
    private String name;

    @IpV4Address
    private String ipV4Address;
}
