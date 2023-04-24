package world.meta.sns.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import world.meta.sns.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Long countMemberByMemberName(String memberName);

}
