package com.example.noticeboard.account.profile.repository;

import com.example.noticeboard.account.profile.dto.StatisticsResponse;

public interface CustomProfileRepository {

    StatisticsResponse getStatisticsOfUser(String userId);

}
