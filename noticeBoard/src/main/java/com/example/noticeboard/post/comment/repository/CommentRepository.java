package com.example.noticeboard.post.comment.repository;

import com.example.noticeboard.post.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> , CustomCommentRepository {

}
