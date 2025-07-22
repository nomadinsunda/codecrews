package com.example.noticeboard.account.profile.repository;

import com.example.noticeboard.account.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> , CustomProfileRepository {

}
