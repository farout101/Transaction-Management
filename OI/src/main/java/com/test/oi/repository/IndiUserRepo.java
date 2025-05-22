package com.test.oi.repository;

import com.test.oi.model.IndividualUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndiUserRepo extends JpaRepository<IndividualUser, Long> {

}
