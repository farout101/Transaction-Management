package com.test.merchant.repository;

import com.test.merchant.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionEntity, Long> {// This method is inherited from JpaRepository
}
