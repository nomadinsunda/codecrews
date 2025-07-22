package com.example.noticeboard.admin.visitant.repository;

import com.example.noticeboard.admin.visitant.domain.QVisitant;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.noticeboard.admin.visitant.dto.VisitantResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class VisitantRepositoryImpl implements CustomVisitantRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<VisitantResponse> findVisitantCountByVisitDate() {
        return queryFactory.select(Projections.constructor(VisitantResponse.class
                        , QVisitant.visitant.count(), QVisitant.visitant.visitDate ))
                .from(QVisitant.visitant)
                .groupBy(QVisitant.visitant.visitDate)
                .orderBy(QVisitant.visitant.visitDate.asc())
                .limit(20L)
                .fetch();
    }
}
