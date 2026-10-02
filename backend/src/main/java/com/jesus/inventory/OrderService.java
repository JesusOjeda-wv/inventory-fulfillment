package com.jesus.inventory;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository
    ){
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public long createOrder(CreateOrderRequest request){
        if(request.productId() == null || request.productId() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Product not found"
                ));

        boolean reduced = productRepository.reduceStock(
                product.id(),
                request.quantity()
        );

        if(!reduced){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Not enough stock to complete this order."
        );
        }
        return orderRepository.create(
                product.id(),
                request.quantity(),
                product.price()
        );
    }
    public List<Order> getOrders(){
        return orderRepository.findAll();
    }

    public void shipOrder(long orderId){
        if(orderId <= 0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID must be positive."
            );
        }
        boolean shipped = orderRepository.markShipped(orderId);

        if(!shipped){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Order does not exist or is no longer pending."
            );
        }
    }

}
