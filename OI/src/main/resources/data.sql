-- Insert OiGroups
INSERT INTO oi_group (id, io_group) VALUES
(1, 'KBZ Pay'),
(2, 'Wave Pay'),
(3, 'AYA Pay');

-- Insert Individual Users linked to each group
INSERT INTO individual_user (id, name, email, password, amount, group_id) VALUES
(1, 'Aung Aung', 'aungaung@kbz.com', 'kbz123', '1000', 1),
(2, 'Su Su', 'susu@kbz.com', 'kbzpass', '850', 1),
(3, 'Ko Ko', 'koko@wave.com', 'wave123', '920', 2),
(4, 'Hla Hla', 'hlahla@wave.com', 'wavepass', '780', 2),
(5, 'Zaw Zaw', 'zawzaw@aya.com', 'aya123', '640', 3),
(6, 'Mya Mya', 'myamya@aya.com', 'ayapass', '720', 3);
