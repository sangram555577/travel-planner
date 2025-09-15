import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useSearchParams } from 'react-router-dom';
import { Plane, ArrowRight, Calendar, AlertTriangle, Frown } from 'lucide-react';

const FlightResults = () => {
  const [searchParams] = useSearchParams();
  const from = searchParams.get('from') || 'DEL';
  const to = searchParams.get('to') || 'JAI';
  const date = searchParams.get('date') || '2025-09-20';
  
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchFlights = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get(`http://localhost:8080/api/v1/flights?from=${from}&to=${to}&date=${date}`);
        setFlights(response.data || []);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Unable to fetch flight data. Please check your API keys and try again.';
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };
    fetchFlights();
  }, [from, to, date]);

  if (loading) {
    return (
      <div className="flex flex-col justify-center items-center h-screen bg-gray-100">
        <Plane className="animate-bounce w-16 h-16 text-blue-500" />
        <p className="mt-4 text-lg text-gray-600">Searching for Flights...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto mt-20 pt-10 text-center">
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-6 rounded-lg shadow-md max-w-lg mx-auto">
           <div className="flex items-center">
            <AlertTriangle className="h-8 w-8 mr-3" />
            <div>
              <h4 className="font-bold text-xl">Could Not Fetch Flights</h4>
              <p>{error}</p>
              <Link to="/plan" className="mt-4 inline-block bg-red-500 text-white font-bold py-2 px-4 rounded hover:bg-red-600">
                Plan a New Trip
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <main className="container mx-auto my-10 pt-20 px-4">
      <div className="text-center mb-8">
        <div className="flex justify-center items-center text-3xl md:text-4xl font-extrabold text-gray-800">
          <span>{from}</span>
          <ArrowRight className="w-8 h-8 mx-4 text-blue-500" />
          <span>{to}</span>
        </div>
        <p className="flex justify-center items-center mt-2 text-lg text-gray-600">
          <Calendar className="w-5 h-5 mr-2" />
          {date}
        </p>
      </div>

      {flights.length === 0 ? (
        <div className="text-center my-16">
          <Frown className="w-16 h-16 mx-auto text-gray-400" />
          <h3 className="mt-4 text-2xl font-semibold text-gray-700">No flights found for this route.</h3>
          <p className="mt-2 text-gray-500">Try adjusting your search criteria in the plan page.</p>
          <Link to="/plan" className="mt-6 inline-block bg-blue-500 text-white font-bold py-2 px-6 rounded-lg hover:bg-blue-600">
            Go Back to Planning
          </Link>
        </div>
      ) : (
        <div className="space-y-6 max-w-4xl mx-auto">
          {flights.map((flight) => (
            <div className="bg-white rounded-lg shadow-lg p-6 flex flex-col md:flex-row items-center justify-between" key={flight.id}>
              <div className="w-full md:w-3/4 flex flex-col md:flex-row items-center text-center md:text-left">
                <div className="flex items-center font-bold text-lg w-full md:w-1/4 mb-4 md:mb-0">
                  <Plane className="w-6 h-6 mr-2 text-blue-500" />
                  {flight.itineraries[0].segments[0].carrierCode} {flight.itineraries[0].segments[0].aircraft.code}
                </div>
                <div className="flex items-center justify-around w-full md:w-3/4">
                  <div className="font-semibold">
                    <div className="text-2xl">{new Date(flight.itineraries[0].segments[0].departure.at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                    <div className="text-sm text-gray-500">{from}</div>
                  </div>
                  <div className="text-center w-1/3">
                    <div className="text-sm text-gray-500">{flight.itineraries[0].duration.replace('PT','').replace('H','h ').replace('M','m')}</div>
                    <div className="w-full bg-gray-200 rounded-full h-1 my-1">
                      <div className="bg-blue-500 h-1 rounded-full"></div>
                    </div>
                    <div className="text-xs text-gray-400">Non-Stop</div>
                  </div>
                  <div className="font-semibold">
                    <div className="text-2xl">{new Date(flight.itineraries[0].segments[0].arrival.at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                    <div className="text-sm text-gray-500">{to}</div>
                  </div>
                </div>
              </div>
              <div className="w-full md:w-1/4 text-center md:text-right mt-6 md:mt-0">
                <div className="text-3xl font-bold text-gray-800">
                  ₹{flight.price.total}
                </div>
                <button className="mt-2 w-full md:w-auto bg-blue-500 text-white font-bold py-2 px-6 rounded-lg hover:bg-blue-600 transition-colors">
                  Select Flight
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </main>
  );
};

export default FlightResults;