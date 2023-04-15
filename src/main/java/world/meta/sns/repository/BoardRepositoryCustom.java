package world.meta.sns.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.dto.BoardDto;
import world.meta.sns.form.BoardForm;

public interface BoardRepositoryCustom {

    Page<BoardDto> findAll(BoardForm boardForm, Pageable pageable);

}