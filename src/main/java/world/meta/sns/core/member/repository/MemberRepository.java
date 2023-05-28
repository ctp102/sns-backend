package world.meta.sns.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.core.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Long countMemberByEmail(String email);

    Member findMemberByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("delete from Member m where m.id = :memberId")
    void deleteById(
            @Param("memberId") Long memberId
    );

}
