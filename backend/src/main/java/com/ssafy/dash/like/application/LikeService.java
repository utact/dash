package com.ssafy.dash.like.application;

import com.ssafy.dash.board.domain.Board;
import com.ssafy.dash.board.domain.BoardRepository;
import com.ssafy.dash.board.domain.exception.BoardNotFoundException;
import com.ssafy.dash.comment.domain.Comment;
import com.ssafy.dash.comment.domain.CommentRepository;
import com.ssafy.dash.comment.domain.exception.CommentNotFoundException;
import com.ssafy.dash.like.domain.BoardLike;
import com.ssafy.dash.like.domain.CommentLike;
import com.ssafy.dash.like.domain.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public LikeService(LikeRepository likeRepository, BoardRepository boardRepository,
            CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public int likeBoardToggle(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        var existingLike = likeRepository.findBoardLike(boardId, userId);

        if (existingLike.isPresent()) {
            // 이미 추천한 경우 -> 추천 취소
            likeRepository.deleteBoardLike(boardId, userId);
            board.decrementLikeCount();
        } else {
            // 추천하지 않은 경우 -> 추천
            BoardLike like = BoardLike.create(boardId, userId, LocalDateTime.now());
            likeRepository.saveBoardLike(like);
            board.incrementLikeCount();
        }

        boardRepository.update(board);
        return board.getLikeCount();
    }

    @Transactional
    public int likeBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        if (likeRepository.findBoardLike(boardId, userId).isEmpty()) {
            BoardLike like = BoardLike.create(boardId, userId, LocalDateTime.now());
            likeRepository.saveBoardLike(like);
            board.incrementLikeCount();
            boardRepository.update(board);
        }

        return board.getLikeCount();
    }

    @Transactional
    public int unlikeBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        if (likeRepository.findBoardLike(boardId, userId).isPresent()) {
            likeRepository.deleteBoardLike(boardId, userId);
            board.decrementLikeCount();
            boardRepository.update(board);
        }

        return board.getLikeCount();
    }

    @Transactional
    public int likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (likeRepository.findCommentLike(commentId, userId).isEmpty()) {
            CommentLike like = CommentLike.create(commentId, userId, LocalDateTime.now());
            likeRepository.saveCommentLike(like);
            comment.incrementLikeCount();
            commentRepository.update(comment);
        }

        return comment.getLikeCount();
    }

    @Transactional
    public int unlikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (likeRepository.findCommentLike(commentId, userId).isPresent()) {
            likeRepository.deleteCommentLike(commentId, userId);
            comment.decrementLikeCount();
            commentRepository.update(comment);
        }

        return comment.getLikeCount();
    }

    @Transactional(readOnly = true)
    public boolean isLikedBoard(Long boardId, Long userId) {
        return likeRepository.findBoardLike(boardId, userId).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isLikedComment(Long commentId, Long userId) {
        return likeRepository.findCommentLike(commentId, userId).isPresent();
    }

}
