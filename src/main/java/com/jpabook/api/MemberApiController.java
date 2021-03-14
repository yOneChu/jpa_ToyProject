package com.jpabook.api;

import com.jpabook.domain.Member;
import com.jpabook.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController// = @Controller + @ResponseBody을 합친거다
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 엔티티를 받지발고 보내지도 말라!!
    // api스펙을 위한 별도의 DTO를 만들어 사용하는게 제이 좋다!!

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // @RequestBody는 해당 컨트롤러로 날라온 json데이터를 Member로 변환해준다.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    // v2
    // Member 엔티티바꾸면 다 에러가난다.
    // 장점 : api 스펙이 바뀌지 않는다.
    /**
     * 등록
     * @param request
     * @return
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
