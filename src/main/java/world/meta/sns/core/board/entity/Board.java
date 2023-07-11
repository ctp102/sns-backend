package world.meta.sns.core.board.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import world.meta.sns.core.board.dto.BoardRequestDto;
import world.meta.sns.core.board.dto.BoardUpdateDto;
import world.meta.sns.core.comment.entity.Comment;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.api.common.entity.BaseTimeEntity;
import world.meta.sns.core.board.enums.Category;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @BatchSize(size = 500)
    private List<Comment> comments = new ArrayList<>();

    public Board(String title, String content, Category category, Member member) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setBoard(this);
    }

    // TODO: [2023-04-28] 추후 컨트롤러 단에서 validation 처리하기 
    public void update(BoardUpdateDto boardUpdateDto) {

        if (StringUtils.isNotBlank(boardUpdateDto.getTitle())) {
            this.title = boardUpdateDto.getTitle();
        }

        if (StringUtils.isNotBlank(boardUpdateDto.getContent())) {
            this.content = boardUpdateDto.getContent();
        }

        if (Objects.nonNull(boardUpdateDto.getCategory())) {
            this.category = boardUpdateDto.getCategory();
        }
    }

    public static Board from(BoardRequestDto boardRequestDto) {
        return Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .category(boardRequestDto.getCategory())
                .build();
    }

}
