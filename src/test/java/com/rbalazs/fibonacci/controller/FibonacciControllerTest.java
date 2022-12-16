package com.rbalazs.fibonacci.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbalazs.fibonacci.dto.FibonacciRequestDTO;
import com.rbalazs.fibonacci.service.FibonacciService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;

@WebMvcTest
class FibonacciControllerTest {
    @MockBean
    private FibonacciService fibonacciService;

    @Value("${message:Config server down}")
    private String configProp;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetFibonacciValueOfNumber() throws Exception {
        FibonacciRequestDTO fibonacciRequestDTO = new FibonacciRequestDTO("Test", 10);
        given(fibonacciService.getFibonacci(fibonacciRequestDTO)).willReturn("55");
        mockMvc.perform(MockMvcRequestBuilders.post("/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fibonacciRequestDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("The number is: 55"));
    }

    @Test
    void shouldGetInterruptionErrorOnFibonacciCalculation() throws Exception {
        FibonacciRequestDTO fibonacciRequestDTO = new FibonacciRequestDTO("Test", 10);
        given(fibonacciService.getFibonacci(fibonacciRequestDTO)).willThrow(Exception.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fibonacciRequestDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getConfigProperty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}