package com.jpabook.service;

import com.jpabook.domain.Delivery;
import com.jpabook.domain.Member;
import com.jpabook.domain.Order;
import com.jpabook.domain.OrderItem;
import com.jpabook.domain.item.Item;
import com.jpabook.repository.ItemRepository;
import com.jpabook.repository.MemberRepository;
import com.jpabook.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        // order만 persis해준다.
        // whey? 원래는 각 delivery, orderitem을 persis해줘야 하지만 Order 엔티티의 각 컬럼에 cascade = CascadeType.ALL
        // 옵션이 붙어서 order을 persis하면 각 자동으로 persis되어진다.
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문취소
        order.cancel(); // 한줄로 끝난다.
    }

    //검색
    //public List<Order> findOrders
}
