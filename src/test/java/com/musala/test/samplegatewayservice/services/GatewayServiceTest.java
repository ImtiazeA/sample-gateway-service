package com.musala.test.samplegatewayservice.services;

import com.musala.test.samplegatewayservice.config.modelmapper.ModelMapperConfig;
import com.musala.test.samplegatewayservice.controllers.OperationNotAllowedException;
import com.musala.test.samplegatewayservice.dtos.gateway.CreateGatewayDTO;
import com.musala.test.samplegatewayservice.dtos.peripheral.CreatePeripheralRequestDTO;
import com.musala.test.samplegatewayservice.models.Gateway;
import com.musala.test.samplegatewayservice.models.Peripheral;
import com.musala.test.samplegatewayservice.models.PeripheralStatus;
import com.musala.test.samplegatewayservice.repo.GatewayRepo;
import com.musala.test.samplegatewayservice.repo.PeripheralRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.VoidAnswer1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = {ModelMapperConfig.class, GatewayService.class})
@Import({ModelMapperConfig.class})
class GatewayServiceTest {

    @MockBean
    private GatewayRepo gatewayRepo;

    @MockBean
    private PeripheralRepo peripheralRepo;

    @Autowired
    private GatewayService gatewayService;


    @Nested
    @DisplayName("CreateGateway")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateGatewayTests {

        @Test
        @DisplayName("Given valid CreateGatewayDTO instance, When add gateway, Creates Gateway, Returns saved Gateway")
        void givenValidCreateGatewayDTO_WhenCreateGateway_CreatesGateway_ReturnsSavedGateway() {

            String id = UUID.randomUUID().toString();
            String validName = "Valid Name";
            String ipV4Address = "192.168.0.1";

            CreateGatewayDTO createGatewayDTO = new CreateGatewayDTO(validName, ipV4Address);

            Gateway gateway = new Gateway(id, validName, ipV4Address, null);

            when(gatewayRepo.save(any()))
                    .thenReturn(gateway);

            Gateway returnedGateway = gatewayService.createGateway(createGatewayDTO);

            ArgumentCaptor<Gateway> gatewayCaptor = ArgumentCaptor.forClass(Gateway.class);

            verify(gatewayRepo, times(1))
                    .save(gatewayCaptor.capture());

            // assert captured object has expected values
            assertThat(gatewayCaptor.getValue().getId())
                    .isNull();
            assertThat(gatewayCaptor.getValue().getName())
                    .isEqualTo(validName);
            assertThat(gatewayCaptor.getValue().getIpV4Address())
                    .isEqualTo(ipV4Address);

            // assert returned object has expected values
            assertThat(returnedGateway.getId())
                    .isEqualTo(id);
            assertThat(returnedGateway.getIpV4Address())
                    .isEqualTo(ipV4Address);
            assertThat(returnedGateway.getName())
                    .isEqualTo(validName);
        }

        @Test
        @DisplayName("Given CreateGatewayDTO with Invalid IP, When add gateway, Creates Gateway, Throws Exception")
        void givenCreateGatewayDtoWithInvalidIp_WhenCreateGateway_CreatesGateway_ReturnsSavedGateway() {

            String id = UUID.randomUUID().toString();
            String validName = "Valid Name";
            String ipV4Address = "InvalidIpAddress";

            CreateGatewayDTO createGatewayDTO = new CreateGatewayDTO(validName, ipV4Address);

            Gateway gateway = new Gateway(id, validName, ipV4Address, null);

            when(gatewayRepo.save(any()))
                    .thenThrow(ConstraintViolationException.class);

            // assert exception is thrown when tried to save on db
            assertThrows(ConstraintViolationException.class, () -> gatewayService.createGateway(createGatewayDTO));

            ArgumentCaptor<Gateway> gatewayCaptor = ArgumentCaptor.forClass(Gateway.class);

            verify(gatewayRepo, times(1))
                    .save(gatewayCaptor.capture());

            // assert captured object has expected values
            assertThat(gatewayCaptor.getValue().getId())
                    .isNull();
            assertThat(gatewayCaptor.getValue().getName())
                    .isEqualTo(validName);
            assertThat(gatewayCaptor.getValue().getIpV4Address())
                    .isEqualTo(ipV4Address);
        }

    }


