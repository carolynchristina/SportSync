--DROP DATABASE IF EXISTS sportSync;
--CREATE DATABASE sportSync;

DROP TABLE IF EXISTS raceParticipants CASCADE;
DROP TABLE IF EXISTS activity CASCADE;
DROP TABLE IF EXISTS race CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users(
	username VARCHAR(30) PRIMARY KEY,
	email VARCHAR(60),
	password VARCHAR(60),
	roles VARCHAR(10)
);

CREATE TABLE race(
	id SERIAL PRIMARY KEY,
	judul VARCHAR(100),
	deskripsi VARCHAR(200),
	tglMulai DATE, --YYYY-MM-DD
	tglSelesai DATE --YYYY-MM-DD
);

CREATE TABLE activity(
	id SERIAL PRIMARY KEY,
	judul VARCHAR(100),
	deskripsi VARCHAR(200),
	tglWaktuMulai TIMESTAMP, --YYYY-MM-DD hh:mm:ss
	jarakTempuh int, --meter
	durasi TIME, --hh:mm:ss
	foto BYTEA,
	username VARCHAR(30) REFERENCES users(username),
	idRace int REFERENCES race(id)
);

CREATE TABLE raceParticipants(
	username VARCHAR(30) REFERENCES users(username),
	idRace int REFERENCES race(id)
);

INSERT INTO users (username, email, password, roles) VALUES
('admin', 'admin@gmail.com', 'admin123', 'admin'),
('alice', 'alice@gmail.com', 'password1', 'pengguna'),
('bobby', 'bobby@gmail.com', 'password2', 'pengguna'),
('charles', 'charles@gmail.com', 'password3', 'pengguna'),
('diana', 'diana@gmail.com', 'password4', 'pengguna'),
('edward', 'edward@gmail.com', 'password5', 'pengguna'),
('fiona', 'fiona@gmail.com', 'password6', 'pengguna'),
('greg', 'greg@gmail.com', 'password7', 'pengguna'),
('hannah', 'hannah@gmail.com', 'password8', 'pengguna'),
('ivan', 'ivan@gmail.com', 'password9', 'pengguna'),
('julia', 'julia@gmail.com', 'password10', 'pengguna'),
('karen', 'karen@gmail.com', 'password11', 'pengguna'),
('linda', 'linda@gmail.com', 'password12', 'pengguna'),
('mia', 'mia@gmail.com', 'password13', 'pengguna'),
('noah', 'noah@gmail.com', 'password14', 'pengguna'),
('olivia', 'olivia@gmail.com', 'password15', 'pengguna'),
('peter', 'peter@gmail.com', 'password16', 'pengguna'),
('rachel', 'rachel@gmail.com', 'password18', 'pengguna'),
('qira', 'qira@gmail.com', 'password19', 'pengguna'),
('steve', 'steve@gmail.com', 'password19', 'pengguna');

INSERT INTO race (judul, deskripsi, tglMulai, tglSelesai) VALUES
('December Run 10K Race', 'Run a total of 10km', '2024-12-01', '2024-12-31'),
('Half Marathon', 'Run a total of 21km', '2024-12-15', '2024-12-22'),
('Last Day of the Year Sprint', 'A fast 5km run to end the year', '2024-12-31', '2024-12-31');

INSERT INTO activity (judul, deskripsi, tglWaktuMulai, jarakTempuh, durasi, foto, username, idRace) VALUES
('Morning Run', 'A refreshing 1km run to start the day', '2024-01-01 07:00:00', 1000, '01:00:00', NULL, 'alice', NULL),
('Morning Run', 'A refreshing 1km run to start the day', '2024-01-01 07:00:00', 1000, '01:00:00', NULL, 'bobby', NULL),
('December Run', 'Competing in the December Run 10K Race', '2024-12-05 07:00:00', 10000, '01:00:00', NULL, 'bobby', 1),
('Evening Run', 'A relaxing evening run of 2km', '2024-01-01 18:00:00', 2000, '00:30:00', NULL, 'charles', NULL),
('December Run', 'Part of the 10km race', '2024-12-15 08:00:00', 10000, '01:20:00', NULL, 'diana', 1),
('Morning Run', 'Run uphill for 3km', '2024-12-20 07:30:00', 3000, '01:30:00', NULL, 'edward', NULL),
('City Jogging', 'Exploring the city while jogging 5km', '2024-12-31 06:45:00', 5000, '00:45:00', NULL, 'fiona', 3),
('Nature Run', 'A 10km run in the park', '2024-01-02 07:00:00', 10000, '01:00:00', NULL, 'greg', NULL),
('Beach Run', 'Running along the beach for 4km', '2024-01-03 08:00:00', 4000, '01:00:00', NULL, 'hannah', NULL),
('Morning Sprint', 'A quick 5km race', '2024-12-31 07:10:00', 5000, '00:15:00', NULL, 'julia', 3),
('Mountain Cycling', 'Cycling a mountain for 7km', '2024-03-16 06:00:00', 7000, '02:10:00', NULL, 'karen', NULL),
('Urban Run', 'Running around downtown for 2.5km', '2024-03-17 17:00:00', 2500, '00:35:00', NULL, 'linda', NULL),
('River Run', 'A 3km run along the river', '2024-03-18 06:30:00', 3000, '00:25:00', NULL, 'mia', NULL),
('Evening Cycling', 'Casual evening bike ride of 8km', '2024-10-19 18:00:00', 8000, '00:50:00', NULL, 'noah', NULL),
('Forest Run', 'Running through a forest trail for 4km', '2024-10-20 08:00:00', 4000, '01:00:00', NULL, 'olivia', NULL),
('Hill Run', 'Challenging uphill run for 5km', '2024-12-31 07:30:00', 5000, '00:50:00', NULL, 'peter', 3),
('Night Run', 'A quiet 2km night run', '2024-03-22 20:00:00', 2000, '00:40:00', NULL, 'qira', NULL),
('Park Run', 'Jogging in the park for 6km', '2024-03-23 06:00:00', 6000, '00:55:00', NULL, 'rachel', NULL),
('Weekend Run', 'Running in the hills for 7km', '2024-03-24 07:00:00', 7000, '01:40:00', NULL, 'steve', NULL),
('Marathon Run', 'Participating in the Half Marathon', '2024-12-20 06:30:00', 21000, '01:10:00', NULL, 'alice', 2);

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

