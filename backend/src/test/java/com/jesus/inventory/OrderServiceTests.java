package com.jesus.inventory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class OrderServiceTests {

    @Test
    void createOrderReducesStockAndSavesOrder() {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderService service = new OrderService(productRepository, orderRepository);
        BigDecimal price = new BigDecimal("19.99");
        Product product = new Product(1L, "Test product", price, 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.reduceStock(1L, 3)).thenReturn(true);
        when(orderRepository.create(1L, 3, price)).thenReturn(42L);

        long orderId = service.createOrder(new CreateOrderRequest(1L, 3));

        assertEquals(42L, orderId);
        InOrder inOrder = inOrder(productRepository, orderRepository);
        inOrder.verify(productRepository).reduceStock(1L, 3);
        inOrder.verify(orderRepository).create(1L, 3, price);
    }

    @Test
    void createOrderRejectsInsufficientStock() {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderService service = new OrderService(productRepository, orderRepository);
        Product product = new Product(1L, "Test product", new BigDecimal("19.99"), 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.reduceStock(1L, 3)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createOrder(new CreateOrderRequest(1L, 3))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Not enough stock to complete this order.", exception.getReason());
        verify(productRepository).reduceStock(1L, 3);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shipOrderMarksOrderShipped() {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderService service = new OrderService(productRepository, orderRepository);

        when(orderRepository.markShipped(42L)).thenReturn(true);

        assertDoesNotThrow(() -> service.shipOrder(42L));

        verify(orderRepository).markShipped(42L);
        verifyNoInteractions(productRepository);
    }

    @Test
    void shipOrderRejectsMissingOrNonPendingOrder() {
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderService service = new OrderService(productRepository, orderRepository);

        when(orderRepository.markShipped(42L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.shipOrder(42L)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Order does not exist or is no longer pending.", exception.getReason());
        verify(orderRepository).markShipped(42L);
        verifyNoInteractions(productRepository);
    }

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

    @Test
    void createOrderRejectsMissingProduct(){
        ProductRepository productRepository = mock(ProductRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderService service = new OrderService(productRepository, orderRepository);

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createOrder(new CreateOrderRequest(999L, 1))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(productRepository, never()).reduceStock(anyLong(), anyInt());
        verifyNoInteractions(orderRepository);

    }
}
