CREATE TABLE IF NOT EXISTS transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_user_id VARCHAR(50) NOT NULL,
    sender_user_group VARCHAR(50) NOT NULL,
    receiver_user_group VARCHAR(50) NOT NULL,
    receiver_user_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10,5) NOT NULL,
    datetime DATETIME NOT NULL DEFAULT NOW(),
    status VARCHAR(10) DEFAULT 'WAITING'
);

INSERT INTO transaction (
    sender_user_id,
    sender_user_group,
    receiver_user_group,
    receiver_user_id,
    amount
) VALUES
      ('user_003', 'wallet', 'wallet', 'user_004', 120.50),
      ('user_005', 'bank', 'wallet', 'user_006', 75.25),
      ('user_007', 'loan', 'bank', 'user_008', 500.00),
      ('user_009', 'wallet', 'loan', 'user_010', 330.15);
