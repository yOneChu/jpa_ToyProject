package com.jpabook.api;

import com.jpabook.domain.Member;
import com.jpabook.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 수정 API
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
                                    @PathVariable("id") Long id,
                                    @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName()); //  @AllArgsConstructor 있어서 이렇게 생성함
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    /**
     * 멤버조회
     * 문제점 : 엔티티 모든것이 노출된다.
     * @return
     */
    @GetMapping("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                            .map(m -> new MemberDto(m.getName()))
                            .collect(Collectors.toList());
        // API 파라미터 받을때나 보낼때 엔티티를 사용하지 말아라.
        // DTO를 만들어서 필요한 정보만 노출해야 한다.
        // Result로 감싸서 보내기 때문에 유연성이 생긴다. return new Result(collect.size(), collect);
        return  new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }





}
