package com.jpabook.service;

import com.jpabook.domain.Member;
import com.jpabook.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired
    EntityManager em;


    //회원가입
    @Test
    public void save() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then : db에 저장한거와 해당 member와 같나 체크
        em.flush(); //db에 강제로 쿼리를 날린다.
        assertEquals(member, memberRepository.findOne(savedId));
    }
    
    //중복회원 예외
    @Test(expected = IllegalStateException.class)
    public void duplicate() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다!

        //then
        fail("예외가 발생해야 한다");
    }
    

}