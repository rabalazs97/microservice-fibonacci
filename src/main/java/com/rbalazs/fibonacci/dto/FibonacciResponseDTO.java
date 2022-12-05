package com.rbalazs.fibonacci.dto;

import java.io.Serializable;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FibonacciResponseDTO implements Serializable {
    int serialNumber;
    BigInteger fibonacciNumber;
}
