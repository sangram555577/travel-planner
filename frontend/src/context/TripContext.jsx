import React, { createContext, useState, useEffect } from 'react';

export const TripContext = createContext(null);

export const TripProvider = ({ children }) => {
    const [tripDetails, setTripDetails] = useState({
        city: '',
        startDate: '',
        endDate: '',
        travelType: '',
    });

    // Load initial data from localStorage
    useEffect(() => {
        const savedCity = localStorage.getItem('selectedCity');
        const savedStart = localStorage.getItem('tripStartDate');
        const savedEnd = localStorage.getItem('tripEndDate');
        if (savedCity && savedStart && savedEnd) {
            setTripDetails({
                city: savedCity,
                startDate: savedStart,
                endDate: savedEnd,
                travelType: '', // travelType is not persisted, can be re-selected
            });
        }
    }, []);

    const value = { tripDetails, setTripDetails };

    return <TripContext.Provider value={value}>{children}</TripContext.Provider>;
};