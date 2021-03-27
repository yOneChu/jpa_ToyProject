package com.jpabook.repository;

import com.jpabook.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m" +
                        " where o.status = :status" +
                        " and m.name like :name",Order.class)
                        .setParameter("status", orderSearch.getOrderStatus())
                        .setParameter("name", orderSearch.getMemberName())
                        .setMaxResults(1000) // 최대 1000건
                        .getResultList();
    }


    public List<Order> findAllByString(OrderSearch orderSearch) {

        //language = JPQL
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문상태 검색
        if(orderSearch.getOrderStatus() != null) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if(StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }


    // fetch join을 통한 성능 최적화
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order  o " +
                                " join fetch o.member m " +
                                " join fetch  o.delivery d", Order.class)
                                .getResultList();
    }

    // V2와 V3은 똑같지만 쿼리가 다르다.
    // fetch join은 확실하게 공부해서 적극 활용해라!!
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                            .map(o -> new SimpleOrderDto(o))
                            .collect(Collectors.toList());
        return result;
    }



}
