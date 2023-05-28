package world.meta.sns.core.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.core.board.dto.BoardDto;
import world.meta.sns.core.board.form.BoardForm;

public interface BoardRepositoryCustom {

    Page<BoardDto> findAll(BoardForm boardForm, Pageable pageable);

}