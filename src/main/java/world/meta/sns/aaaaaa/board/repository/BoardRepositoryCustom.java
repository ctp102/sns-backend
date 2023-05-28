package world.meta.sns.aaaaaa.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.aaaaaa.board.dto.BoardDto;
import world.meta.sns.aaaaaa.board.form.BoardForm;

public interface BoardRepositoryCustom {

    Page<BoardDto> findAll(BoardForm boardForm, Pageable pageable);

}