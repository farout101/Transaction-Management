package com.test.oi.repository;

import com.test.oi.model.OiGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OiRepo extends JpaRepository<OiGroup, Long> {

}
