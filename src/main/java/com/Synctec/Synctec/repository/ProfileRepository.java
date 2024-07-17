package com.Synctec.Synctec.repository;

import com.Synctec.Synctec.domains.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile,String> {
}
