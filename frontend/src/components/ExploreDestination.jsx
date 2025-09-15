import React, { useEffect, useState } from 'react';
import { useLocation, Link } from 'react-router-dom';
import axios from 'axios';
import { Map, Thermometer, Wind, AlertTriangle, Frown } from 'lucide-react';

const ExploreDestination = () => {
  const [destinations, setDestinations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [weatherData, setWeatherData] = useState(null);

  const query = new URLSearchParams(useLocation().search);
  const selectedCity = query.get('city');

  useEffect(() => {
    if (!selectedCity) {
      setError('Please select a city to explore destinations.');
      setLoading(false);
      return;
    }

    const fetchDestinations = async () => {
      setLoading(true);
      setError(null);
      try {
        // Fetch destinations for the city
        const destResponse = await axios.get(`http://localhost:8080/api/v1/destinations`);
        const filtered = destResponse.data.filter(
          (dest) => dest.city.toLowerCase() === selectedCity.toLowerCase()
        );
        setDestinations(filtered);

        // Fetch weather for the city
        if (filtered.length > 0) {
          const weatherResponse = await axios.get(`http://localhost:8080/api/v1/destinations/weather/${selectedCity}`);
          setWeatherData(weatherResponse.data);
        }
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Failed to load destination data.';
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchDestinations();
  }, [selectedCity]);

  if (loading) {
    return (
      <div className="flex flex-col justify-center items-center h-screen bg-secondary-50">
        <div className="p-6 bg-white rounded-2xl shadow-soft">
          <Map className="animate-pulse w-16 h-16 text-primary-500 mx-auto mb-4" />
          <p className="text-lg text-secondary-700 font-medium">Loading Destinations for {selectedCity}...</p>
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
            <h4 className="text-2xl font-bold text-secondary-900 mb-4">Oops! Something went wrong</h4>
            <p className="text-secondary-600 mb-8 text-lg">{error}</p>
            <Link to="/plan" className="btn-primary">
              Plan a Different Trip
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
            Discover <span className="text-gradient">{selectedCity}</span>
          </h1>
          <p className="text-xl text-secondary-600 max-w-2xl mx-auto">
            Uncover hidden gems and popular attractions in this amazing destination
          </p>
          {weatherData && (
            <div className="flex justify-center items-center space-x-6 mt-6 p-4 bg-white rounded-2xl shadow-soft max-w-md mx-auto">
              <div className="flex items-center text-secondary-700">
                <div className="p-2 bg-accent-100 rounded-lg mr-3">
                  <Thermometer className="w-5 h-5 text-accent-600" />
                </div>
                <span className="font-semibold">{weatherData.temperature}°C</span>
              </div>
              <div className="flex items-center text-secondary-700">
                <div className="p-2 bg-primary-100 rounded-lg mr-3">
                  <Wind className="w-5 h-5 text-primary-600" />
                </div>
                <span className="font-semibold">{weatherData.windspeed} km/h</span>
              </div>
            </div>
          )}
        </div>
      
        {destinations.length === 0 ? (
          <div className="card-elevated text-center max-w-2xl mx-auto">
            <div className="p-8">
              <div className="p-6 bg-secondary-100 rounded-full w-24 h-24 mx-auto mb-6 flex items-center justify-center">
                <Frown className="w-12 h-12 text-secondary-500" />
              </div>
              <h3 className="text-3xl font-bold text-secondary-900 mb-4">
                No destinations found for "{selectedCity}"
              </h3>
              <p className="text-secondary-600 text-lg mb-8">
                Don't worry! Try exploring a different destination or check back later.
              </p>
              <Link to="/plan" className="btn-primary">
                Plan a Different Trip
              </Link>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {destinations.map((dest, idx) => (
              <div 
                key={dest.id} 
                className="card hover-lift animate-slide-up group"
                style={{animationDelay: `${idx * 0.1}s`}}
              >
                <div className="relative overflow-hidden rounded-xl mb-6">
                  <img
                    src={dest.backImage}
                    alt={dest.location}
                    className="w-full h-64 object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                </div>
                <div className="space-y-4">
                  <h3 className="text-2xl font-bold text-secondary-900 group-hover:text-primary-600 transition-colors">
                    {dest.location}
                  </h3>
                  <p className="text-secondary-600 leading-relaxed line-clamp-3">
                    {dest.description}
                  </p>
                  <Link 
                    to={`/destination/${dest.id}`} 
                    className="btn-primary inline-flex items-center gap-2 group/btn"
                  >
                    Explore More
                    <span className="group-hover/btn:translate-x-1 transition-transform">→</span>
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  );
};

export default ExploreDestination;