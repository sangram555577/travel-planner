import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import { MapPin, Sun, Wind, List, Eye, AlertTriangle } from 'lucide-react';

const Destination = () => {
    const { id } = useParams();
    const [destination, setDestination] = useState(null);
    const [weather, setWeather] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDestinationData = async () => {
            try {
                setLoading(true);
                const destResponse = await axios.get(`http://localhost:8080/api/v1/destinations/${id}`);
                setDestination(destResponse.data);

                if (destResponse.data.city) {
                    const weatherResponse = await axios.get(`http://localhost:8080/api/v1/destinations/weather/${destResponse.data.city}`);
                    setWeather(weatherResponse.data);
                }
            } catch (err) {
                setError("Destination not found or failed to load data.");
            } finally {
                setLoading(false);
            }
        };

        fetchDestinationData();
    }, [id]);

    if (loading) {
        return <DestinationSkeleton />;
    }

    if (error) {
        return (
            <div className="container mx-auto mt-20 pt-10 text-center">
                <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-6 rounded-lg shadow-md max-w-lg mx-auto">
                    <div className="flex items-center">
                        <AlertTriangle className="h-8 w-8 mr-3" />
                        <div>
                            <h4 className="font-bold text-xl">Error Loading Page</h4>
                            <p>{error}</p>
                            <Link to="/plan" className="mt-4 inline-block bg-red-500 text-white font-bold py-2 px-4 rounded hover:bg-red-600">
                                Back to Planning
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    const popularSpotsList = destination.popularSpots?.split(',').map(spot => spot.trim()) || [];

    return (
        <div className="bg-gray-50">
            {/* Hero Section */}
            <header
                className="relative h-[60vh] bg-cover bg-center flex items-end p-8 text-white"
                style={{ backgroundImage: `url('${destination.backImage}')` }}
            >
                <div className="absolute inset-0 bg-black/50"></div>
                <div className="relative z-10 max-w-2xl">
                    <h1 className="text-5xl md:text-6xl font-extrabold">{destination.location}</h1>
                    <p className="text-2xl font-light text-gray-200 flex items-center mt-2">
                        <MapPin className="mr-2" /> {destination.city}
                    </p>
                    {weather && (
                        <div className="mt-4 bg-white/20 backdrop-blur-sm rounded-lg p-3 inline-flex items-center space-x-4 text-sm">
                            <span className="flex items-center"><Sun className="w-5 h-5 mr-1.5 text-yellow-300" /> {weather.temperature}°C</span>
                            <span className="flex items-center"><Wind className="w-5 h-5 mr-1.5 text-blue-200" /> {weather.windspeed} km/h</span>
                        </div>
                    )}
                </div>
            </header>

            {/* Main Content */}
            <main className="container mx-auto p-8">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Left Column */}
                    <div className="lg:col-span-2">
                        <div className="bg-white p-6 rounded-lg shadow-md">
                           <h2 className="text-3xl font-bold text-gray-800 mb-4 flex items-center"><Eye className="mr-3 text-blue-500"/>Overview</h2>
                           <p className="text-gray-600 leading-relaxed mb-6">{destination.description}</p>
                           <img src={destination.overviewImage} alt="Overview" className="rounded-lg w-full object-cover"/>
                        </div>
                    </div>
                    {/* Right Column */}
                    <div>
                         <div className="bg-white p-6 rounded-lg shadow-md">
                            <h2 className="text-3xl font-bold text-gray-800 mb-4 flex items-center"><List className="mr-3 text-blue-500"/>Popular Spots</h2>
                            <ul className="space-y-3">
                                {popularSpotsList.map((spot, idx) => (
                                    <li key={idx} className="flex items-center text-gray-700">
                                       <MapPin size={16} className="mr-3 text-gray-400 flex-shrink-0" />
                                       <span>{spot}</span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

// Skeleton Loader Component
const DestinationSkeleton = () => (
    <div className="animate-pulse">
        <div className="h-[60vh] bg-gray-300"></div>
        <div className="container mx-auto p-8">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div className="lg:col-span-2 space-y-6">
                    <div className="h-8 bg-gray-300 rounded w-1/3"></div>
                    <div className="h-4 bg-gray-300 rounded w-full"></div>
                    <div className="h-4 bg-gray-300 rounded w-5/6"></div>
                    <div className="h-96 bg-gray-300 rounded-lg w-full"></div>
                </div>
                <div className="space-y-6">
                    <div className="h-8 bg-gray-300 rounded w-1/2"></div>
                    <div className="h-4 bg-gray-300 rounded w-full"></div>
                    <div className="h-4 bg-gray-300 rounded w-full"></div>
                    <div className="h-4 bg-gray-300 rounded w-4/5"></div>
                </div>
            </div>
        </div>
    </div>
);


export default Destination;