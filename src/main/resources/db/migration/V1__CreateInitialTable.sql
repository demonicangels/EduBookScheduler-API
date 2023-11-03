CREATE TABLE edu_user (
                          id INT NOT NULL AUTO_INCREMENT,
                          name VARCHAR(50) NOT NULL,
                          email VARCHAR(255) NOT NULL, -- Specify the length for the email column
                          password VARCHAR(255) NOT NULL,
                          role INT NOT NULL,
                          PRIMARY KEY (id)
);

CREATE TABLE student_info (
                              id INT NOT NULL AUTO_INCREMENT,
                              user_id INT NOT NULL, -- Define the user_id column with its type
                              pcn INT NOT NULL,
                              PRIMARY KEY (id),
                              FOREIGN KEY (user_id) REFERENCES edu_user(id)
);

CREATE TABLE booking (
                         id INT NOT NULL AUTO_INCREMENT,
                         date DATE NOT NULL,
                         startTime INT NOT NULL,
                         endTime INT NOT NULL,
                         description VARCHAR(255) NOT NULL,
                         student_id INT NOT NULL,
                         tutor_id INT NOT NULL,
                         PRIMARY KEY (id),
                         FOREIGN KEY (student_id) REFERENCES edu_user(id),
                         FOREIGN KEY (tutor_id) REFERENCES edu_user(id)
);