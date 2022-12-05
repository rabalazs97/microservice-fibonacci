package com.rbalazs.fibonacci.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.rbalazs.fibonacci.dto.FibonacciRequestDTO;
import com.rbalazs.fibonacci.service.FibonacciService;

@RestController
public class FibonacciController {
    private final FibonacciService fibonacciService;

    @Value("${message:Config server down}")
    private String configProp;

    @Autowired
    public FibonacciController(FibonacciService fibonacciService){
        this.fibonacciService = fibonacciService;
    }

    @PostMapping
    public String getFibonacci(@RequestBody FibonacciRequestDTO fibonacciRequestDTO){
        String result;
        try{
            System.out.println("Starting the computation..............................");
            result = "The number is: " + fibonacciService.getFibonacci(fibonacciRequestDTO);
            System.out.println("Finished the computation..............................");
        }
        catch(Exception e){
            result = e.getMessage();
        }
        return result;
    }

    @GetMapping
    public String getConfigProperty(){
        return configProp;
    }
    
}
