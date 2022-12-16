package com.rbalazs.fibonacci.service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rbalazs.fibonacci.dto.FibonacciRequestDTO;
import com.rbalazs.fibonacci.dto.FibonacciResponseDTO;
import com.rbalazs.fibonacci.dto.FibonacciSaveRequestDTO;

@Service
public class FibonacciService {
    private final RestTemplate restTemplate;
    
    @Value("${user.service.url}")
    private String userServiceURL;

    @Autowired
    public FibonacciService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String getFibonacci(FibonacciRequestDTO fibonacciRequestDTO) throws Exception{
        System.out.println("Getting userId...");
        int userId = findUserByName(fibonacciRequestDTO.getName());
        if(userId != 0){
            System.out.println("Checking if it's been calculated already by the user.");
            List<FibonacciResponseDTO> fibList = Arrays.asList(getUserSpecificCalculationHistory(userId));
            System.out.println("Got the list!");
            for(int i = 0; i < fibList.size(); i++){
                if(fibList.get(i).getSerialNumber() == fibonacciRequestDTO.getNumber()){
                    System.out.println("The number was in the list of the user, giving back the number from the database.");
                    return fibList.get(i).getFibonacciNumber().toString();
                }
            }
        }

        System.out.println("The number wasn't in the user's history, so please wait until the calculation finishes.");

        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<BigInteger> task = new Callable<BigInteger>(){
            public BigInteger call(){
                return calculateFibonacci(fibonacciRequestDTO.getNumber());
            }
        };

        Future<BigInteger> future = executor.submit(task);
        try{
            BigInteger fibNumber = future.get(10, TimeUnit.SECONDS);
     
            FibonacciSaveRequestDTO fibonacciSaveRequestDTO = FibonacciSaveRequestDTO.builder()
                                                .serialNumber(fibonacciRequestDTO.getNumber())
                                                .fibonacciNumber(fibNumber.toByteArray())
                                                .userId(userId)
                                                .build();

            saveFibonacciToDatabase(fibonacciSaveRequestDTO);

            return fibNumber.toString();
        }
        catch(TimeoutException te){
            throw new Exception("The provided number is too big to calculate. Please enter a smaller number!"); 
        }
        catch(InterruptedException ie){
            throw new Exception("Calculation got interrupted.");
        }
        catch(ExecutionException ee){
            throw new Exception("Server error.");
        }
        finally{
            future.cancel(true);
        }
    }

    private int findUserByName(String name){
        if(name == "") return 0;
        Integer result = restTemplate.getForObject(userServiceURL + "user/" + "{name}", Integer.class, name);
        if(result != null) return result;
        return 0;
    }

    private FibonacciResponseDTO[] getUserSpecificCalculationHistory(int id){
        return restTemplate.getForObject(userServiceURL + "history/{id}", FibonacciResponseDTO[].class, id);
    }

    private void saveFibonacciToDatabase(FibonacciSaveRequestDTO fibonacciSaveRequestDTO){
        restTemplate.postForObject(userServiceURL + "save", fibonacciSaveRequestDTO, ResponseEntity.class);
    }

    private BigInteger calculateFibonacci(int number){
        BigInteger first = new BigInteger("0");
        if(number <= 0 || number > Integer.MAX_VALUE) return first;
        BigInteger second = new BigInteger("1");
        BigInteger result = first.add(second);

        for(int i = 2; i<number; i++){
            first = second;
            second = result;
            result = first.add(second);
        }

        return result;
    }
}
