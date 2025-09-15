import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { TripContext } from '../context/TripContext';
import { MapPin, Calendar, Users } from 'lucide-react';

const PlanTrip = () => {
    const navigate = useNavigate();
    const { setTripDetails } = useContext(TripContext);
    const [formData, setFormData] = useState({
        city: '',
        startDate: '',
        endDate: '',
        travelType: '',
    });

    useEffect(() => {
        const savedCity = localStorage.getItem('selectedCity');
        const savedStart = localStorage.getItem('tripStartDate');
        const savedEnd = localStorage.getItem('tripEndDate');

        setFormData((prev) => ({
            ...prev,
            city: savedCity || '',
            startDate: savedStart || '',
            endDate: savedEnd || '',
        }));
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));

        if (name === 'city') localStorage.setItem('selectedCity', value);
        if (name === 'startDate') localStorage.setItem('tripStartDate', value);
        if (name === 'endDate') localStorage.setItem('tripEndDate', value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Update the global context with the new trip details
        setTripDetails(formData);
        
        const { city, startDate, endDate, travelType } = formData;
        navigate(
            `/explore-destination?city=${city}&startDate=${startDate}&endDate=${endDate}&travelType=${travelType}`
        );
    };

    return (
        <div
            className="min-h-screen flex items-center justify-center bg-cover bg-center p-4 pt-16 relative"
            style={{ backgroundImage: "url('https://images.unsplash.com/photo-1526772662000-3f88f10405ff?q=80&w=1974&auto=format&fit=crop')" }}
        >
            <div className="absolute inset-0 hero-gradient opacity-75"></div>
            <div className="w-full max-w-2xl glass rounded-2xl shadow-hard p-8 space-y-8 relative z-10 animate-slide-up">
                <div className="text-center">
                    <h2 className="text-4xl font-bold text-white mb-4">
                        Plan Your Perfect <span className="text-yellow-400">Adventure</span>
                    </h2>
                    <p className="text-white/80 text-lg">Tell us your preferences and we'll craft the ideal itinerary</p>
                </div>
                <form onSubmit={handleSubmit} className="space-y-8">
                    <div className="space-y-2">
                        <label htmlFor="city" className="block text-sm font-semibold text-white mb-3">
                            Where would you like to go?
                        </label>
                        <div className="relative">
                            <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-primary-500" />
                            <select
                                id="city"
                                name="city"
                                className="input bg-white/90 backdrop-blur-sm text-secondary-900 pl-12"
                                value={formData.city}
                                onChange={handleChange}
                                required
                            >
                                <option value="">-- Select a place --</option>
                                <option value="Pune">Pune</option>
                                <option value="Jaipur">Jaipur</option>
                                <option value="Udaipur">Udaipur</option>
                                <option value="Manali">Manali</option>
                                <option value="Shimla">Shimla</option>
                                <option value="Kullu">Kullu</option>
                                <option value="Satara">Satara</option>
                                <option value="Nashik">Nashik</option>
                                <option value="Nagpur">Nagpur</option>
                                <option value="Mumbai">Mumbai</option>
                            </select>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <label htmlFor="startDate" className="block text-sm font-semibold text-white mb-3">Start Date</label>
                            <div className="relative">
                                <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-primary-500" />
                                <input
                                    type="date"
                                    id="startDate"
                                    name="startDate"
                                    className="input bg-white/90 backdrop-blur-sm text-secondary-900 pl-12"
                                    value={formData.startDate}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                        <div className="space-y-2">
                            <label htmlFor="endDate" className="block text-sm font-semibold text-white mb-3">End Date</label>
                            <div className="relative">
                               <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-primary-500" />
                                <input
                                    type="date"
                                    id="endDate"
                                    name="endDate"
                                    className="input bg-white/90 backdrop-blur-sm text-secondary-900 pl-12"
                                    value={formData.endDate}
                                    min={formData.startDate}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label htmlFor="travelType" className="block text-sm font-semibold text-white mb-3">Travel Style</label>
                         <div className="relative">
                            <Users className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-primary-500" />
                            <select
                                id="travelType"
                                name="travelType"
                                className="input bg-white/90 backdrop-blur-sm text-secondary-900 pl-12"
                                value={formData.travelType}
                                onChange={handleChange}
                                required
                            >
                                <option value="">-- Select your travel style --</option>
                                <option value="solo">Solo Adventure</option>
                                <option value="couple">Romantic Getaway</option>
                                <option value="group">Group Trip</option>
                                <option value="family">Family Vacation</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" className="btn-primary w-full py-4 text-lg font-semibold">
                        Let's Craft Your Adventure ✨
                    </button>
                </form>
            </div>
        </div>
    );
};

export default PlanTrip;