-- Insert sample users (including admin)
-- Password for all users is '12345678'
-- Admin user is now created programmatically by AdminUserInitializer
INSERT INTO users (full_name, email, phone, password, role) VALUES 
('John Doe', 'john@example.com', '1234567890', '$2a$10$eImiTXuWVxfM37uY4JANjO8QgCfPo8HKBsj9EcC7yR1O5VzGkVDAK', 'USER'),
('Jane Smith', 'jane@example.com', '0987654321', '$2a$10$eImiTXuWVxfM37uY4JANjO8QgCfPo8HKBsj9EcC7yR1O5VzGkVDAK', 'USER');

-- Insert sample destinations
INSERT INTO destinations (location, city, description, back_image) VALUES
('Shaniwar Wada', 'Pune', 'Historical fortified palace and seat of the Peshwas of the Maratha Empire', 'https://images.unsplash.com/photo-1574779681789-d56ba19cd8c5?q=80&w=2074&auto=format&fit=crop'),
('Amer Fort', 'Jaipur', 'Magnificent fort located in Amer, known for its artistic Hindu style elements', 'https://images.unsplash.com/photo-1599661046289-e31897846e41?q=80&w=2070&auto=format&fit=crop'),
('City Palace', 'Udaipur', 'Palace complex situated in the city of Udaipur overlooking Lake Pichola', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?q=80&w=2070&auto=format&fit=crop'),
('Solang Valley', 'Manali', 'Beautiful valley between Solang village and Beas Kund, perfect for adventure sports', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?q=80&w=2070&auto=format&fit=crop'),
('The Ridge', 'Shimla', 'Famous open space in the heart of Shimla with stunning mountain views', 'https://images.unsplash.com/photo-1605649487212-47bdab064df7?q=80&w=2070&auto=format&fit=crop'),
('Hadimba Temple', 'Kullu', 'Ancient cave temple dedicated to Hidimbi Devi, surrounded by cedar forests', 'https://images.unsplash.com/photo-1559181567-c3190ca9959b?q=80&w=2070&auto=format&fit=crop'),
('Kaas Plateau', 'Satara', 'UNESCO World Natural Heritage site known as Valley of Flowers of Maharashtra', 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=2070&auto=format&fit=crop'),
('Sula Vineyards', 'Nashik', 'Premier wine destination in India with beautiful vineyard landscapes', 'https://images.unsplash.com/photo-1596142332133-327e5d96e15e?q=80&w=2070&auto=format&fit=crop'),
('Deekshabhoomi', 'Nagpur', 'Sacred monument where Dr. B.R. Ambedkar converted to Buddhism', 'https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=2070&auto=format&fit=crop'),
('Gateway of India', 'Mumbai', 'Iconic arch monument built during the British Raj in Mumbai', 'https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=2070&auto=format&fit=crop');

-- Insert sample hotels
INSERT INTO hotels (name, city, location, price, rating, imageurl) VALUES
('The Oberoi Udaipur', 'Udaipur', 'Lake Pichola', 15000, 4.8, 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=2070&auto=format&fit=crop'),
('Taj Lake Palace', 'Udaipur', 'Lake Pichola', 25000, 4.9, 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?q=80&w=2070&auto=format&fit=crop'),
('Rambagh Palace', 'Jaipur', 'Bhawani Singh Road', 18000, 4.7, 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=2070&auto=format&fit=crop'),
('The Leela Palace Udaipur', 'Udaipur', 'Lake Pichola', 20000, 4.6, 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=2070&auto=format&fit=crop'),
('Wildflower Hall', 'Shimla', 'Chharabra', 22000, 4.8, 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=2070&auto=format&fit=crop'),
('The Himalayan', 'Manali', 'Hadimba Road', 8000, 4.5, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070&auto=format&fit=crop'),
('JW Marriott Mumbai Juhu', 'Mumbai', 'Juhu Beach', 12000, 4.6, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?q=80&w=2070&auto=format&fit=crop'),
('The Taj Mahal Palace', 'Mumbai', 'Apollo Bunder', 16000, 4.7, 'https://images.unsplash.com/photo-1578774204375-826dc5d996ed?q=80&w=2070&auto=format&fit=crop'),
('Hotel Pune Centre Point', 'Pune', 'Koregaon Park', 3500, 4.2, 'https://images.unsplash.com/photo-1495365200479-c4ed1d35e1aa?q=80&w=2070&auto=format&fit=crop'),
('The Westin Pune Koregaon Park', 'Pune', 'Koregaon Park', 7500, 4.5, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?q=80&w=2070&auto=format&fit=crop');
