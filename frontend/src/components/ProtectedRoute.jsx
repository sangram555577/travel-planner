import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Plane } from 'lucide-react';

const ProtectedRoute = ({ children }) => {
    const { user } = useContext(AuthContext);

    if (user === null) {
        // You can add a loading spinner here while the context is initializing
        return (
            <div className="flex flex-col justify-center items-center h-screen bg-gray-100">
                <Plane className="animate-bounce w-16 h-16 text-blue-500" />
                <p className="mt-4 text-lg text-gray-600">Loading...</p>
            </div>
        );
    }
    
    if (!user) {
        // If the user is not logged in after initialization, redirect to the /login page
        return <Navigate to="/login" replace />;
    }

    // If the user is logged in, render the child components (the protected page)
    return children;
};

export default ProtectedRoute;