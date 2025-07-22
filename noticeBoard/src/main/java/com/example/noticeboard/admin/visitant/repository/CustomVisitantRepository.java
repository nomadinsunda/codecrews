package com.example.noticeboard.admin.visitant.repository;

import com.example.noticeboard.admin.visitant.dto.VisitantResponse;

import java.util.List;

public interface CustomVisitantRepository {

    List<VisitantResponse> findVisitantCountByVisitDate();

}
