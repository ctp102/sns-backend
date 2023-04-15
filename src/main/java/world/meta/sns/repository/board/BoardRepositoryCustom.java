package world.meta.sns.repository.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.dto.board.BoardDto;
import world.meta.sns.form.board.BoardForm;

public interface BoardRepositoryCustom {

    Page<BoardDto> findAll(BoardForm boardForm, Pageable pageable);

}