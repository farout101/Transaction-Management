INSERT INTO user_entity (id, name, email, password, amount) VALUES
    (1, 'Alice Smith', 'alice@example.com', 'alicePass123', '1500.00'),
    (2, 'Bob Johnson', 'bob@example.com', 'bobSecure!89', '2450.50'),
    (3, 'Carol White', 'carol@example.com', 'carolPwd456', '3200.75'),
    (4, 'David Brown', 'david@example.com', 'davidKey!2025', '980.00'),
    (5, 'Eva Green', 'eva@example.com', 'evaSafe#321', '5000.00');


INSERT INTO transaction_entity (
    transaction_id, sender_id, sender_group, amount, receiver_id, receiver_group, status, created_at, updated_at
) VALUES
      (1, 1, 'GroupA', '200.00', 1,'GroupX', 'SUCCESS', '2025-05-01 10:30:00', '2025-05-01 10:45:00'),
      (2, 1, 'GroupA', '150.00', 2,'GroupY', 'PENDING', '2025-05-02 14:00:00', '2025-05-02 14:00:00'),
      (3, 2, 'GroupB', '500.00', 2,'GroupZ', 'FAIL', '2025-05-03 09:15:00', '2025-05-03 09:20:00'),
      (4, 3, 'GroupC', '1000.00', 2,'GroupX', 'SUCCESS', '2025-05-04 11:00:00', '2025-05-04 11:30:00'),
      (5, 4, 'GroupD', '250.00', 4,'GroupY', 'PENDING', '2025-05-05 16:45:00', '2025-05-05 16:45:00');
