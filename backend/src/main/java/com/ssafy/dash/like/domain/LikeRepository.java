package com.ssafy.dash.like.domain;

import java.util.Optional;

public interface LikeRepository {

    // BoardLike
    void saveBoardLike(BoardLike boardLike);

    Optional<BoardLike> findBoardLike(Long boardId, Long userId);

    void deleteBoardLike(Long boardId, Long userId);

    // CommentLike
    void saveCommentLike(CommentLike commentLike);

    Optional<CommentLike> findCommentLike(Long commentId, Long userId);

    void deleteCommentLike(Long commentId, Long userId);

}
