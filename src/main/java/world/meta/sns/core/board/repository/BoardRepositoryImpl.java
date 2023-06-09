package world.meta.sns.core.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.board.form.BoardSearchForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static world.meta.sns.core.board.entity.QBoard.board;
import static world.meta.sns.core.member.entity.QMember.member;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<Board> findAll(BoardSearchForm boardSearchForm, Pageable pageable) {

        List<Board> boardDtos = queryFactory
                .selectFrom(board)
                .leftJoin(board.member, member)
                .where(
                        equalsWriter(boardSearchForm.getWriter()),
                        equalsTitle(boardSearchForm.getTitle()),
                        betweenCreatedDate(boardSearchForm.getStartDate(), boardSearchForm.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // TODO: [2023-04-15] Deprecated 된 메서드 사용하지 않기
        long totalCount = queryFactory
                .selectFrom(board)
                .leftJoin(board.member, member)
                .where(
                        equalsWriter(boardSearchForm.getWriter()),
                        equalsTitle(boardSearchForm.getTitle()),
                        betweenCreatedDate(boardSearchForm.getStartDate(), boardSearchForm.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchCount();

        return new PageImpl<>(boardDtos, pageable, totalCount);
    }

//    public Page<BoardDto> findAll(BoardSearchForm boardSearchForm, Pageable pageable) {
//
//        List<BoardDto> boardDtos = queryFactory
//                .select(new QBoardDto(board.id, board.title, board.content, member.name, board.createdDate, board.updatedDate))
//                .from(board)
//                .leftJoin(board.member, member)
//                .where(
//                        equalsWriter(boardSearchForm.getWriter()),
//                        equalsTitle(boardSearchForm.getTitle()),
//                        betweenCreatedDate(boardSearchForm.getStartDate(), boardSearchForm.getEndDate())
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // TODO: [2023-04-15] Deprecated 된 메서드 사용하지 않기
//        long totalCount = queryFactory
//                .select(Projections.constructor(
//                        BoardDto.class, board.id, board.title, board.content, member.name, board.createdDate, board.updatedDate)
//                )
//                .from(board)
//                .leftJoin(board.member, member)
//                .where(
//                        equalsWriter(boardSearchForm.getWriter()),
//                        equalsTitle(boardSearchForm.getTitle()),
//                        betweenCreatedDate(boardSearchForm.getStartDate(), boardSearchForm.getEndDate())
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchCount();
//
//        return new PageImpl<>(boardDtos, pageable, totalCount);
//    }

    // or 처리하기 위해 만들었는데 한계점이 존재
    // 만약 title, writer 조건 검색 시 and가 아닌 각각의 or 조건으로 조회함
    public BooleanBuilder searchCondition(BoardSearchForm boardSearchForm) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(boardSearchForm.getTitle())) {
            builder.or(board.title.eq(boardSearchForm.getTitle()));
        }

        if (StringUtils.isNotBlank(boardSearchForm.getWriter())) {
            builder.or(member.name.eq(boardSearchForm.getWriter()));
        }

        if (Objects.nonNull(boardSearchForm.getStartDate())) {
            builder.or(board.createdDate.goe(boardSearchForm.getStartDate()));
        }

        if (Objects.nonNull(boardSearchForm.getEndDate())) {
            builder.or(board.createdDate.loe(boardSearchForm.getEndDate()));
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
        return Objects.nonNull(startDate) ? new BooleanBuilder(board.createdDate.goe(startDate)) : new BooleanBuilder();
    }

    public BooleanBuilder createdDateLoe(LocalDateTime endDate) {
        return Objects.nonNull(endDate) ? new BooleanBuilder(board.createdDate.loe(endDate)) : new BooleanBuilder();
    }

}
