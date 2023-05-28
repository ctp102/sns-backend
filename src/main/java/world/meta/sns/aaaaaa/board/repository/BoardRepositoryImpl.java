package world.meta.sns.aaaaaa.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import world.meta.sns.aaaaaa.board.dto.BoardDto;
import world.meta.sns.dto.board.QBoardDto;
import world.meta.sns.aaaaaa.board.form.BoardForm;

import java.time.LocalDateTime;
import java.util.List;

import static world.meta.sns.entity.QBoard.board;
import static world.meta.sns.entity.QMember.*;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<BoardDto> findAll(BoardForm boardForm, Pageable pageable) {

        List<BoardDto> boardDtos = queryFactory
                .select(new QBoardDto(board.id, board.title, board.content, member.name, board.createdDate, board.updatedDate))
                .from(board)
                .leftJoin(board.member, member)
                .where(
                        equalsWriter(boardForm.getWriter()),
                        equalsTitle(boardForm.getTitle()),
                        betweenCreatedDate(boardForm.getStartDate(), boardForm.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // TODO: [2023-04-15] Deprecated 된 메서드 사용하지 않기
        long totalCount = queryFactory
                .select(Projections.constructor(
                        BoardDto.class, board.id, board.title, board.content, member.name, board.createdDate, board.updatedDate)
                )
                .from(board)
                .leftJoin(board.member, member)
                .where(
                        equalsWriter(boardForm.getWriter()),
                        equalsTitle(boardForm.getTitle()),
                        betweenCreatedDate(boardForm.getStartDate(), boardForm.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchCount();

        return new PageImpl<>(boardDtos, pageable, totalCount);
    }

    // or 처리하기 위해 만들었는데 한계점이 존재
    // 만약 title, writer 조건 검색 시 and가 아닌 각각의 or 조건으로 조회함
    public BooleanBuilder searchCondition(BoardForm boardForm) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(boardForm.getTitle())) {
            builder.or(board.title.eq(boardForm.getTitle()));
        }

        if (StringUtils.isNotBlank(boardForm.getWriter())) {
            builder.or(member.name.eq(boardForm.getWriter()));
        }

        if (boardForm.getStartDate() != null) {
            builder.or(board.createdDate.goe(boardForm.getStartDate()));
        }

        if (boardForm.getEndDate() != null) {
            builder.or(board.createdDate.loe(boardForm.getEndDate()));
        }

        return builder;
    }

    public BooleanExpression equalsWriter(String writer) {
        return StringUtils.isNotBlank(writer) ? member.name.eq(writer) : null;
    }

    public BooleanExpression equalsTitle(String title) {
        return StringUtils.isNotBlank(title) ? board.title.eq(title) : null;
    }

    public BooleanBuilder betweenCreatedDate(LocalDateTime startDate, LocalDateTime endDate) {
        return createdDateGoe(startDate).and(createdDateLoe(endDate));
    }

    public BooleanBuilder createdDateGoe(LocalDateTime startDate) {
        return startDate != null ? new BooleanBuilder(board.createdDate.goe(startDate)) : new BooleanBuilder();
    }

    public BooleanBuilder createdDateLoe(LocalDateTime endDate) {
        return endDate != null ? new BooleanBuilder(board.createdDate.loe(endDate)) : new BooleanBuilder();
    }

}
