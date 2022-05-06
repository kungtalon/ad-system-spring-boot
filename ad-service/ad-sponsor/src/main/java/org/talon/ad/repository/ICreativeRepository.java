package org.talon.ad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.talon.ad.datamodel.AdUnit;
import org.talon.ad.datamodel.Creative;

import java.util.List;

public interface ICreativeRepository extends JpaRepository<Creative, Long> {
    Creative findByNameAndUserId(String name, Long userId);
}
