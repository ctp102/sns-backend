package world.meta.sns.core.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.board.form.BoardSearchForm;

public interface BoardRepositoryCustom {

    Page<Board> findAll(BoardSearchForm boardSearchForm, Pageable pageable);

}