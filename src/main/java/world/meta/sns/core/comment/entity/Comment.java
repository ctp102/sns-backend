package world.meta.sns.core.comment.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import world.meta.sns.core.comment.dto.CommentUpdateDto;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.api.common.entity.BaseTimeEntity;
import world.meta.sns.core.board.entity.Board;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> childComments;

    public void updateParentComment(Comment parentComment) {

        this.parentComment = parentComment;
        parentComment.getChildComments().add(this);
    }

    public void update(Comment comment) {

        if (StringUtils.isNotBlank(comment.getContent())) {
            this.content = comment.getContent();
        }
    }

    public void update(CommentUpdateDto commentUpdateDto) {
        this.content = commentUpdateDto.getContent();
    }

}
