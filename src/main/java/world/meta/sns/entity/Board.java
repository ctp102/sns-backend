package world.meta.sns.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import world.meta.sns.dto.board.BoardRequestDto;
import world.meta.sns.dto.board.BoardUpdateDto;
import world.meta.sns.enums.Category;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private List<Comment> comments;

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

        if (boardUpdateDto.getCategory() != null) {
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
