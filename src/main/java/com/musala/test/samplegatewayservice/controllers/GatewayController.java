package com.musala.test.samplegatewayservice.controllers;

import com.musala.test.samplegatewayservice.dtos.gateway.CreateGatewayDTO;
import com.musala.test.samplegatewayservice.dtos.gateway.GatewayResponseDTO;
import com.musala.test.samplegatewayservice.dtos.peripheral.CreatePeripheralRequestDTO;
import com.musala.test.samplegatewayservice.dtos.peripheral.PeripheralResponseDTO;
import com.musala.test.samplegatewayservice.models.Gateway;
import com.musala.test.samplegatewayservice.models.Peripheral;
import com.musala.test.samplegatewayservice.services.GatewayService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final ModelMapper modelMapper;
    private final GatewayService gatewayService;

    @Autowired
    public GatewayController(ModelMapper modelMapper, GatewayService gatewayService) {
        this.modelMapper = modelMapper;
        this.gatewayService = gatewayService;
    }

    @PostMapping("")
    @ResponseStatus(code = HttpStatus.CREATED)
    public GatewayResponseDTO createGateway(@Valid @RequestBody CreateGatewayDTO createGatewayDTO) {

        Gateway gateway = gatewayService.createGateway(createGatewayDTO);

        GatewayResponseDTO gatewayResponseDTO = modelMapper.map(gateway, GatewayResponseDTO.class);

        return gatewayResponseDTO;

    }

    @GetMapping("")
    public Page<GatewayResponseDTO> getGateways(@RequestParam int size, @RequestParam int page) {

        Page<Gateway> gateways = gatewayService.getGateways(size, page);

        Page<GatewayResponseDTO> gatewayDtos = gateways
                .map(gateway -> modelMapper.map(gateway, GatewayResponseDTO.class));

        return gatewayDtos;

    }

    @GetMapping("/{id}")
    public GatewayResponseDTO getGateway(@PathVariable("id") String gatewayId) {

        Gateway gateway = gatewayService.getGateway(gatewayId);

        GatewayResponseDTO gatewayResponseDTO = modelMapper.map(gateway, GatewayResponseDTO.class);

        return gatewayResponseDTO;

    }

    @PostMapping("/peripheral")
    @ResponseStatus(HttpStatus.CREATED)
    public PeripheralResponseDTO createGatewayPeripheral(@Valid @RequestBody CreatePeripheralRequestDTO createPeripheralDTO) {

        Peripheral peripheral = gatewayService.createGatewayPeripheral(createPeripheralDTO);

        PeripheralResponseDTO peripheralDTO = modelMapper.map(peripheral, PeripheralResponseDTO.class);

        return peripheralDTO;

    }

    @DeleteMapping("/peripheral/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGatewayPeripheral(@PathVariable("id") Long peripheralId) {

        Peripheral peripheral = gatewayService.deletePeripheral(peripheralId);

    }

}
