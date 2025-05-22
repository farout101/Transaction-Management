package com.test.dps.repo;

import com.test.dps.dto.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transaction (user_id, io_group, target_io_group, amount) " +
                "VALUES (?, ?, ?, ?)";


        return jdbcTemplate.update(sql,
            transaction.getId(),
            transaction.getIoGroup(),
            transaction.getTargetIoGroup(),
            transaction.getAmount()
        );
    }

    public List<Transaction> getAllTransaction() {
        String sql = "SELECT * FROM transaction";

        RowMapper<Transaction> rowMapper = (r, i) -> {
            Transaction rowObject = new Transaction();
            rowObject.setId(r.getInt("id"));
            rowObject.setAmount(r.getDouble("amount"));
            rowObject.setIoGroup(r.getString("io_group"));
            rowObject.setTargetIoGroup(r.getString("target_io_group"));
            rowObject.setUserId(r.getString("user_id"));
            return rowObject;
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
}
