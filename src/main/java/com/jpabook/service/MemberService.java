package com.jpabook.service;

import com.jpabook.domain.Member;
import com.jpabook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // spring 트랜잭션을 써라. 왜냐면 쓸수있는 옵션들이 많기 때문에
@RequiredArgsConstructor // final 있는 필드만 생성자 만들어 준다.(선호)
public class MemberService {

    private final MemberRepository memberRepository;

    // 이상적
    /*@Autowired
    private final MemberRepository memberRepository;
    
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    /*
    * 회원가입
    */
    @Transactional // 기본옵션은 false 이다.(조회가 아닌거에는 false를 줘야됨)
    public Long join(Member member) {
        validationDuplidateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validationDuplidateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }


    }

    //회원 전체 조회
   // @Transactional(readOnly = true) <-- 해당 옵션주면 JPA가 성능 최적화 해준다.
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //한명만 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

}
