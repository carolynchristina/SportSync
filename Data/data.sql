--DROP DATABASE IF EXISTS sportSync;
--CREATE DATABASE sportSync;

DROP TABLE IF EXISTS raceParticipants CASCADE;
DROP TABLE IF EXISTS activity CASCADE;
DROP TABLE IF EXISTS race CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendlist CASCADE;

CREATE TABLE users(
	username VARCHAR(30) PRIMARY KEY,
	email VARCHAR(60),
	password VARCHAR(60),
	roles VARCHAR(10),
	status boolean
);

CREATE TABLE friendlist (
    user1 VARCHAR(30) REFERENCES users(username) ON DELETE CASCADE,
    user2 VARCHAR(30) REFERENCES users(username) ON DELETE CASCADE,
    PRIMARY KEY (user1, user2),
    CONSTRAINT chk_diff_users CHECK (user1 <> user2)
);

CREATE TABLE race(
	id SERIAL PRIMARY KEY,
	judul VARCHAR(100),
	deskripsi VARCHAR(200),
	tglMulai DATE, --YYYY-MM-DD
	tglSelesai DATE, --YYYY-MM-DD
	jarakTempuh int
);

CREATE TABLE activity(
	id SERIAL PRIMARY KEY,
	judul VARCHAR(100),
	deskripsi VARCHAR(200),
	tglWaktuMulai TIMESTAMP, --YYYY-MM-DD hh:mm:ss
	jarakTempuh int, --meter
	durasi TIME, --hh:mm:ss
	foto BYTEA,
	username VARCHAR(30) REFERENCES users(username) ON DELETE CASCADE, --hapus otomatis jika username dihapus
	idRace int REFERENCES race(id)
);

CREATE TABLE raceParticipants(
	username VARCHAR(30) REFERENCES users(username) ON DELETE CASCADE, --hapus otomatis jika username dihapus
	idRace int REFERENCES race(id)
);

--password: {nama}pass (misal: nama: alice, password: alicepass)
INSERT INTO users (username, email, password, roles, status) VALUES
('admin', 'admin@gmail.com', '$2a$10$w75XAuVuRo.SePNZf7L/O.Tzod4gOi6.sXNXDIdN3BLQ70XOvWmhS', 'admin', true),
('alice', 'alice@gmail.com', '$2a$10$CsRVcGlbj/RmSZ85dxtyOOn5uqcTw1Ks4p8QPM3AyRnFtdzRZqsEC', 'pengguna', true),
('bobby', 'bobby@gmail.com', '$2a$10$2XEzZ44.QBNphivTZsxwX.ssMrO0FEJTlQ3.UN6XUMsLug/oiebP2', 'pengguna', true),
('charles', 'charles@gmail.com', '$2a$10$TOvFyQy2snIob1vxZyP58epKZPbLDAaK5Bmy0NQPs/U6PRl1e6Y5C', 'pengguna', false),
('diana', 'diana@gmail.com', '$2a$10$nmcBQNru8jorM.n47hCafO9RrwQEmGlnvQn706IMS49ixmLUzB93y', 'pengguna', true),
('edward', 'edward@gmail.com', '$2a$10$XWk.EU4GRpj2ZKVc2mc8nepy1hSL7D1kH8MaWIuwir6qtKCJGd4PW', 'pengguna', true),
('fiona', 'fiona@gmail.com', '$2a$10$lJxMWEmwN.GiZWgPfdFAV.v2bc2K6aclIIcLtUzMduwAt0oqI5bUO', 'pengguna', true),
('greg', 'greg@gmail.com', '$2a$10$2OQig.RpOX4a/9agblasweCw7nf5.gXQuRTmNAXXXYCjQbvV4Bn6K', 'pengguna', false),
('hannah', 'hannah@gmail.com', '$2a$10$cQzyUn6VcaBnHjr8hg5HnOb3QluFD.O5wEmKDakAi2bznDFwyXwH6', 'pengguna', false),
('ivan', 'ivan@gmail.com', '$2a$10$2OaeW.JAdBbKgTdNwsuL1O592bU13nU.fnXwpL7ntOZUXUfoK6Fom', 'pengguna', false),
('julia', 'julia@gmail.com', '$2a$10$nnF5AiLtbelS2KJDQOaHxuhjAn.rjzgUMXrh1rqzMZ2VYmGfNgA8C', 'pengguna', true),
('karen', 'karen@gmail.com', '$2a$10$E.q9mZ8J091WlQegmWSXfOnu0ubomXBTU6AwpRt4pEJY3NjLVQUgS', 'pengguna', false),
('linda', 'linda@gmail.com', '$2a$10$b704sWJSP/EeHF650mpcBePqZNNh8VSAk7jaZybIuXj5RIvS6IW4q', 'pengguna', false),
('mia', 'mia@gmail.com', '$2a$10$lsPNZbRKw2q9RVnxDSwqquDdVyTvxjtUjalyIu97fMdOXqndh6mia', 'pengguna', false),
('noah', 'noah@gmail.com', '$2a$10$cNuqQKY57qDuxw/CGj.BwecEZc/AE7Sz3mqxbeMVNAJDhi2ytQvN6', 'pengguna', true),
('olivia', 'olivia@gmail.com', '$2a$10$SwDApthMpRuKBr99xirF5OMh/26CuQjzEiw3NlJ14xwjkoudA0n9e', 'pengguna', true),
('peter', 'peter@gmail.com', '$2a$10$zJFUFQBTAt.C5Hmn0EfDl.AJnSJi30j5b55Fv9q9pOQGJ1Zm1IqKa', 'pengguna', true),
('rachel', 'rachel@gmail.com', '$2a$10$emgmGHnkNbXeBubDkPaeQ.abuWqLnGFOuGvp2eqxW.TspQdlopvbW', 'pengguna', false),
('qira', 'qira@gmail.com', '$2a$10$a9vHaKt/eMw39aZnTgVoyuNLHynqajsomWnrRoXVAXi7ml3.ImxHa', 'pengguna', false),
('steve', 'steve@gmail.com', '$2a$10$K2IOW6IqSlOWiEsCLh/RNOtP8Togcg4lYvsCZmHEPti/ALzaTGEOe', 'pengguna', false);


