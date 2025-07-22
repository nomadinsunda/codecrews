package com.example.noticeboard.account.profile.repository;

import com.example.noticeboard.account.user.domain.QUser;
import com.example.noticeboard.post.comment.domain.QComment;
import com.example.noticeboard.post.post.domain.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.noticeboard.account.profile.dto.StatisticsResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ProfileRepositoryImpl implements CustomProfileRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public StatisticsResponse getStatisticsOfUser(String userId) {

        long totalPost = queryFactory.select(QPost.post.count())
                .from(QPost.post)
                .innerJoin(QPost.post.writer , QUser.user).on(QUser.user.id.eq(userId))
                .fetchOne();

        Integer totalView = queryFactory.select(QPost.post.views.sum())
                .from(QPost.post)
                .innerJoin(QPost.post.writer , QUser.user).on(QUser.user.id.eq(userId)).fetchOne();

        if(totalView == null) {
            totalView = 0;
        }

        long totalComment = queryFactory.select(QComment.comment.count())
                .from(QComment.comment)
                .innerJoin(QComment.comment.writer , QUser.user).on(QUser.user.id.eq(userId)).fetchOne();

        LocalDate joinDate = queryFactory.select(QUser.user.joinDate)
                .from(QUser.user)
                .where(QUser.user.id.eq(userId)).fetchOne();

        return StatisticsResponse.builder()
                .totalPost(totalPost)
                .totalPostView(totalView)
                .totalComment(totalComment)
                .joinData(joinDate.toString())
                .build();
    }
}
