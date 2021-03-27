package com.jpabook.api;

import com.jpabook.domain.Address;
import com.jpabook.domain.Order;
import com.jpabook.domain.OrderStatus;
import com.jpabook.repository.OrderRepository;
import com.jpabook.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;

        // 무한루프가 돈다.
        // why? Order -> Member -> Orders -> Member.... 서로 연결되어있으니 양방향 연관관계 문제가 생긴다.
        // 양방향이면 한쪽은 @JsonIgnore을 해줘야 한다.
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                                    .map(o -> new SimpleOrderDto(o))
                                    .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }

    }




}