INSERT INTO race (judul, deskripsi, tglMulai, tglSelesai, jarakTempuh) VALUES
('January Run 10K Race', 'Run a total of 10km', '2025-01-01', '2025-01-31', 10000),
('Half Marathon', 'Run a total of 21km', '2025-01-07', '2025-01-14', 21000),
('First Day of the Year Sprint', 'A fast 5km run to end the year', '2025-01-01', '2025-01-01', 5000),
('Spring Fun Run', 'A fun run event for the spring season', '2025-01-05', '2025-01-20', 1000),
('Marathon Challenge', 'Challenge yourself with a 42km marathon!', '2025-01-10', '2025-01-15', 42000);

INSERT INTO activity (judul, deskripsi, tglWaktuMulai, jarakTempuh, durasi, foto, username, idRace) VALUES
('Morning Run', 'A refreshing 1km run to start the day', '2025-01-01 07:00:00', 1000, '01:00:00', NULL, 'alice', NULL),
('Morning Run', 'A refreshing 1km run to start the day', '2025-01-01 07:00:00', 1000, '01:00:00', NULL, 'bobby', NULL),
('January Run', 'Competing in the January Run 10K Race', '2025-01-05 07:00:00', 10000, '01:00:00', NULL, 'bobby', 1),
('Evening Run', 'A relaxing evening run of 2km', '2025-01-01 18:00:00', 2000, '00:30:00', NULL, 'charles', NULL),
('January Run', 'Part of the 10km race', '2025-01-15 08:00:00', 10000, '01:20:00', NULL, 'diana', 1),
('Morning Run', 'Run uphill for 3km', '2025-01-10 07:30:00', 3000, '01:30:00', NULL, 'edward', NULL),
('City Jogging', 'Exploring the city while jogging 5km', '2025-01-01 06:45:00', 5000, '00:45:00', NULL, 'fiona', 3),
('Nature Run', 'A 10km run in the park', '2025-01-02 07:00:00', 10000, '01:00:00', NULL, 'greg', NULL),
('Beach Run', 'Running along the beach for 4km', '2025-01-03 08:00:00', 4000, '01:00:00', NULL, 'hannah', NULL),
('Morning Sprint', 'A quick 5km race', '2025-01-01 07:10:00', 5000, '00:15:00', NULL, 'julia', 3),
('Mountain Cycling', 'Cycling a mountain for 7km', '2024-03-16 06:00:00', 7000, '02:10:00', NULL, 'karen', NULL),
('Urban Run', 'Running around downtown for 2.5km', '2024-03-17 17:00:00', 2500, '00:35:00', NULL, 'linda', NULL),
('River Run', 'A 3km run along the river', '2024-03-18 06:30:00', 3000, '00:25:00', NULL, 'mia', NULL),
('Evening Cycling', 'Casual evening bike ride of 8km', '2024-10-19 18:00:00', 8000, '00:50:00', NULL, 'noah', NULL),
('Forest Run', 'Running through a forest trail for 4km', '2024-10-20 08:00:00', 4000, '01:00:00', NULL, 'olivia', NULL),
('Hill Run', 'Challenging uphill run for 5km', '2025-01-01 07:30:00', 5000, '00:50:00', NULL, 'peter', 3),
('Night Run', 'A quiet 2km night run', '2024-03-22 20:00:00', 2000, '00:40:00', NULL, 'qira', NULL),
('Park Run', 'Jogging in the park for 6km', '2024-03-23 06:00:00', 6000, '00:55:00', NULL, 'rachel', NULL),
('Weekend Run', 'Running in the hills for 7km', '2024-03-24 07:00:00', 7000, '01:40:00', NULL, 'steve', NULL),
('Marathon Run', 'Participating in the Half Marathon', '2025-01-10 06:30:00', 21000, '01:10:00', NULL, 'alice', 2);

INSERT INTO raceParticipants (username, idRace) VALUES
('bobby', 1),
('diana', 1),
('fiona', 3),
('julia', 3),
('peter', 3),
('alice', 2),
('steve', 2),
('peter', 2),
('mia', 1),
('rachel', 2);

INSERT INTO friendlist (user1, user2) VALUES
('alice', 'bobby'),
('alice', 'charles'),
('bobby', 'diana'),
('charles', 'edward'),
('diana', 'fiona'),
('fiona', 'greg'),
('greg', 'hannah'),
('alice', 'edward'),
('mia', 'noah'),
('julia', 'olivia'),
('peter', 'rachel');
---------------------------------------------------------------
--VIEW
CREATE OR REPLACE VIEW lastActDate AS
SELECT username, MAX(tglwaktumulai) AS last_activity
FROM ACTIVITY
GROUP BY username;


CREATE OR REPLACE VIEW userFriends AS
SELECT 
    user1 AS username,
    user2 AS friend
FROM friendlist
UNION
SELECT 
    user2 AS username,
    user1 AS friend
FROM friendlist
ORDER BY username, friend;

--validasi isi leaderboard
SELECT username, jarakTempuh, durasi 
FROM activity 
WHERE idRace = 1
ORDER BY jarakTempuh DESC, durasi ASC;

