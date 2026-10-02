package com.jesus.inventory;

import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<Map<String, Long>> createOrder(@RequestBody CreateOrderRequest request){
        long orderId = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("orderId", orderId));
    }

    @GetMapping
    public List<Order> getOrders(){
        return orderService.getOrders();
    }

    @PatchMapping("/{orderId}/ship")
    public ResponseEntity<Void> shipOrder(
            @PathVariable("orderId") long orderId){
        orderService.shipOrder(orderId);

        return ResponseEntity.noContent().build();
    }


}
