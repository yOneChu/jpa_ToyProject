package com.jpabook.service;

import com.jpabook.domain.Address;
import com.jpabook.domain.Member;
import com.jpabook.domain.Order;
import com.jpabook.domain.OrderStatus;
import com.jpabook.domain.item.Book;
import com.jpabook.domain.item.Item;
import com.jpabook.exception.NotEnoughStockException;
import com.jpabook.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test // 상품주문
    public void order() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook("spring JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("사품주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 사품 종류 수가 정확해야한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 (가격 * 수량) 이다.", 10000*orderCount, getOrder.getTotalPrice());
        assertEquals("주문수량만큼 재고가 줄어야 한다", 8, book.getStockQuantity());
    }


    // 상품주문_재고수량 초과
    @Test(expected = NotEnoughStockException.class)
    public void over_OrderCount() {
        //given
        Member member = createMember();
        Item item = createBook("spring JPA", 10000, 10);

        int orderCount = 11;

        //when
        //주문수량을 초과해서 exception이 발생해야 한다.
        orderService.order(member.getId(), item.getId(), orderCount );

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    //주문취소
    @Test
    public void orderCancel() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook("spring jpa", 10000, 10);

        int orderCount = 2; // 2개 주문

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId); // 주문취소

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL 이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }


    // 파라미터 셋팅 : ctrl + alt + p
    // 생성자 만들기 : ctrl + alt + m
    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강남", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

}