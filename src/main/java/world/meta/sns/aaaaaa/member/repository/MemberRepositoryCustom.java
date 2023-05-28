package world.meta.sns.aaaaaa.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import world.meta.sns.aaaaaa.member.entity.Member;
import world.meta.sns.aaaaaa.member.form.MemberSearchForm;

public interface MemberRepositoryCustom {

    Page<Member> findAll(MemberSearchForm memberSearchForm, Pageable pageable);

}
