package com.jpabook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jpabook.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    private int orderPrice; //주문가격
    private int count; //주문수량

    // protected OrderItem() { } // @NoArgsConstructor(access = AccessLevel.PROTECTED)와 같은 의미이다.

    //== 생성 메서드 == //
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 원래 재고에서 빼준다.
        return orderItem;
    }


    // == 비즈니스 로직 == //
    /**
     *  재고수량은 원복해준다.
     */
    public void cancel() {
        getItem().addStock(count);
    }

    /**
     * 주문상품 전체 가격 조회
     * @return
     */
    public int getTotalPrice() {
        return getOrderPrice() * count;
    }

}

