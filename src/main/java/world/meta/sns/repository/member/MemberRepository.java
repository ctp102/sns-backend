package world.meta.sns.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import world.meta.sns.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Long countMemberByMemberName(String memberName);

    @Modifying(clearAutomatically = true)
    @Query("delete from Member m where m.id = :memberId")
    void deleteAllById(
            @Param("memberId") Long memberId
    );

}
