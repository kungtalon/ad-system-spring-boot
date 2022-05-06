package org.talon.ad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talon.ad.datamodel.AdUser;

public interface IAdUserRepository extends JpaRepository<AdUser, Long> {

    /**
     * Find an AdUser by name
     * @param username
     * @return an AdUser object, or null if non-exist
     */
    AdUser findByUsername(String username);
}
