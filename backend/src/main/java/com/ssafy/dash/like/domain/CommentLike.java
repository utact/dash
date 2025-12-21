package com.ssafy.dash.like.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentLike {

    private Long id;
    private Long commentId;
    private Long userId;
    private LocalDateTime createdAt;

    private CommentLike(Long commentId, Long userId, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static CommentLike create(Long commentId, Long userId, LocalDateTime createdAt) {
        return new CommentLike(commentId, userId, createdAt);
    }

}
