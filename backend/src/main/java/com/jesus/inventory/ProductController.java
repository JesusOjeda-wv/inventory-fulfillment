package com.jesus.inventory;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @GetMapping("/api/products")
    public List<Product> getProducts(){
        return productRepository.findAll();
    }
    @PutMapping("/api/products/{id}/stock")
    public ResponseEntity<String> updateStock(
            @PathVariable("id") long id,
            @RequestBody UpdateStockRequest request
            ){
        if(request.stock() == null || request.stock() < 0){
            return ResponseEntity.badRequest().body("Stock is required and cannot be negative");
        }
        int updateRows = productRepository.updateStock(id, request.stock());
        if(updateRows == 0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Stock updated.");
    }
}
