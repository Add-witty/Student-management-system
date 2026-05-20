CREATE DATABASE university;

USE university;

CREATE TABLE teacher (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE student (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE marks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    subject VARCHAR(50),
    cca INT DEFAULT 0,
    lca INT DEFAULT 0,
    final INT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    subject VARCHAR(50),
    present_days INT DEFAULT 0,
    total_days INT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

CREATE TABLE assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    subject VARCHAR(50),
    assignment_number INT,
    assignment_name VARCHAR(100),
    status ENUM('Submitted', 'Not Submitted') DEFAULT 'Not Submitted',
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);





ALTER TABLE assignments ADD COLUMN assignment_no VARCHAR(20);
ALTER TABLE assignments ADD COLUMN assignment_name VARCHAR(100);


CREATE TABLE notices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    notice_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    event_type ENUM('Assignment', 'Test', 'Extra Lecture', 'Group Discussion', 'Meeting') NOT NULL,
    event_date DATE NOT NULL,
    description TEXT
);

CREATE TABLE leaves (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    reason TEXT NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

INSERT INTO teacher (name, email, password) 
VALUES ('Ranjana Agrawal', 'ranjana_agrawal@mitwpu.edu.in', 'password123');

INSERT INTO student (name, email, password) 
VALUES 
('Sai Khairnar', '1032222193@mitwpu.edu.in', 'pass123'),
('Adwiti Jha', '1032222223@mitwpu.edu.in', 'pass123'),
('Sanjyot Aher', '1032222205@mitwpu.edu.in', 'pass123');

SELECT student_id, subject, COUNT(*) 
FROM marks 
GROUP BY student_id, subject 
HAVING COUNT(*) > 1;

DELETE m1 FROM marks m1
JOIN marks m2 
ON m1.student_id = m2.student_id 
AND m1.subject = m2.subject 
AND m1.id < m2.id;

ALTER TABLE marks ADD UNIQUE (student_id, subject);

SHOW INDEX FROM marks;



