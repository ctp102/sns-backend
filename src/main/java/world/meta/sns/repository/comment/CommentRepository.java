package world.meta.sns.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import world.meta.sns.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
