package com.ssafy.dash.like.infrastructure.persistence;

import com.ssafy.dash.like.domain.BoardLike;
import com.ssafy.dash.like.domain.CommentLike;
import com.ssafy.dash.like.domain.LikeRepository;
import com.ssafy.dash.like.infrastructure.mapper.LikeMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeMapper likeMapper;

    public LikeRepositoryImpl(LikeMapper likeMapper) {
        this.likeMapper = likeMapper;
    }

    @Override
    public void saveBoardLike(BoardLike boardLike) {
        likeMapper.insertBoardLike(boardLike);
    }

    @Override
    public Optional<BoardLike> findBoardLike(Long boardId, Long userId) {
        return likeMapper.selectBoardLike(boardId, userId);
    }

    @Override
    public void deleteBoardLike(Long boardId, Long userId) {
        likeMapper.deleteBoardLike(boardId, userId);
    }

    @Override
    public void saveCommentLike(CommentLike commentLike) {
        likeMapper.insertCommentLike(commentLike);
    }

    @Override
    public Optional<CommentLike> findCommentLike(Long commentId, Long userId) {
        return likeMapper.selectCommentLike(commentId, userId);
    }

    @Override
    public void deleteCommentLike(Long commentId, Long userId) {
        likeMapper.deleteCommentLike(commentId, userId);
    }

}
