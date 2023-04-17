package world.meta.sns.repository.board;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.dto.board.BoardDto;
import world.meta.sns.entity.Board;
import world.meta.sns.form.board.BoardForm;
import world.meta.sns.repository.board.BoardRepository;
import world.meta.sns.repository.member.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void findAll() throws Exception {

        // given
        BoardForm boardForm = new BoardForm();
        boardForm.setTitle("title2");

        // when
        Page<BoardDto> result = boardRepository.findAll(boardForm, PageRequest.of(1, 2));

        for (BoardDto boardDto : result) {
            log.info("boardDto = {}", boardDto);
        }

        // then
    }

}
