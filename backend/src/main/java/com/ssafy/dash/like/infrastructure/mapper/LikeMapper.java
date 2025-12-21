package com.ssafy.dash.like.infrastructure.mapper;

import com.ssafy.dash.like.domain.BoardLike;
import com.ssafy.dash.like.domain.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface LikeMapper {

    // BoardLike
    void insertBoardLike(BoardLike boardLike);

    Optional<BoardLike> selectBoardLike(@Param("boardId") Long boardId, @Param("userId") Long userId);

    void deleteBoardLike(@Param("boardId") Long boardId, @Param("userId") Long userId);

    // CommentLike
    void insertCommentLike(CommentLike commentLike);

    Optional<CommentLike> selectCommentLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    void deleteCommentLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

}
