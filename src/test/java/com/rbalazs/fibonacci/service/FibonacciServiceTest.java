package com.rbalazs.fibonacci.service;

import com.rbalazs.fibonacci.dto.FibonacciResponseDTO;
import com.rbalazs.fibonacci.dto.FibonacciSaveRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FibonacciServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceURL;
    private FibonacciService fibonacciService;

    @BeforeEach
    void setUp(){
        fibonacciService = new FibonacciService(restTemplate);
    }

    @Test
    void shouldGetUserIdFromOtherServiceByName()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String userName = "Test";
        int userId = 1;

        given(restTemplate.getForObject(userServiceURL + "user/" + "{name}", Integer.class, userName)).willReturn(userId);

        assertEquals(1,getFindUserByNameMethod().invoke(fibonacciService, userName));
        verify(restTemplate).getForObject(userServiceURL + "user/" + "{name}", Integer.class, userName);
    }

    @Test
    void shouldReturn0ForUserIdFromOtherServiceIfResultIsNull()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String userName = "Test";

        given(restTemplate.getForObject(userServiceURL + "user/" + "{name}", Integer.class, userName)).willReturn(null);

        assertEquals(0,getFindUserByNameMethod().invoke(fibonacciService, userName));
        verify(restTemplate).getForObject(userServiceURL + "user/" + "{name}", Integer.class, userName);
    }

    @Test
    void shouldBeAbleToReturnUserSpecificCalculationHistory()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int userId = 1;
        FibonacciResponseDTO fibonacciResponseDTO = new FibonacciResponseDTO(10,new BigInteger("55"));
        FibonacciResponseDTO fibonacciResponseDTO2 = new FibonacciResponseDTO(11,new BigInteger("89"));
        FibonacciResponseDTO expectedResult[] = new FibonacciResponseDTO[]
                { fibonacciResponseDTO, fibonacciResponseDTO2 };

        given(restTemplate.getForObject(userServiceURL + "history/{id}", FibonacciResponseDTO[].class, userId)).
                willReturn(expectedResult);

        assertEquals(expectedResult, getGetUserSpecificCalculationHistory().invoke(fibonacciService, userId));
        verify(restTemplate).getForObject(userServiceURL + "history/{id}", FibonacciResponseDTO[].class, userId);
    }

    @Test
    void shouldBeAbleToSaveFibonacciToDatabase()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FibonacciSaveRequestDTO fibonacciSaveRequestDTO = new FibonacciSaveRequestDTO(10, new BigInteger("55").toByteArray(), 1);

        getSaveFibonacciToDatabase().invoke(fibonacciService, fibonacciSaveRequestDTO);

        verify(restTemplate).postForObject(userServiceURL + "save", fibonacciSaveRequestDTO, ResponseEntity.class);
    }

    @Test
    void shouldCalculateTheNThFibonacciNumber()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int serialNumber = 10;
        BigInteger expectedResult = new BigInteger("55");
        assertEquals(expectedResult, getCalculateFibonacci().invoke(fibonacciService, serialNumber));
    }

    private Method getFindUserByNameMethod() throws NoSuchMethodException {
        Method method = FibonacciService.class.getDeclaredMethod("findUserByName", String.class);
        method.setAccessible(true);
        return method;
    }

    private Method getGetUserSpecificCalculationHistory() throws NoSuchMethodException {
        Method method = FibonacciService.class.getDeclaredMethod("getUserSpecificCalculationHistory", int.class);
        method.setAccessible(true);
        return method;
    }

    private Method getSaveFibonacciToDatabase() throws NoSuchMethodException {
        Method method = FibonacciService.class.getDeclaredMethod("saveFibonacciToDatabase", FibonacciSaveRequestDTO.class);
        method.setAccessible(true);
        return method;
    }

    private Method getCalculateFibonacci() throws NoSuchMethodException {
        Method method = FibonacciService.class.getDeclaredMethod("calculateFibonacci", int.class);
        method.setAccessible(true);
        return method;
    }
}