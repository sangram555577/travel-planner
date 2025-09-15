import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './components/Home';
import LoginSignup from './components/LoginSignup';
import PlanTrip from './components/PlanTrip';
import ExploreDestination from './components/ExploreDestination';
import Destination from './components/Destination';
import Hotel from './components/Hotel';
import FlightResults from './components/FlightResults';
import ItineraryPlanner from './components/ItineraryPlanner';
import Expenses from './components/Expenses';
import AdminPage from './pages/AdminPage';
import ProtectedRoute from './components/ProtectedRoute';
import AdminRoute from './components/AdminRoute';
import Navbar from './components/Navbar';
import Footer from './components/Footer';

function App() {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Navbar />
      <main className="flex-grow">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginSignup />} />
          <Route path="/hotels" element={<Hotel />} />
          <Route path="/destination/:id" element={<Destination />} />
          <Route path="/flights" element={<FlightResults />} />
          <Route path="/explore-destination" element={<ExploreDestination />} />

          {/* Protected Routes */}
          <Route 
            path="/plan" 
            element={<ProtectedRoute><PlanTrip /></ProtectedRoute>} 
          />
          <Route 
            path="/expenses" 
            element={<ProtectedRoute><Expenses /></ProtectedRoute>} 
          />
          <Route 
            path="/itinerary" 
            element={<ProtectedRoute><ItineraryPlanner /></ProtectedRoute>} 
          />
          
          {/* Admin Route */}
          <Route 
            path="/admin" 
            element={<AdminRoute><AdminPage /></AdminRoute>} 
          />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;