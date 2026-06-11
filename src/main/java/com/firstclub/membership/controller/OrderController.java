package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreateOrderRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.OrderResponse;
import com.firstclub.membership.entity.Order;
import com.firstclub.membership.service.OrderService;
import com.firstclub.membership.service.UserMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserMembershipService membershipService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        tryAutoEvaluateTier(request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order created", OrderResponse.from(order)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getUserOrders(userId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    // Best-effort: tier re-evaluation is non-critical to the order itself.
    // Logs failures instead of swallowing them silently.
    private void tryAutoEvaluateTier(Long userId) {
        try {
            membershipService.evaluateAndAutoUpgrade(userId);
        } catch (Exception ex) {
            log.info("Tier re-evaluation skipped for user {}: {}", userId, ex.getMessage());
        }
    }
}
