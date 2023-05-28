package world.meta.sns.aaaaaa.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import world.meta.sns.aaaaaa.member.entity.Member;
import world.meta.sns.aaaaaa.member.form.MemberSearchForm;

import java.util.List;

import static world.meta.sns.entity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<Member> findAll(MemberSearchForm memberSearchForm, Pageable pageable) {

        List<Member> members = queryFactory
                .selectFrom(member)
                .where(equalsName(memberSearchForm.getName()))
//                .where(
//                        likeEmail(memberSearchForm.getEmail()),
//                        likeName(memberSearchForm.getName())
//                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // TODO: [2023-04-15] Deprecated 된 메서드 사용하지 않기
        long totalCount = queryFactory
                .selectFrom(member)
                .where(equalsName(memberSearchForm.getName()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchCount();

        return new PageImpl<>(members, pageable, totalCount);
    }

    public BooleanExpression equalsName(String name) {
        return StringUtils.isNotBlank(name) ? member.name.eq(name) : null;
    }

    public BooleanExpression likeName(String name) {
        return StringUtils.isNotBlank(name) ? member.name.like(name + "%") : null;
    }

    public BooleanExpression likeEmail(String email) {
        return StringUtils.isNotBlank(email) ? member.email.like(email + "%") : null;
    }

}
