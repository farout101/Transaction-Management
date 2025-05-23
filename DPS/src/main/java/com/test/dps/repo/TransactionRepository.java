package com.test.dps.repo;

import com.test.dps.dto.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Transaction> rowMapper = (r, i) -> {
        Transaction rowObject = new Transaction();
        rowObject.setId(r.getInt("id"));
        rowObject.setSenderUserId(r.getString("sender_user_id"));
        rowObject.setSenderUserGroup(r.getString("sender_user_group"));
        rowObject.setReceiverUserGroup(r.getString("receiver_user_group"));
        rowObject.setReceiverUserId(r.getString("receiver_user_id"));
        rowObject.setAmount(r.getDouble("amount"));
        rowObject.setDateTime(r.getTimestamp("datetime").toLocalDateTime());
        rowObject.setStatus(r.getString("status"));
        return rowObject;
    };


    TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transaction (sender_user_id, sender_user_group, receiver_user_group, receiver_user_id, amount) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, transaction.getSenderUserId());
            ps.setString(2, transaction.getSenderUserGroup());
            ps.setString(3, transaction.getReceiverUserGroup());
            ps.setString(4, transaction.getReceiverUserId());
            ps.setDouble(5, transaction.getAmount());
            return ps;
        }, keyHolder);

        return keyHolder.getKeys(); // return generated ID
    }



    public List<Transaction> getAllTransaction() {
        String sql = "SELECT * FROM transaction";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public Transaction getSingleTransaction(int id) throws Error {
        String sql = "SELECT * FROM transaction where id = ?";

        List<Transaction> result = jdbcTemplate.query(sql, rowMapper, id);
        if(result.size() != 1) throw new Error("Selecting from database went something wrong");
        return result.get(0);
    }

    public List<Transaction> getAllWaitingTransaction() {
        String sql = "SELECT * FROM transaction WHERE status = 'WAITING'";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public int updateTransactionStatus(int id, String newStatus) {
        String sql = "UPDATE transaction SET status = ? WHERE id = ?";

        return jdbcTemplate.update(sql, newStatus, id);
    }



}
