package io.davidabejirin.bvnvalidationassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.davidabejirin.bvnvalidationassessment.util.ResponseStatusCode;
import io.davidabejirin.bvnvalidationassessment.entity.BVNRequestResponse;
import io.davidabejirin.bvnvalidationassessment.payload.BVNInfoDTO;
import io.davidabejirin.bvnvalidationassessment.payload.BVNRequest;
import io.davidabejirin.bvnvalidationassessment.repository.BVNRequestResponseRepository;
import io.davidabejirin.bvnvalidationassessment.service.impl.BVNCacheImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BVNValidationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BVNRequestResponseRepository BVNRequestResponseRepository;

    private final Faker faker = new Faker();

    private final String BVN_VALIDATION_ENDPOINT = "/bv-service/svalidate/wrapper";

    public Long processingTime(Long startTime, Long endTime) {
        return endTime - startTime;
    }

    @Test
    public void validBVNShouldPass() throws Exception {
        BVNInfoDTO validBVNInfoDTO = getValidDetails();
        BVNRequest request = new BVNRequest();
        request.setBvn(validBVNInfoDTO.getBvn());
        Long startTime = System.currentTimeMillis();

        mockMvc.perform(post(BVN_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.code").value(ResponseStatusCode.SUCCESS))
                .andExpect(jsonPath("$.bvn").value(validBVNInfoDTO.getBvn()))
                .andExpect(jsonPath("$.imageDetail").value(validBVNInfoDTO.getImageDetail()))
                .andExpect(jsonPath("$.basicDetail").value(validBVNInfoDTO.getBasicDetail()));
        Long endTime = System.currentTimeMillis();
        assertTrue(processingTime(startTime, endTime) < 5000);
    }


    @Test
    public void emptyBVNShouldFail() throws Exception {
        BVNRequest request = new BVNRequest();
        request.setBvn("");
        Long startTime = System.currentTimeMillis();
        mockMvc.perform(post(BVN_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more of your request parameters failed validation. Please retry"))
                .andExpect(jsonPath("$.code").value(ResponseStatusCode.BAD_REQUEST));
        Long endTime = System.currentTimeMillis();
        assertTrue(processingTime(startTime, endTime) < 1000);
    }

    @Test
    public void invalidBVNNotFoundShouldFail() throws Exception {
        String invalidBvn = faker.number().digits(11);
        BVNRequest request = new BVNRequest();
        request.setBvn(invalidBvn);
        Long startTime = System.currentTimeMillis();
        mockMvc.perform(post(BVN_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The searched BVN does not exist"))
                .andExpect(jsonPath("$.code").value(ResponseStatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.bvn").value(invalidBvn));
        Long endTime = System.currentTimeMillis();
        assertTrue(processingTime(startTime, endTime) < 1000);
    }

    @Test
    public void BVNWithInvalidLengthShouldFail() throws Exception {
        int invalidBvnLength = faker.number().numberBetween(1, 10);
        String bvnWithInvalidLength = new Faker().number().digits(invalidBvnLength);
        BVNRequest request = new BVNRequest();
        request.setBvn(bvnWithInvalidLength);
        Long startTime = System.currentTimeMillis();
        mockMvc.perform(post(BVN_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The searched BVN is invalid"))
                .andExpect(jsonPath("$.code").value(ResponseStatusCode.INVALID_BVN_DIGITS))
                .andExpect(jsonPath("$.bvn").value(bvnWithInvalidLength));
        Long endTime = System.currentTimeMillis();
        assertTrue(processingTime(startTime, endTime) < 1000);
    }

    @Test
    public void invalidBVNWithNonDigitsShouldFail() throws Exception {
        String bvnWithNonDigit = new Faker().number().digits(10) + "A";
        BVNRequest request = new BVNRequest();
        request.setBvn(bvnWithNonDigit);
        Long startTime = System.currentTimeMillis();

        mockMvc.perform(post(BVN_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The searched BVN is invalid"))
                .andExpect(jsonPath("$.code").value(ResponseStatusCode.BAD_REQUEST))
                .andExpect(jsonPath("$.bvn").value(bvnWithNonDigit));
        Long endTime = System.currentTimeMillis();
        assertTrue(processingTime(startTime, endTime) < 1000);
    }

    @Test
    public void persistRequestAndResponsePayload() throws Exception {
        BVNInfoDTO validBVNInfoDTO = getValidDetails();
        BVNRequest request = new BVNRequest();
        request.setBvn(validBVNInfoDTO.getBvn());
        Long startTime = System.currentTimeMillis();

        ArgumentCaptor<BVNRequestResponse> logEntryArgumentCaptor = ArgumentCaptor.forClass(BVNRequestResponse.class);
        doReturn(null).when(BVNRequestResponseRepository).save(logEntryArgumentCaptor.capture());

        String requestPayload = mapper.writeValueAsString(request);

        String responsePayload = mockMvc.perform(post(BVN_VALIDATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        verify(BVNRequestResponseRepository, times(1)).save(any(BVNRequestResponse.class));
        Long endTime = System.currentTimeMillis();
        assertTrue(processingTime(startTime, endTime) < 5000);
        BVNRequestResponse logEntry = logEntryArgumentCaptor.getValue();
        Assertions.assertEquals(requestPayload, logEntry.getRequest());
        Assertions.assertEquals(responsePayload, logEntry.getResponse());
        Assertions.assertNotNull(logEntry.getId());
    }

    private BVNInfoDTO getValidDetails() {
        return BVNCacheImpl.BVN_DETAILS.stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("No BVN details have been configured"));
    }
}