    @Nested
    @DisplayName("GetGateways")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetGatewaysTests {

        @Test
        @DisplayName("Given valid Size, Page No, When Get Gateways, Returns Page of Gateways")
        void givenValidPageSizePageNo_WhenGetGateways_ReturnsPageOfGateways() {

            String id1 = UUID.randomUUID().toString();
            String validName1 = "Valid Name 1";
            String ipV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(id1, validName1, ipV4Address1, null);

            String id2 = UUID.randomUUID().toString();
            String validName2 = "Valid Name 1";
            String ipV4Address2 = "192.168.0.1";

            Gateway gateway2 = new Gateway(id2, validName2, ipV4Address2, null);

            when(gatewayRepo.findAll(any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(gateway1, gateway2)));

            int pageSize = 50;
            int pageNo = 0;
            Page<Gateway> returnedGateways = gatewayService.getGateways(pageSize, pageNo);

            // verify gatewayRepo findAll method was invoked once
            verify(gatewayRepo, times(1))
                    .findAll(any(PageRequest.class));

            // verify peripheralRepo findByGatewayIdIn method invoked onc with correct params
            ArgumentCaptor<List<String>> gatewayIdCaptor = ArgumentCaptor.forClass((Class) List.class);

            // verify gatewayRepo findAll method was invoked once
            verify(peripheralRepo, times(1))
                    .findByGatewayIdIn(gatewayIdCaptor.capture());

            // assert captured object has expected values
            List<String> capturedGatewayIds = gatewayIdCaptor.getValue();

            assertThat(capturedGatewayIds).hasSameElementsAs(List.of(id1, id2));

        }

    }


    @Nested
    @DisplayName("GetGateway")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetGatewayTests {

        @Test
        @DisplayName("Given valid Gateway ID, When Get Gateway, Returns One Gateway")
        void givenValidGatewayId_WhenGetGateway_ReturnsSavedGateway() {

            String id1 = UUID.randomUUID().toString();
            String validName1 = "Valid Name 1";
            String ipV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(id1, validName1, ipV4Address1, null);

            when(gatewayRepo.findById(anyString()))
                    .thenReturn(Optional.of(gateway1));

            Gateway returnedGateway = gatewayService.getGateway(id1);

            // verify gatewayRepo findAll method was invoked once
            verify(gatewayRepo, times(1))
                    .findById(anyString());

            // verify peripheralRepo findByGatewayIdIn method invoked onc with correct params
            ArgumentCaptor<List<String>> gatewayIdCaptor = ArgumentCaptor.forClass((Class) List.class);

            // verify gatewayRepo findAll method was invoked once
            verify(peripheralRepo, times(1))
                    .findByGatewayIdIn(gatewayIdCaptor.capture());

            // assert captured object has expected values
            List<String> capturedGatewayIds = gatewayIdCaptor.getValue();

            assertThat(capturedGatewayIds).hasSameElementsAs(List.of(id1));

        }

        @Test
        @DisplayName("Given Invalid Gateway ID, When Get Gateway, Throws Exception")
        void givenInvalidGatewayId_WhenGetGateway_ThrowsException() {

            String id1 = UUID.randomUUID().toString();
            String validName1 = "Valid Name 1";
            String ipV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(id1, validName1, ipV4Address1, null);

            when(gatewayRepo.findById(anyString()))
                    .thenThrow(new EntityNotFoundException("Gateway Not Found by ID: " + id1));

            assertThrows(EntityNotFoundException.class, () -> gatewayRepo.findById(id1));

            // verify gatewayRepo findAll method was invoked once
            verify(gatewayRepo, times(1))
                    .findById(anyString());

            // verify peripheralRepo findByGatewayIdIn method invoked onc with correct params
            ArgumentCaptor<List<String>> gatewayIdCaptor = ArgumentCaptor.forClass((Class) List.class);

            // verify gatewayRepo findAll method was invoked once
            verify(peripheralRepo, times(0))
                    .findByGatewayIdIn(gatewayIdCaptor.capture());

        }

    }


