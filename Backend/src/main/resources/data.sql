-- Insert sample users
INSERT INTO users (id, full_name, email, phone, password) VALUES 
(1, 'John Doe', 'john@example.com', '1234567890', '$2a$10$eImiTXuWVxfM37uY4JANjOhzlyQwHbBDJdGJw.4xz8K7.7L8XQ1.e'),
(2, 'Jane Smith', 'jane@example.com', '0987654321', '$2a$10$eImiTXuWVxfM37uY4JANjOhzlyQwHbBDJdGJw.4xz8K7.7L8XQ1.e');

-- Insert sample destinations
INSERT INTO destinations (id, location, city, description, back_image) VALUES
(1, 'Shaniwar Wada', 'Pune', 'Historical fortified palace and seat of the Peshwas of the Maratha Empire', 'https://images.unsplash.com/photo-1574779681789-d56ba19cd8c5?q=80&w=2074&auto=format&fit=crop'),
(2, 'Amer Fort', 'Jaipur', 'Magnificent fort located in Amer, known for its artistic Hindu style elements', 'https://images.unsplash.com/photo-1599661046289-e31897846e41?q=80&w=2070&auto=format&fit=crop'),
(3, 'City Palace', 'Udaipur', 'Palace complex situated in the city of Udaipur overlooking Lake Pichola', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?q=80&w=2070&auto=format&fit=crop'),
(4, 'Solang Valley', 'Manali', 'Beautiful valley between Solang village and Beas Kund, perfect for adventure sports', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?q=80&w=2070&auto=format&fit=crop'),
(5, 'The Ridge', 'Shimla', 'Famous open space in the heart of Shimla with stunning mountain views', 'https://images.unsplash.com/photo-1605649487212-47bdab064df7?q=80&w=2070&auto=format&fit=crop'),
(6, 'Hadimba Temple', 'Kullu', 'Ancient cave temple dedicated to Hidimbi Devi, surrounded by cedar forests', 'https://images.unsplash.com/photo-1559181567-c3190ca9959b?q=80&w=2070&auto=format&fit=crop'),
(7, 'Kaas Plateau', 'Satara', 'UNESCO World Natural Heritage site known as Valley of Flowers of Maharashtra', 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=2070&auto=format&fit=crop'),
(8, 'Sula Vineyards', 'Nashik', 'Premier wine destination in India with beautiful vineyard landscapes', 'https://images.unsplash.com/photo-1596142332133-327e5d96e15e?q=80&w=2070&auto=format&fit=crop'),
(9, 'Deekshabhoomi', 'Nagpur', 'Sacred monument where Dr. B.R. Ambedkar converted to Buddhism', 'https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=2070&auto=format&fit=crop'),
(10, 'Gateway of India', 'Mumbai', 'Iconic arch monument built during the British Raj in Mumbai', 'https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=2070&auto=format&fit=crop');

-- Insert sample hotels
INSERT INTO hotels (id, name, city, location, price, rating, imageurl) VALUES
(1, 'The Oberoi Udaipur', 'Udaipur', 'Lake Pichola', 15000, 4.8, 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=2070&auto=format&fit=crop'),
(2, 'Taj Lake Palace', 'Udaipur', 'Lake Pichola', 25000, 4.9, 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?q=80&w=2070&auto=format&fit=crop'),
(3, 'Rambagh Palace', 'Jaipur', 'Bhawani Singh Road', 18000, 4.7, 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=2070&auto=format&fit=crop'),
(4, 'The Leela Palace Udaipur', 'Udaipur', 'Lake Pichola', 20000, 4.6, 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=2070&auto=format&fit=crop'),
(5, 'Wildflower Hall', 'Shimla', 'Chharabra', 22000, 4.8, 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=2070&auto=format&fit=crop'),
(6, 'The Himalayan', 'Manali', 'Hadimba Road', 8000, 4.5, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070&auto=format&fit=crop'),
(7, 'JW Marriott Mumbai Juhu', 'Mumbai', 'Juhu Beach', 12000, 4.6, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?q=80&w=2070&auto=format&fit=crop'),
(8, 'The Taj Mahal Palace', 'Mumbai', 'Apollo Bunder', 16000, 4.7, 'https://images.unsplash.com/photo-1578774204375-826dc5d996ed?q=80&w=2070&auto=format&fit=crop'),
(9, 'Hotel Pune Centre Point', 'Pune', 'Koregaon Park', 3500, 4.2, 'https://images.unsplash.com/photo-1495365200479-c4ed1d35e1aa?q=80&w=2070&auto=format&fit=crop'),
(10, 'The Westin Pune Koregaon Park', 'Pune', 'Koregaon Park', 7500, 4.5, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?q=80&w=2070&auto=format&fit=crop');