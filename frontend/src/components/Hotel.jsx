import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { BedDouble, Star, Tag, AlertTriangle, Building } from 'lucide-react';

const Hotel = () => {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [bookingHotelId, setBookingHotelId] = useState(null);

  useEffect(() => {
    const fetchHotels = async () => {
      try {
        setLoading(true);
        const response = await axios.get('http://localhost:8080/api/v1/hotels');
        setHotels(response.data);
      } catch (err) {
        setError('Unable to fetch hotels. Please try again later.');
      } finally {
        setLoading(false);
      }
    };
    fetchHotels();
  }, []);

  const handleBookHotel = (hotelId) => {
    setBookingHotelId(hotelId);
    setTimeout(() => {
      // In a real app, you would show a success toast/modal here.
      alert(`Hotel booking confirmed for hotel ID: ${hotelId}!`);
      setBookingHotelId(null);
    }, 1500);
  };

  if (loading) {
    return (
      <div className="flex flex-col justify-center items-center h-screen bg-secondary-50">
        <div className="p-6 bg-white rounded-2xl shadow-soft">
          <Building className="animate-pulse w-16 h-16 text-primary-500 mx-auto mb-4" />
          <p className="text-lg text-secondary-700 font-medium">Loading Premium Hotels...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto mt-20 pt-10 px-4">
        <div className="card-elevated max-w-2xl mx-auto text-center">
          <div className="flex flex-col items-center">
            <div className="p-4 bg-accent-100 rounded-full mb-6">
              <AlertTriangle className="h-12 w-12 text-accent-600" />
            </div>
            <h4 className="text-2xl font-bold text-secondary-900 mb-4">Failed to Load Hotels</h4>
            <p className="text-secondary-600 mb-8 text-lg">{error}</p>
            <Link to="/" className="btn-primary">
              Back to Home
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-secondary-50 pt-20">
      <div className="container mx-auto px-4 py-10">
        <div className="text-center mb-12 animate-fade-in">
          <h1 className="text-5xl md:text-6xl font-extrabold text-secondary-900 mb-4">
            Premium <span className="text-gradient">Hotels</span>
          </h1>
          <p className="text-xl text-secondary-600 max-w-2xl mx-auto">
            Discover luxury accommodations and comfortable stays for your perfect trip
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {hotels.map((hotel, idx) => (
            <div 
              key={hotel.id} 
              className="card hover-lift animate-slide-up group flex flex-col h-full"
              style={{animationDelay: `${idx * 0.1}s`}}
            >
              <div className="relative overflow-hidden rounded-xl mb-6">
                <img 
                  src={hotel.imageURL} 
                  alt={hotel.name} 
                  className="w-full h-64 object-cover group-hover:scale-110 transition-transform duration-500" 
                />
                <div className="absolute top-4 right-4">
                  <div className="flex items-center bg-white/90 backdrop-blur-sm text-yellow-600 font-bold px-3 py-1 rounded-full">
                    <Star className="w-4 h-4 mr-1 fill-current" /> {hotel.rating}
                  </div>
                </div>
              </div>
              
              <div className="flex flex-col flex-grow space-y-4">
                <div>
                  <h3 className="text-2xl font-bold text-secondary-900 mb-2 group-hover:text-primary-600 transition-colors">
                    {hotel.name}
                  </h3>
                  <p className="text-secondary-600 flex items-center">
                    <span className="inline-block w-2 h-2 bg-primary-500 rounded-full mr-2"></span>
                    {hotel.city}, {hotel.location}
                  </p>
                </div>
                
                <div className="mt-auto space-y-4">
                  <div className="flex justify-between items-center">
                    <div className="text-3xl font-bold text-secondary-900">
                      ₹{hotel.price.toLocaleString()}
                    </div>
                    <div className="text-sm text-secondary-500">per night</div>
                  </div>
                  
                  <button
                    className={`btn-primary w-full py-3 flex justify-center items-center group/btn ${
                      bookingHotelId === hotel.id ? 'disabled:bg-primary-300 disabled:transform-none' : ''
                    }`}
                    disabled={bookingHotelId === hotel.id}
                    onClick={() => handleBookHotel(hotel.id)}
                  >
                    {bookingHotelId === hotel.id ? (
                      <>
                        <div className="loading-spinner h-5 w-5 mr-2"></div>
                        Booking...
                      </>
                    ) : (
                      <>
                        <BedDouble className="w-5 h-5 mr-2" />
                        Book Now
                        <span className="group-hover/btn:translate-x-1 transition-transform ml-2">→</span>
                      </>
                    )}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </main>
  );
};

export default Hotel;