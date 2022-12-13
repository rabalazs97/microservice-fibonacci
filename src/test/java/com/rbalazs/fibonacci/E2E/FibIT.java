package com.rbalazs.fibonacci.E2E;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.rbalazs.fibonacci.dto.FibonacciRequestDTO;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FibIT {
    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate restTemplate;

    // @Container
    // DockerComposeContainer<?> environment = new DockerComposeContainer<>(new File("src/test/resources/compose-test.yml"))
    // //.withExposedService("db", 5435, Wait.forListeningPort())
    // .withExposedService("user", 8090, Wait.forListeningPort())
    // //.withExposedService("configserver_1",  8888, Wait.forListeningPort())
    // .withLocalCompose(true);
    
    @Test
    public void testConfigServer() throws Exception{
        assertEquals("Hello world", this.restTemplate.getForObject("http://localhost:" + port + "/", String.class));
    }

    @Test
    public void testGetFibonacci() throws Exception{
        FibonacciRequestDTO fibonacciRequestDTO = new FibonacciRequestDTO("test", 10);
        String answer = this.restTemplate.postForObject("http://localhost:" + port + "/", fibonacciRequestDTO, String.class);
        System.out.println(answer);
        assertTrue(answer.equals("The number is: 55"));
    }
}
