package com.musala.test.samplegatewayservice.services;

import com.musala.test.samplegatewayservice.controllers.OperationNotAllowedException;
import com.musala.test.samplegatewayservice.dtos.gateway.CreateGatewayDTO;
import com.musala.test.samplegatewayservice.dtos.peripheral.CreatePeripheralRequestDTO;
import com.musala.test.samplegatewayservice.models.Gateway;
import com.musala.test.samplegatewayservice.models.Peripheral;
import com.musala.test.samplegatewayservice.repo.GatewayRepo;
import com.musala.test.samplegatewayservice.repo.PeripheralRepo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GatewayService {

    private final GatewayRepo gatewayRepo;
    private final PeripheralRepo peripheralRepo;
    private final ModelMapper modelMapper;

    public GatewayService(GatewayRepo gatewayRepo, PeripheralRepo peripheralRepo, ModelMapper modelMapper) {
        this.gatewayRepo = gatewayRepo;
        this.peripheralRepo = peripheralRepo;
        this.modelMapper = modelMapper;
    }

    public Gateway createGateway(CreateGatewayDTO createGatewayDTO) {

        Gateway gatewayNotSaved = modelMapper.map(createGatewayDTO, Gateway.class);

        Gateway gateway = gatewayRepo.save(gatewayNotSaved);

        return gateway;

    }

    public Page<Gateway> getGateways(int size, int page) {

        var pageRequest = PageRequest.of(page, size, Sort.by("createdAt"));

        Page<Gateway> gateways = gatewayRepo.findAll(pageRequest);

        List<String> gatewayIds = gateways.stream()
                .map(Gateway::getId)
                .distinct()
                .collect(Collectors.toList());

        List<Peripheral> peripherals = peripheralRepo.findByGatewayIdIn(gatewayIds);

        Map<String, List<Peripheral>> gatewayIdToPeripherals = peripherals.stream()
                .collect(Collectors.groupingBy(Peripheral::getGatewayId));

        for (Gateway gateway : gateways) {
            List<Peripheral> gatewayPeripherals = gatewayIdToPeripherals.get(gateway.getId());
            gateway.setPeripherals(gatewayPeripherals);
        }

        return gateways;

    }


    public Gateway getGateway(String gatewayId) {

        var gateway = gatewayRepo.findById(gatewayId)
                .orElseThrow(() -> new EntityNotFoundException("Gateway Not Found by ID: " + gatewayId));

        List<Peripheral> peripherals = peripheralRepo.findByGatewayIdIn(List.of(gatewayId));

        gateway.setPeripherals(peripherals);

        return gateway;
    }

    public Peripheral createGatewayPeripheral(CreatePeripheralRequestDTO createPeripheralDTO) {

        log.trace("Thread Name: {}, Thread ID: {}", Thread.currentThread().getName(), Thread.currentThread().getId());

        String gatewayId = createPeripheralDTO.getGatewayId();

        Gateway gateway = gatewayRepo.findById(gatewayId)
                .orElseThrow(() -> new EntityNotFoundException("Gateway Not Found by ID: " + gatewayId));

        List<Peripheral> peripherals = peripheralRepo.findByGatewayIdIn(List.of(gatewayId));

        log.trace("Existing Peripherals Count: {}", peripherals.size());

        if (peripherals.size() >= 10) {
            throw new OperationNotAllowedException("You already have 10 peripherals on this gateway.");
        }

        Peripheral peripheralNotSaved = modelMapper.map(createPeripheralDTO, Peripheral.class);

        Peripheral peripheral = peripheralRepo.save(peripheralNotSaved);

        return peripheral;

    }

    public Peripheral deletePeripheral(Long peripheralId) {

        Peripheral peripheral = peripheralRepo.findById(peripheralId)
                .orElseThrow(() -> new EntityNotFoundException("Peripheral Not Found by ID: " + peripheralId));

        peripheralRepo.delete(peripheral);

        return peripheral;
    }

}
