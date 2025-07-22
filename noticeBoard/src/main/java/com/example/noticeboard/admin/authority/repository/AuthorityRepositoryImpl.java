package com.example.noticeboard.admin.authority.repository;

import com.example.noticeboard.account.user.domain.QUser;
import com.example.noticeboard.admin.authority.dto.AuthorityResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class AuthorityRepositoryImpl implements CustomAuthorityRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AuthorityResponse> findAllAuthorityUser(Pageable pageable) {
        return jpaQueryFactory.select(Projections.constructor(AuthorityResponse.class,
                        QUser.user.userKey,
                        QUser.user.id,
                        QUser.user.joinDate,
                        QUser.user.lastLoginDate,
                        QUser.user.suspensionReason,
                        QUser.user.suspensionDate,
                        QUser.user.isSuspension,
                        QUser.user.role
                ))
                .from(QUser.user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<AuthorityResponse> findAllAuthorityUserById(Pageable pageable, String search) {
        return jpaQueryFactory.select(Projections.constructor(AuthorityResponse.class,
                        QUser.user.userKey,
                        QUser.user.id,
                        QUser.user.joinDate,
                        QUser.user.lastLoginDate,
                        QUser.user.suspensionReason,
                        QUser.user.suspensionDate,
                        QUser.user.isSuspension,
                        QUser.user.role
                ))
                .from(QUser.user)
                .where(QUser.user.id.contains(search))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
