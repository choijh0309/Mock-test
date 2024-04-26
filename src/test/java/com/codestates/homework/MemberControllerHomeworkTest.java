package com.codestates.homework;

import com.codestates.helper.StubData;
import com.codestates.member.dto.MemberDto;
import com.codestates.member.entity.Member;
import com.codestates.member.mapper.MemberMapper;
import com.codestates.member.service.MemberService;
import com.codestates.stamp.Stamp;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private MemberService memberService;

    @Autowired
    private MemberMapper mapper;

    @Test
    void postMemberTest() throws Exception {
        // TODO 연습용으로 추가해서 해봅니다.^^;
        // given
        MemberDto.Post post = new MemberDto.Post(
                "hgd@gmail.com",
                "홍길동",
                "010-1111-1111");

        given(mapper.memberPostToMember(Mockito.any(MemberDto.Post.class)))
                .willReturn(new Member());

        Member mockResultMember = new Member();
        mockResultMember.setMemberId(1L);
        given(memberService.createMember(Mockito.any(Member.class)))
                .willReturn(mockResultMember);

        String content = gson.toJson(post);
        URI uri = UriComponentsBuilder.newInstance().path("v11/members/").build().toUri();

        // when / then
        ResultActions actions =
                mockMvc.perform(
                                MockMvcRequestBuilders
                                        .post(uri)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(content))
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Location", is(startsWith("/v11/members/"))));
    }

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;

        MemberDto.Patch patch =
                (MemberDto.Patch) StubData.MockMember.getRequestBody(HttpMethod.PATCH);

        MemberDto.Response response = new MemberDto.Response(1L,
                "hgd@gmail.com",
                "홍길동",
                "010-2222-2222",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());

        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class)))
                .willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class)))
                .willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        Gson gson = new Gson();
        String content = gson.toJson(patch);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}")
                .buildAndExpand(memberId).toUri();

        // when / then
        ResultActions actions =
                mockMvc.perform(
                                MockMvcRequestBuilders
                                        .patch(uri)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(content))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.phone").value(patch.getPhone()));
    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;
        Member member = new Member("hgd@gmail.com", "홍길동", "010-1111-1111");
        member.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        MemberDto.Response response = new MemberDto.Response(1L,
                "hgd@gmail.com",
                "홍길동",
                "010-1111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());

        given(memberService.findMember(Mockito.anyLong()))
                .willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}")
                .buildAndExpand(memberId).toUri();

        // when / then
        ResultActions actions =
                mockMvc.perform(
                                MockMvcRequestBuilders
                                        .get(uri)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.email").value(member.getEmail()))
                        .andExpect(jsonPath("$.data.name").value(member.getName()))
                        .andExpect(jsonPath("$.data.phone").value(member.getPhone()));
    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        Member member1 = new Member("hgd1@gmail.com", "홍길동1", "010-1111-1111");
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member1.setStamp(new Stamp());

        Member member2 = new Member("hgd2@gmail.com", "홍길동2", "010-2222-2222");
        member2.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member2.setStamp(new Stamp());

        Page<Member> pageMembers =
                new PageImpl<>(List.of(member1, member2),
                        PageRequest.of(0, 10,
                                Sort.by("memberId").descending()), 2);

        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(1L,
                        "hgd1@gmail.com",
                        "홍길동1",
                        "010-1111-1111",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()),
                new MemberDto.Response(2L,
                        "hgd2@gmail.com",
                        "홍길동2",
                        "010-2222-2222",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp())
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).
                willReturn(responses);

        String page = "1";
        String size = "10";
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", page);
        queryParams.add("size", size);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members").build().toUri();

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(uri)
                                .params(queryParams)
                                .accept(MediaType.APPLICATION_JSON));

        // then
        MvcResult result = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(), is(2));
    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;

        doNothing().when(memberService).deleteMember(memberId);

        // when / then
        ResultActions deleteActions =
                mockMvc.perform(
                                delete("/v11/members/" + memberId)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
    }
}
