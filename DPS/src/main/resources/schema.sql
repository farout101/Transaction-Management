CREATE TABLE IF NOT EXISTS transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    io_group VARCHAR(50) NOT NULL,
    target_io_group VARCHAR(50) NOT NULL,
    amount DECIMAL(10,5) NOT NULL
);

INSERT INTO transaction (user_id, io_group, target_io_group, amount) VALUES
 ('user001', 'wallet', 'savings', 150.25000),
 ('user002', 'bank', 'wallet', 75.00000),
 ('user003', 'wallet', 'loan', 200.12550),
 ('user004', 'credit_card', 'wallet', 320.00000),
 ('user001', 'savings', 'investment', 500.50000),
 ('user005', 'wallet', 'charity', 25.00000),
 ('user002', 'bank', 'savings', 1000.00000),
 ('user006', 'wallet', 'utilities', 89.99000),
 ('user003', 'loan', 'wallet', 50.75000),
 ('user007', 'wallet', 'travel', 300.00000);
