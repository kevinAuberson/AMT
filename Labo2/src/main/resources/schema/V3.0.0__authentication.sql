-- Create roles table
CREATE TABLE role (
                      role_id SERIAL PRIMARY KEY,
                      role VARCHAR(255) NOT NULL UNIQUE
);

-- Create staff_roles join table
CREATE TABLE staff_roles (
                             staff_id INT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (staff_id, role_id),
                             FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
                             FOREIGN KEY (role_id) REFERENCES role(role_id)
);