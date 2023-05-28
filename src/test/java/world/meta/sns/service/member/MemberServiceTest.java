package world.meta.sns.service.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import world.meta.sns.core.member.dto.MemberBoardDto;
import world.meta.sns.core.member.dto.MemberDto;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.board.repository.BoardRepository;
import world.meta.sns.core.comment.repository.CommentRepository;
import world.meta.sns.core.member.repository.MemberRepository;
import world.meta.sns.core.member.service.MemberService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("회원 단건 조회")
    void findMember() throws Exception {

        // given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setEmail("test@example.com");
        member.setName("John Doe");
        Board board = new Board();
        board.setId(1L);
        board.setTitle("Board 1");
        board.setContent("Content 1");
        board.setMember(member);
        member.getBoards().add(board);

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        MemberDto result = memberService.findMember(memberId);

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getName()).isEqualTo(member.getName());
        assertThat(result.getMemberBoardDtos()).hasSize(1);
        MemberBoardDto boardDto = result.getMemberBoardDtos().get(0);
        assertThat(boardDto.getBoardId()).isEqualTo(board.getId());
        assertThat(boardDto.getTitle()).isEqualTo(board.getTitle());
        assertThat(boardDto.getContent()).isEqualTo(board.getContent());
        assertThat(boardDto.getWriter()).isEqualTo(board.getMember().getName());

        // verify
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원 등록")
    public void saveMemberTest() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setEmail("test@example.com");
        member.setName("John Doe");

        // stub
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        MemberDto result = memberService.findMember(memberId);

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getName()).isEqualTo(member.getName());

        // verify
        verify(memberRepository, times(1)).findById(memberId);
    }

}
