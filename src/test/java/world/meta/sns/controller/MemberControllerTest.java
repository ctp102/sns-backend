package world.meta.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import world.meta.sns.aaaaaa.member.dto.MemberJoinDto;
import world.meta.sns.aaaaaa.member.dto.MemberUpdateDto;
import world.meta.sns.aaaaaa.member.form.MemberSearchForm;
import world.meta.sns.aaaaaa.member.service.MemberService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@SpringBootTest
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 목록 조회")
    void findMemberList() throws Exception {
        MemberSearchForm memberSearchForm = new MemberSearchForm();

        mockMvc.perform(get("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSearchForm)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원 단건 조회")
    void findMember() throws Exception {
        mockMvc.perform(get("/api/v1/members/1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원 등록")
    void saveMember() throws Exception {
        String email = "test999@naver.com";
        String password = "test";
        String role = "ROLE_USER";
        MemberJoinDto memberJoinDto = new MemberJoinDto(email, password, role);

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberJoinDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원 수정")
    void updateMember() throws Exception {
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();
        memberUpdateDto.setName("테스트999를 888로");

        mockMvc.perform(put("/api/v1/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberUpdateDto)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception {
        mockMvc.perform(delete("/api/v1/members/1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
