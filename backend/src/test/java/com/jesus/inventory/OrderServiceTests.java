package com.jesus.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class OrderServiceTests {

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void createOrderRejectsInvalidQuantity(Integer quantity) {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderService service = new OrderService(productRepository, orderRepository);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createOrder(new CreateOrderRequest(1L, quantity))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Quantity must be positive", exception.getReason());
        verifyNoInteractions(productRepository, orderRepository);
    }
}
