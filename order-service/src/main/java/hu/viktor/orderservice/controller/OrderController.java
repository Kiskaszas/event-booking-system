package hu.viktor.orderservice.controller;

import hu.viktor.orderservice.dto.CreateOrderRequest;
import hu.viktor.orderservice.dto.OrderResponse;
import hu.viktor.orderservice.model.Order;
import hu.viktor.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order savedOrder = orderService.placeOrder(request);
        return OrderResponse.fromEntity(savedOrder);
    }
}