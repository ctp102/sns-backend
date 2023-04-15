package world.meta.sns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import world.meta.sns.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByMemberName(String memberName);

}
