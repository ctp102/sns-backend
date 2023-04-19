package world.meta.sns.repository.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.entity.Member;
import world.meta.sns.form.member.MemberSearchForm;

public interface MemberRepositoryCustom {

    Page<Member> findAll(MemberSearchForm memberSearchForm, Pageable pageable);

}
