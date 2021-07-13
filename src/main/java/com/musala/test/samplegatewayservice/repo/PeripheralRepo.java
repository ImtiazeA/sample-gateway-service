package com.musala.test.samplegatewayservice.repo;

import com.musala.test.samplegatewayservice.models.Peripheral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeripheralRepo extends JpaRepository<Peripheral, Long> {

    List<Peripheral> findByGatewayIdIn(List<String> gatewayIds);

}
