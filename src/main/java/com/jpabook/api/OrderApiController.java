package com.jpabook.api;

import com.jpabook.domain.Address;
import com.jpabook.domain.Order;
import com.jpabook.domain.OrderItem;
import com.jpabook.domain.OrderStatus;
import com.jpabook.repository.OrderRepository;
import com.jpabook.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                            .map(o -> new OrderDto(o))
                            .collect(Collectors.toList());

        return result;
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //private List<OrderItem> orderItems; // <-- OrderItem 도 DTO로 바꿔줘야한다.
        private List<OrderItemDto> orderItems;

        // DTO안에 엔티티가 있으면 안된다.
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            //order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            //orderItems = order.getOrderItems();

            //DTO로 변환
            orderItems = order.getOrderItems().stream()
                            .map(orderItem -> new OrderItemDto(orderItem))
                            .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName; //상품명
        private int orderPrice; // 주문가격
        private int count; // 주문수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
