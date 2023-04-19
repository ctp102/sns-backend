package world.meta.sns.repository.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import world.meta.sns.entity.Member;
import world.meta.sns.form.member.MemberSearchForm;

import java.util.List;

import static world.meta.sns.entity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<Member> findAll(MemberSearchForm memberSearchForm, Pageable pageable) {

        List<Member> members = queryFactory
                .selectFrom(member)
                .where(equalsMemberName(memberSearchForm.getMemberName()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // TODO: [2023-04-15] Deprecated 된 메서드 사용하지 않기
        long totalCount = queryFactory
                .selectFrom(member)
                .where(equalsMemberName(memberSearchForm.getMemberName()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchCount();

        return new PageImpl<>(members, pageable, totalCount);
    }

    public BooleanExpression equalsMemberName(String memberName) {
        return StringUtils.isNotBlank(memberName) ? member.memberName.eq(memberName) : null;
    }

}
