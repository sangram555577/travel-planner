-- Delete existing admin user if exists
DELETE FROM users WHERE email = 'admin369@gmail.com';

-- Insert admin user with correct BCrypt hash for password "12345678"
INSERT INTO users (full_name, email, phone, password, role) VALUES 
('Admin User', 'admin369@gmail.com', '9999999999', '$2a$10$eImiTXuWVxfM37uY4JANjO8QgCfPo8HKBsj9EcC7yR1O5VzGkVDAK', 'ADMIN');

-- Verify the admin user was created
SELECT id, full_name, email, role FROM users WHERE email = 'admin369@gmail.com';