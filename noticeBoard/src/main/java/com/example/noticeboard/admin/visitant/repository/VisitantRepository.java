package com.example.noticeboard.admin.visitant.repository;

import com.example.noticeboard.admin.visitant.domain.Visitant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitantRepository extends JpaRepository<Visitant, Long>, CustomVisitantRepository {

    boolean existsByUserIpAndVisitDate(String userIp, LocalDate visitDate);

}
