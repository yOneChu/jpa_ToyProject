package com.jpabook.repository;

import com.jpabook.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item);
        }else{
            em.merge(item); // 강제로 업데이트
        }
    }

    // 1건 검색
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    // 전체 검색
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class) //JPQL
                .getResultList();
    }

}
