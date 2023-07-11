package world.meta.sns.core.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.meta.sns.api.exception.CustomNotFoundException;
import world.meta.sns.core.board.dto.BoardDto;
import world.meta.sns.core.board.dto.BoardRequestDto;
import world.meta.sns.core.board.dto.BoardUpdateDto;
import world.meta.sns.core.board.entity.Board;
import world.meta.sns.core.board.form.BoardSearchForm;
import world.meta.sns.core.board.repository.BoardRepository;
import world.meta.sns.core.comment.repository.CommentRepository;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.BOARD_NOT_FOUND;
import static world.meta.sns.api.common.enums.ErrorResponseCodes.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    /**
     * 게시글 목록 조회
     *
     * @param boardSearchForm the board form
     * @param pageable  the pageable
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<BoardDto> findBoardList(BoardSearchForm boardSearchForm, Pageable pageable) {

        Page<Board> pageBoards = boardRepository.findAll(boardSearchForm, pageable);

        // TODO: [2023-04-17] 개수가 많은 경우에는 stream.parallel() 처리 고려
        List<BoardDto> boardDtos = new ArrayList<>();

        pageBoards.getContent().forEach(board -> {
            BoardDto boardDto = BoardDto.from(board);
            BoardDto.setCommentDtos(board, boardDto);
            boardDtos.add(boardDto);
        });

        return new PageImpl<>(boardDtos, pageable, pageBoards.getTotalElements());
    }

    /**
     * 게시글 단건 조회
     *
     * @param boardId the board id
     * @return the board dto
     */
    @Transactional(readOnly = true)
    public BoardDto findBoard(Long boardId) {

        Board foundBoard = boardRepository.findFetchJoinById(boardId);

        if (Objects.isNull(foundBoard)) {
            log.error("[findBoard] 해당 게시글을 찾을 수 없습니다. boardId: {}", boardId);
            throw new CustomNotFoundException(BOARD_NOT_FOUND.getNumber(), BOARD_NOT_FOUND.getMessage());
        }
        BoardDto boardDto = BoardDto.from(foundBoard);
        BoardDto.setCommentDtos(foundBoard, boardDto);

        return boardDto;
    }

    /**
     * 게시글 등록
     *
     * @param requestDto the request dto
     * @return the board
     */
    public Board saveBoard(BoardRequestDto requestDto) {

        Member foundMember = memberRepository.findById(requestDto.getMemberId()).orElse(null);
        if (Objects.isNull(foundMember)) {
            log.error("[saveBoard] 해당 사용자를 찾을 수 없습니다. memberId: {}", requestDto.getMemberId());
            throw new CustomNotFoundException(MEMBER_NOT_FOUND.getNumber(), MEMBER_NOT_FOUND.getMessage());
        }
        Board board = Board.from(requestDto);
        foundMember.addBoard(board);

        return boardRepository.save(board);
    }

    /**
     * 게시글 수정
     *
     * @param boardId        the board id
     * @param boardUpdateDto the board update dto
     */
    public void updateBoard(Long boardId, BoardUpdateDto boardUpdateDto) {

        Board foundBoard = boardRepository.findByIdAndMemberId(boardId, boardUpdateDto.getMemberId());
        if (Objects.isNull(foundBoard)) {
            log.error("[updateBoard] 해당 게시글을 찾을 수 없습니다. boardId: {}, memberId: {}", boardId, boardUpdateDto.getMemberId());
            throw new CustomNotFoundException(BOARD_NOT_FOUND.getNumber(), BOARD_NOT_FOUND.getMessage());
        }

        foundBoard.update(boardUpdateDto);
    }

    /**
     * 게시글 삭제
     * 작성된 댓글도 함께 삭제
     *
     * @param boardId the board id
     */
    public void deleteBoard(Long boardId, Long memberId) {

        Board foundBoard = boardRepository.findByIdAndMemberId(boardId, memberId);
        if (Objects.isNull(foundBoard)) {
            log.error("[deleteBoard] 해당 게시글을 찾을 수 없습니다. boardId: {}, memberId: {}", boardId, memberId);
            throw new CustomNotFoundException(BOARD_NOT_FOUND.getNumber(), BOARD_NOT_FOUND.getMessage());
        }

        List<Long> parentCommentIds = commentRepository.findParentCommentIdsByBoardId(boardId);
        commentRepository.deleteChildCommentsByParentCommentIds(parentCommentIds); // 자식 댓글부터 삭제해야 참조 무결성이 깨지지 않는다.
        commentRepository.deleteCommentsByBoardId(boardId); // 부모 댓글 삭제

        boardRepository.deleteByBoardId(boardId);
    }

}