    @Nested
    @DisplayName("CreateGatewayPeripheral")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateGatewayPeripheralTests {

        @Test
        @DisplayName("Given Valid Create Peripheral Request Dto, When create gateway peripheral, Creates peripheral, Returns saved peripheral")
        void givenValidCreatePeripheralRequestDTO_WhenCreateGatewayPeripheral_CreatesCreatePeripheral_ReturnsSavedPeripheral() {

            String gatewayId1 = UUID.randomUUID().toString();
            String gatewayValidName1 = "Valid Name 1";
            String validIpV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(gatewayId1, gatewayValidName1, validIpV4Address1, null);

            long peripheralId1 = 1;
            String peripheralName1 = "P1";
            String peripheralVendorName = "Vendor";
            String peripheralStatus = "ONLINE";

            CreatePeripheralRequestDTO requestDTO = new CreatePeripheralRequestDTO();
            requestDTO.setGatewayId(gatewayId1);
            requestDTO.setName(peripheralName1);
            requestDTO.setVendor(peripheralVendorName);
            requestDTO.setStatus(peripheralStatus);

            Peripheral peripheral1 = new Peripheral();
            peripheral1.setId(peripheralId1);
            peripheral1.setName(peripheralName1);
            peripheral1.setStatus(PeripheralStatus.ONLINE);
            peripheral1.setVendor(peripheralVendorName);
            peripheral1.setGatewayId(gatewayId1);

            when(gatewayRepo.findById(anyString()))
                    .thenReturn(Optional.of(gateway1));

            when(peripheralRepo.findByGatewayIdIn(any()))
                    .thenReturn(List.of());

            when(peripheralRepo.save(any()))
                    .thenReturn(peripheral1);

            Peripheral returnedPeripheral = gatewayService.createGatewayPeripheral(requestDTO);

            ArgumentCaptor<List<String>> gatewayIdCaptor = ArgumentCaptor.forClass((Class) List.class);

            verify(peripheralRepo, times(1))
                    .findByGatewayIdIn(gatewayIdCaptor.capture());

            assertThat(returnedPeripheral.getGatewayId()).isEqualTo(gatewayId1);
            assertThat(returnedPeripheral.getName()).isEqualTo(peripheralName1);
            assertThat(returnedPeripheral.getVendor()).isEqualTo(peripheralVendorName);
            assertThat(returnedPeripheral.getId()).isEqualTo(peripheralId1);

        }

        @Test
        @DisplayName("Given Valid Create Peripheral Request Dto, When create gateway peripheral, Gateway Has More Than Allowed Peripheral, Throws Exception")
        void givenValidCreatePeripheralRequestDTO_WhenCreateGatewayPeripheral_GatewayHasMoreThanAllowedPeripherals_ThrowsException() {

            String gatewayId1 = UUID.randomUUID().toString();
            String gatewayValidName1 = "Valid Name 1";
            String validIpV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(gatewayId1, gatewayValidName1, validIpV4Address1, null);

            long peripheralId1 = 1;
            String peripheralName1 = "P1";
            String peripheralVendorName = "Vendor";
            String peripheralStatus = "ONLINE";

            CreatePeripheralRequestDTO requestDTO = new CreatePeripheralRequestDTO();
            requestDTO.setGatewayId(gatewayId1);
            requestDTO.setName(peripheralName1);
            requestDTO.setVendor(peripheralVendorName);
            requestDTO.setStatus(peripheralStatus);

            Peripheral peripheral1 = new Peripheral();
            peripheral1.setId(peripheralId1);
            peripheral1.setName(peripheralName1);
            peripheral1.setStatus(PeripheralStatus.ONLINE);
            peripheral1.setVendor(peripheralVendorName);
            peripheral1.setGatewayId(gatewayId1);

            List<Peripheral> existingPeripherals = new ArrayList<>();

            int maxAllowedPeripheralCount = 10;

            for (int i = 0; i < maxAllowedPeripheralCount; i++) {
                existingPeripherals.add(peripheral1);
            }

            when(gatewayRepo.findById(anyString()))
                    .thenReturn(Optional.of(gateway1));

            when(peripheralRepo.findByGatewayIdIn(any()))
                    .thenReturn(existingPeripherals);

            when(peripheralRepo.save(any()))
                    .thenReturn(peripheral1);

            String expectedExceptionMessage = "You already have 10 peripherals on this gateway.";

            assertThrows(OperationNotAllowedException.class, () -> gatewayService.createGatewayPeripheral(requestDTO), expectedExceptionMessage);

            // verify retrieves peripherals from db to get the existing peripheral count
            ArgumentCaptor<List<String>> gatewayIdCaptor = ArgumentCaptor.forClass((Class) List.class);

            verify(peripheralRepo, times(1))
                    .findByGatewayIdIn(gatewayIdCaptor.capture());

            // verify does not try to save peripheral
            ArgumentCaptor<Peripheral> peripheralCaptor = ArgumentCaptor.forClass(Peripheral.class);

            verify(peripheralRepo, times(0))
                    .save(peripheralCaptor.capture());

        }

        @Test
        @DisplayName("Given CreatePeripheralRequestDto with Non Existing Gateway Id, When Create Gateway Peripheral, Throws Exception")
        void givenValidCreatePeripheralRequestDtoWithNonExistingGateway_WhenCreateGatewayPeripheral_ThrowsException() {

            String gatewayId1 = UUID.randomUUID().toString();
            String gatewayValidName1 = "Valid Name 1";
            String validIpV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(gatewayId1, gatewayValidName1, validIpV4Address1, null);

            long peripheralId1 = 1;
            String peripheralName1 = "P1";
            String peripheralVendorName = "Vendor";
            String peripheralStatus = "ONLINE";

            CreatePeripheralRequestDTO requestDTO = new CreatePeripheralRequestDTO();
            requestDTO.setGatewayId(gatewayId1);
            requestDTO.setName(peripheralName1);
            requestDTO.setVendor(peripheralVendorName);
            requestDTO.setStatus(peripheralStatus);

            Peripheral peripheral1 = new Peripheral();
            peripheral1.setId(peripheralId1);
            peripheral1.setName(peripheralName1);
            peripheral1.setStatus(PeripheralStatus.ONLINE);
            peripheral1.setVendor(peripheralVendorName);
            peripheral1.setGatewayId(gatewayId1);

            List<Peripheral> existingPeripherals = new ArrayList<>();

            int maxAllowedPeripheralCount = 10;

            for (int i = 0; i < maxAllowedPeripheralCount; i++) {
                existingPeripherals.add(peripheral1);
            }

            when(gatewayRepo.findById(anyString()))
                    .thenReturn(Optional.empty());

            String expectedExceptionMessage = "Gateway Not Found by ID: " + gatewayId1;

            assertThrows(EntityNotFoundException.class, () -> gatewayService.createGatewayPeripheral(requestDTO), expectedExceptionMessage);

            // verify retrieves peripherals from db to get the existing peripheral count
            ArgumentCaptor<List<String>> gatewayIdCaptor = ArgumentCaptor.forClass((Class) List.class);

            verify(peripheralRepo, times(0))
                    .findByGatewayIdIn(gatewayIdCaptor.capture());

            // verify does not try to save peripheral
            ArgumentCaptor<Peripheral> peripheralCaptor = ArgumentCaptor.forClass(Peripheral.class);

            verify(peripheralRepo, times(0))
                    .save(peripheralCaptor.capture());

        }

    }



