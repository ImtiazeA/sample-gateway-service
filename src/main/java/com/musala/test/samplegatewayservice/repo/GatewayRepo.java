package com.musala.test.samplegatewayservice.repo;

import com.musala.test.samplegatewayservice.models.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatewayRepo extends JpaRepository<Gateway, String> {

}
