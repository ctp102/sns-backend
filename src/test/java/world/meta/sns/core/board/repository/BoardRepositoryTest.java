package world.meta.sns.core.board.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.board.form.BoardSearchForm;
import world.meta.sns.core.member.repository.MemberRepository;

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
        BoardSearchForm boardSearchForm = new BoardSearchForm();
        boardSearchForm.setTitle("title2");

        // when
        Page<Board> pageBoards = boardRepository.findAll(boardSearchForm, PageRequest.of(1, 2));

        for (Board board : pageBoards) {
            log.info("board = {}", board);
        }

        // then
    }

}
