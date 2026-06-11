package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreateOrderRequest;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.Order;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.OrderRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserMembershipRepository membershipRepository;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        // Snapshot the active tier at order time for benefit audit trail (nullable — non-members can order)
        MembershipTier activeTier = membershipRepository
                .findByUser_IdAndStatus(request.getUserId(), MembershipStatus.ACTIVE)
                .map(m -> m.getTier())
                .orElse(null);

        Order order = Order.builder()
                .user(user)
                .totalAmount(request.getTotalAmount())
                .description(request.getDescription())
                .appliedTier(activeTier)
                .build();
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