    @Nested
    @DisplayName("DeletePeripheral")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeletePeripheralTests {

        @Test
        @DisplayName("Given Valid Peripheral Id, When Delete Peripheral, Returns Deleted Peripheral")
        void givenValidPeripheralId_WhenDeletePeripheral_ReturnsDeletedPeripheral() {

            String gatewayId1 = UUID.randomUUID().toString();

            long peripheralId1 = 1;
            String peripheralName1 = "P1";
            String peripheralVendorName = "Vendor";
            String peripheralStatus = "ONLINE";

            Peripheral peripheral1 = new Peripheral();
            peripheral1.setId(peripheralId1);
            peripheral1.setName(peripheralName1);
            peripheral1.setStatus(PeripheralStatus.ONLINE);
            peripheral1.setVendor(peripheralVendorName);
            peripheral1.setGatewayId(gatewayId1);

            when(peripheralRepo.findById(any()))
                    .thenReturn(Optional.of(peripheral1));

            doNothing().when(peripheralRepo)
                    .delete(any());

            Peripheral returnedPeripheral = gatewayService.deletePeripheral(peripheralId1);

            // verify method to retrieve peripheral was invoked
            ArgumentCaptor<Long> peripheralIdCaptor = ArgumentCaptor.forClass(Long.class);

            verify(peripheralRepo, times(1))
                    .findById(peripheralIdCaptor.capture());

            // verify method to delete the peripheral was invoked
            ArgumentCaptor<Peripheral> peripheralCaptor = ArgumentCaptor.forClass(Peripheral.class);

            verify(peripheralRepo, times(1))
                    .delete(peripheralCaptor.capture());

            assertThat(peripheralCaptor.getValue().getId()).isEqualTo(peripheralId1);
            assertThat(returnedPeripheral.getId()).isEqualTo(peripheralId1);

        }

        @Test
        @DisplayName("Given Non Existing Peripheral Id, When Delete Peripheral, Throws Exception")
        void givenNonExistingPeripheralId_WhenDeletePeripheral_ThrowsException() {

            String gatewayId1 = UUID.randomUUID().toString();

            long peripheralId1 = 1;
            String peripheralName1 = "P1";
            String peripheralVendorName = "Vendor";
            String peripheralStatus = "ONLINE";

            Peripheral peripheral1 = new Peripheral();
            peripheral1.setId(peripheralId1);
            peripheral1.setName(peripheralName1);
            peripheral1.setStatus(PeripheralStatus.ONLINE);
            peripheral1.setVendor(peripheralVendorName);
            peripheral1.setGatewayId(gatewayId1);

            when(peripheralRepo.findById(any()))
                    .thenReturn(Optional.empty());

            String exceptionMessage = "Peripheral Not Found by ID: " + peripheralId1;

            assertThrows(EntityNotFoundException.class, () -> gatewayService.deletePeripheral(peripheralId1), exceptionMessage);

            // verify method to retrieve peripheral was invoked
            ArgumentCaptor<Long> peripheralIdCaptor = ArgumentCaptor.forClass(Long.class);

            verify(peripheralRepo, times(1))
                    .findById(peripheralIdCaptor.capture());

            // verify method to delete the peripheral was invoked
            ArgumentCaptor<Peripheral> peripheralCaptor = ArgumentCaptor.forClass(Peripheral.class);

            verify(peripheralRepo, times(0))
                    .delete(peripheralCaptor.capture());

        }

    }
}