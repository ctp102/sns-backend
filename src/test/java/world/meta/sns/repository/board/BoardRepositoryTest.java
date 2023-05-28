package world.meta.sns.repository.board;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.aaaaaa.board.dto.BoardDto;
import world.meta.sns.aaaaaa.board.form.BoardForm;
import world.meta.sns.aaaaaa.board.repository.BoardRepository;
import world.meta.sns.aaaaaa.member.repository.MemberRepository;

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
