package com.jesus.inventory;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @GetMapping("/api/products")
    public List<Product> getProducts(){
        return List.of(
                new Product(1L, "Mechanical Keyboard",
                        new BigDecimal("79.99"), 12),
                new Product(2L, "Wireless mouse",
                        new BigDecimal("29.99"), 25),
                new Product(3L, "USB-C Hub",
                        new BigDecimal("49.99"), 8),
                new Product(4L, "Gaming Headset",
                        new BigDecimal("59.99"), 10)
        );
    }
}
