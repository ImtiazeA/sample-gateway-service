package com.musala.test.samplegatewayservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.test.samplegatewayservice.config.modelmapper.ModelMapperConfig;
import com.musala.test.samplegatewayservice.dtos.gateway.CreateGatewayDTO;
import com.musala.test.samplegatewayservice.dtos.peripheral.CreatePeripheralRequestDTO;
import com.musala.test.samplegatewayservice.models.Gateway;
import com.musala.test.samplegatewayservice.models.Peripheral;
import com.musala.test.samplegatewayservice.models.PeripheralStatus;
import com.musala.test.samplegatewayservice.repo.GatewayRepo;
import com.musala.test.samplegatewayservice.services.EntityNotFoundException;
import com.musala.test.samplegatewayservice.services.GatewayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GatewayController.class)
@AutoConfigureMockMvc
@Import({ModelMapperConfig.class})
class GatewayControllerTest {

    @MockBean
    private GatewayService gatewayService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("AddGateway")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AddGatewayTests {

        @Test
        @DisplayName("Given valid request body, When add gateway creates gateway, Returns created status, Returns valid response")
        void givenValidRequestBody_WhenAddGateway_CreatesGateway_ReturnsCreatedStatus_ReturnsValidResponse() throws Exception {

            String id = UUID.randomUUID().toString();
            String validName = "Valid Name";
            String ipV4Address = "192.168.0.1";

            Gateway gateway = new Gateway(id, validName, ipV4Address, null);

            when(gatewayService.createGateway(any()))
                    .thenReturn(gateway);

            CreateGatewayDTO dto = new CreateGatewayDTO(validName, ipV4Address);
            String requestBody = objectMapper.writeValueAsString(dto);

            MvcResult mvcResult = mockMvc
                    .perform(post("/gateway/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.name", is(validName)))
                    .andExpect(jsonPath("$.ipV4Address", is(ipV4Address)))
                    .andReturn();


            ArgumentCaptor<CreateGatewayDTO> gatewayCaptor = ArgumentCaptor.forClass(CreateGatewayDTO.class);

            verify(gatewayService, times(1))
                    .createGateway(gatewayCaptor.capture());

            assertThat(gatewayCaptor.getValue().getName()).isEqualTo(validName);
            assertThat(gatewayCaptor.getValue().getIpV4Address()).isEqualTo(ipV4Address);
        }

        @Test
        @DisplayName("Given request body with Invalid IP, When add gateway, Returns Bad Request status, Returns Error with Field Name and Validation Message")
        void givenRequestBodyWithInvalidIp_WhenAddGateway_ReturnsBadRequestReturnsErrorWithFieldNameAndValidationMessage() throws Exception {

            String validName = "Valid Name";
            String ipV4Address = "InvalidIpAddress";

            when(gatewayService.createGateway(any())).thenReturn(null);

            CreateGatewayDTO dto = new CreateGatewayDTO(validName, ipV4Address);
            String requestBody = objectMapper.writeValueAsString(dto);

            MvcResult mvcResult = mockMvc
                    .perform(post("/gateway/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Bad Request")))
                    .andExpect(jsonPath("$.errors[0].fieldName", is("ipV4Address")))
                    .andExpect(jsonPath("$.errors[0].message", is("Invalid IPV4 Address")))
                    .andReturn();

            ArgumentCaptor<CreateGatewayDTO> gatewayCaptor = ArgumentCaptor.forClass(CreateGatewayDTO.class);

            verify(gatewayService, times(0)).createGateway(gatewayCaptor.capture());
        }

    }

    @Nested
    @DisplayName("GetGateways")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetGatewaysTests {

        @Test
        @DisplayName("Given valid request, When get gateways, Returns OK status, Returns list of Gateways")
        void givenValidRequest_whenGetGateways_returnsOkStatus_returnsListOfGatewaysWhenExists() throws Exception {

            String id1 = UUID.randomUUID().toString();
            String validName1 = "Valid Name 1";
            String ipV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(id1, validName1, ipV4Address1, null);

            String id2 = UUID.randomUUID().toString();
            String validName2 = "Valid Name 1";
            String ipV4Address2 = "192.168.0.1";

            Gateway gateway2 = new Gateway(id2, validName2, ipV4Address2, null);

            when(gatewayService.getGateways(anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(gateway1, gateway2)));

            MvcResult mvcResult = mockMvc
                    .perform(get("/gateway/?size=50&page=0")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andReturn();

            verify(gatewayService, times(1))
                    .getGateways(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Given valid request, When get gateways, Returns OK status, Returns empty list of Gateways when None Exists")
        void givenValidRequest_whenGetGateways_returnsOkStatus_returnsEmptyListOfGatewaysWhenNoneExists() throws Exception {

            when(gatewayService.getGateways(anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(new ArrayList<>()));

            MvcResult mvcResult = mockMvc
                    .perform(get("/gateway/?size=50&page=0")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andReturn();

            verify(gatewayService, times(1))
                    .getGateways(anyInt(), anyInt());
        }

    }

    @Nested
    @DisplayName("GetGateway")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetGatewayTests {

        @Test
        @DisplayName("Given request with Existing ID, When get gateway, Returns OK status, Returns Gateway")
        void givenRequestWithValidId_whenGetGateway_returnsOkStatus_returnsGateway() throws Exception {

            String id1 = UUID.randomUUID().toString();
            String validName1 = "Valid Name 1";
            String ipV4Address1 = "192.168.0.1";

            Gateway gateway1 = new Gateway(id1, validName1, ipV4Address1, null);

            when(gatewayService.getGateway(anyString()))
                    .thenReturn(gateway1);

            MvcResult mvcResult = mockMvc
                    .perform(get("/gateway/{id}", id1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(id1)))
                    .andExpect(jsonPath("$.name", is(validName1)))
                    .andExpect(jsonPath("$.ipV4Address", is(ipV4Address1)))
                    .andReturn();

            verify(gatewayService, times(1))
                    .getGateway(anyString());
        }

        @Test
        @DisplayName("Given request with Not Existing Id, When get gateway, Returns Bad Request, Returns Error with Field Name and Validation Message")
        void givenRequestWithInvalidId_whenGetGateway_returnsBadRequestStatus_returnsErrorWithFieldNameAndValidationMessage() throws Exception {

            String id1 = UUID.randomUUID().toString();

            String exceptionMessage = "Gateway Not Found by ID: " + id1;

            when(gatewayService.getGateway(anyString()))
                    .thenThrow(new EntityNotFoundException(exceptionMessage));

            MvcResult mvcResult = mockMvc
                    .perform(get("/gateway/{id}", id1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Bad Request")))
                    .andExpect(jsonPath("$.errors[0].message", is(exceptionMessage)))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                    .andExpect(result -> assertEquals(exceptionMessage, result.getResolvedException().getMessage()))
                    .andReturn();

            verify(gatewayService, times(1))
                    .getGateway(anyString());
        }

    }

    @Nested
    @DisplayName("AddPeripheral")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AddPeripheralTests {

        @Test
        @DisplayName("Given valid request body, When add peripheral, Creates peripheral, Returns created status, Returns valid response")
        void givenValidRequestBody_WhenAddPeripheral_CreatesPeripheral_ReturnsCreatedStatus_ReturnsValidResponse() throws Exception {

            String gatewayId = UUID.randomUUID().toString();

            long peripheralId = 1;
            String name = "P1";
            String vendorName = "Vendor";
            String status = "ONLINE";

            CreatePeripheralRequestDTO requestDTO = new CreatePeripheralRequestDTO();
            requestDTO.setGatewayId(gatewayId);
            requestDTO.setName(name);
            requestDTO.setVendor(vendorName);
            requestDTO.setStatus(status);

            Peripheral peripheral = new Peripheral();
            peripheral.setId(peripheralId);
            peripheral.setName(name);
            peripheral.setStatus(PeripheralStatus.ONLINE);
            peripheral.setVendor(vendorName);
            peripheral.setGatewayId(gatewayId);

            when(gatewayService.createGatewayPeripheral(any()))
                    .thenReturn(peripheral);

            String requestBody = objectMapper.writeValueAsString(requestDTO);

            MvcResult mvcResult = mockMvc
                    .perform(post("/gateway/peripheral")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", equalTo(peripheralId), Long.class))
                    .andExpect(jsonPath("$.name", is(name)))
                    .andExpect(jsonPath("$.vendor", is(vendorName)))
                    .andExpect(jsonPath("$.gatewayId", is(gatewayId)))
                    .andExpect(jsonPath("$.status", is(status)))
                    .andReturn();

            ArgumentCaptor<CreatePeripheralRequestDTO> peripheralCaptor = ArgumentCaptor.forClass(CreatePeripheralRequestDTO.class);

            verify(gatewayService, times(1))
                    .createGatewayPeripheral(peripheralCaptor.capture());

            assertThat(peripheralCaptor.getValue().getName()).isEqualTo(name);
            assertThat(peripheralCaptor.getValue().getGatewayId()).isEqualTo(gatewayId);
            assertThat(peripheralCaptor.getValue().getVendor()).isEqualTo(vendorName);
            assertThat(peripheralCaptor.getValue().getStatus()).isEqualTo(status);

        }

        @Test
        @DisplayName("Given request body with invalid gateway id, When add peripheral, Returns bad request,  Returns error with validation message")
        void givenRequestBodyWithInvalidGatewayId_WhenAddPeripheral_ReturnsBadRequestReturnsErrorWithValidationMessage() throws Exception {

            String gatewayId = UUID.randomUUID().toString();

            long peripheralId = 1;
            String name = "P1";
            String vendorName = "Vendor";
            String status = "ONLINE";

            CreatePeripheralRequestDTO requestDTO = new CreatePeripheralRequestDTO();
            requestDTO.setGatewayId(gatewayId);
            requestDTO.setName(name);
            requestDTO.setVendor(vendorName);
            requestDTO.setStatus(status);

            Peripheral peripheral = new Peripheral();
            peripheral.setId(peripheralId);
            peripheral.setName(name);
            peripheral.setStatus(PeripheralStatus.ONLINE);
            peripheral.setVendor(vendorName);
            peripheral.setGatewayId(gatewayId);

            String message = "Gateway Not Found by ID: " + gatewayId;
            when(gatewayService.createGatewayPeripheral(any()))
                    .thenThrow(new EntityNotFoundException(message));

            String requestBody = objectMapper.writeValueAsString(requestDTO);

            MvcResult mvcResult = mockMvc
                    .perform(post("/gateway/peripheral")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Bad Request")))
                    .andExpect(jsonPath("$.errors[0].message", is(message)))
                    .andReturn();


            ArgumentCaptor<CreatePeripheralRequestDTO> perpheralCaptor = ArgumentCaptor.forClass(CreatePeripheralRequestDTO.class);

            verify(gatewayService, times(1))
                    .createGatewayPeripheral(perpheralCaptor.capture());

            assertThat(perpheralCaptor.getValue().getName()).isEqualTo(name);
            assertThat(perpheralCaptor.getValue().getGatewayId()).isEqualTo(gatewayId);
            assertThat(perpheralCaptor.getValue().getVendor()).isEqualTo(vendorName);
            assertThat(perpheralCaptor.getValue().getStatus()).isEqualTo(status);

        }

    }

    @Nested
    @DisplayName("DeletePeripheral")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeletePeripheralTests {

        @Test
        @DisplayName("Given valid peripheral id, When delete peripheral, Deletes peripheral, Returns no content status, Returns empty response")
        void givenValidPeripheralId_WhenDeletePeripheral_DeletesPeripheral_ReturnsNoContentStatus_ReturnsEmptyResponse() throws Exception {

            String gatewayId = UUID.randomUUID().toString();

            long peripheralId = 1;
            String name = "P1";
            String vendorName = "Vendor";
            String status = "ONLINE";

            Peripheral peripheral = new Peripheral();
            peripheral.setId(peripheralId);
            peripheral.setName(name);
            peripheral.setStatus(PeripheralStatus.ONLINE);
            peripheral.setVendor(vendorName);
            peripheral.setGatewayId(gatewayId);

            when(gatewayService.deletePeripheral(anyLong()))
                    .thenReturn(peripheral);


            MvcResult mvcResult = mockMvc
                    .perform(delete("/gateway/peripheral/{peripheralId}", peripheralId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""))
                    .andReturn();

            verify(gatewayService, times(1))
                    .deletePeripheral(anyLong());

        }


        @Test
        @DisplayName("Given invalid peripheral id, When delete peripheral, Deletes peripheral, Returns no content status, Returns empty response")
        void givenInvalidPeripheralId_WhenDeletePeripheral_ReturnsBadRequest_ReturnsErrorMessage() throws Exception {

            long peripheralId = 1;

            String message = "Peripheral Not Found by ID: " + peripheralId;
            when(gatewayService.deletePeripheral(anyLong()))
                    .thenThrow(new EntityNotFoundException(message));


            MvcResult mvcResult = mockMvc
                    .perform(delete("/gateway/peripheral/{peripheralId}", peripheralId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Bad Request")))
                    .andExpect(jsonPath("$.errors[0].message", is(message)))
                    .andReturn();

            verify(gatewayService, times(1))
                    .deletePeripheral(anyLong());

        }

    }

}