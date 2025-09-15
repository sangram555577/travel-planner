import React, { useState, useEffect, useContext, useCallback, useRef } from 'react';
import { itineraryAPI, apiUtils } from '../services/api';
import { AuthContext } from '../context/AuthContext';
import { TripContext } from '../context/TripContext';
import { Plus, Save, Trash2, GripVertical, Calendar, Info, Plane, Building, MapPin, X, Edit, Clock, DollarSign } from 'lucide-react';
import FlightSearch from './FlightSearch';
import HotelSearch from './HotelSearch';

export default function ItineraryPlanner() {
  const { user, token } = useContext(AuthContext);
  const { tripDetails } = useContext(TripContext);

  // State management
  const [currentItinerary, setCurrentItinerary] = useState(null);
  const [itineraryItems, setItineraryItems] = useState([]);
  const [savedItineraries, setSavedItineraries] = useState([]);
  const [activeTab, setActiveTab] = useState('itinerary'); // itinerary, flights, hotels
  
  // Drag and drop state
  const [draggedItem, setDraggedItem] = useState(null);
  const [dragOverIndex, setDragOverIndex] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  
  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editingItem, setEditingItem] = useState(null);
  
  // Refs for drag and drop
  const dragCounter = useRef(0);

  // Load data on component mount
  useEffect(() => {
    if (user?.id) {
      loadSavedItineraries();
    }
  }, [user]);

  // Load itinerary items when current itinerary changes
  useEffect(() => {
    if (currentItinerary?.id) {
      loadItineraryItems();
    }
  }, [currentItinerary]);

  const loadSavedItineraries = async () => {
    try {
      const itineraries = await itineraryAPI.getItinerariesByUser(user.id);
      setSavedItineraries(itineraries);
      
      // Auto-select first itinerary if available
      if (itineraries.length > 0 && !currentItinerary) {
        setCurrentItinerary(itineraries[0]);
      }
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to load itineraries'));
    }
  };

  const loadItineraryItems = async () => {
    if (!currentItinerary?.id) return;
    
    try {
      const items = await itineraryAPI.getItems(currentItinerary.id);
      setItineraryItems(items.sort((a, b) => a.position - b.position));
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to load itinerary items'));
    }
  };

  const createNewItinerary = async () => {
    if (!user) {
      setError('Please log in to create an itinerary');
      return;
    }

    const tripName = prompt('Enter trip name:');
    if (!tripName) return;

    try {
      const itineraryData = {
        tripName,
        startDate: tripDetails.startDate || new Date().toISOString().split('T')[0],
        endDate: tripDetails.endDate || new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        activitiesJson: JSON.stringify([]),
        userId: user.id
      };

      const newItinerary = await itineraryAPI.createItinerary(itineraryData);
      setCurrentItinerary(newItinerary);
      await loadSavedItineraries();
      setSuccess('Itinerary created successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to create itinerary'));
    }
  };

  const deleteItinerary = async () => {
    if (!currentItinerary || !window.confirm('Are you sure you want to delete this itinerary?')) {
      return;
    }

    try {
      await itineraryAPI.deleteItinerary(currentItinerary.id);
      setCurrentItinerary(null);
      setItineraryItems([]);
      await loadSavedItineraries();
      setSuccess('Itinerary deleted successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to delete itinerary'));
    }
  };

  const removeItem = async (itemId) => {
    if (!currentItinerary || !window.confirm('Remove this item?')) return;

    try {
      await itineraryAPI.removeItem(currentItinerary.id, itemId);
      await loadItineraryItems();
      setSuccess('Item removed successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to remove item'));
    }
  };

  // Drag and drop handlers
  const handleDragStart = useCallback((e, item, index) => {
    setDraggedItem({ item, originalIndex: index });
    setIsDragging(true);
    e.dataTransfer.effectAllowed = 'move';
  }, []);

  const handleDragOver = useCallback((e, index) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setDragOverIndex(index);
  }, []);

  const handleDragLeave = useCallback((e) => {
    dragCounter.current -= 1;
    if (dragCounter.current === 0) {
      setDragOverIndex(null);
    }
  }, []);

  const handleDragEnter = useCallback((e) => {
    e.preventDefault();
    dragCounter.current += 1;
  }, []);

  const handleDrop = useCallback(async (e, dropIndex) => {
    e.preventDefault();
    setIsDragging(false);
    setDragOverIndex(null);
    dragCounter.current = 0;

    if (!draggedItem || !currentItinerary) return;

    const { originalIndex } = draggedItem;
    if (originalIndex === dropIndex) return;

    try {
      // Optimistically update UI
      const newItems = [...itineraryItems];
      const [movedItem] = newItems.splice(originalIndex, 1);
      newItems.splice(dropIndex, 0, movedItem);
      
      // Update positions
      const updatedItems = newItems.map((item, index) => ({
        ...item,
        position: index + 1
      }));
      
      setItineraryItems(updatedItems);

      // Persist to backend
      const itemOrders = updatedItems.map((item, index) => ({
        itemId: item.id,
        position: index + 1
      }));

      await itineraryAPI.reorderItems(currentItinerary.id, itemOrders);
      setSuccess('Items reordered successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      // Revert on error
      await loadItineraryItems();
      setError(apiUtils.handleApiError(error, 'Failed to reorder items'));
    }

    setDraggedItem(null);
  }, [draggedItem, currentItinerary, itineraryItems, loadItineraryItems]);

  const handleAddFromSearch = useCallback((item, type) => {
    // This will be called by FlightSearch and HotelSearch components
    loadItineraryItems(); // Refresh the items list
    setActiveTab('itinerary'); // Switch back to itinerary view
  }, [loadItineraryItems]);

  const renderItemIcon = (type) => {
    switch (type) {
      case 'flight': return <Plane className="w-5 h-5" />;
      case 'hotel': return <Building className="w-5 h-5" />;
      default: return <MapPin className="w-5 h-5" />;
    }
  };

  const renderItemDetails = (item) => {
    if (!item.meta) return null;
    
    try {
      const metadata = JSON.parse(item.meta);
      
      if (item.type === 'flight') {
        const flight = metadata.flight || metadata;
        return (
          <div className="text-sm text-gray-600 mt-1">
            <div className="flex items-center gap-2">
              <Clock className="w-3 h-3" />
              <span>Flight {flight.validatingAirlineCodes}</span>
              {flight.price && (
                <>
                  <DollarSign className="w-3 h-3" />
                  <span>{flight.currency} {flight.price}</span>
                </>
              )}
            </div>
          </div>
        );
      }
      
      if (item.type === 'hotel') {
        const { hotel, offer } = metadata;
        return (
          <div className="text-sm text-gray-600 mt-1">
            <div className="flex items-center gap-2">
              <Building className="w-3 h-3" />
              <span>{hotel?.name}</span>
              {offer?.price && (
                <>
                  <DollarSign className="w-3 h-3" />
                  <span>{offer.price.currency} {offer.price.total}</span>
                </>
              )}
            </div>
          </div>
        );
      }
      
      return null;
    } catch (e) {
      return null;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">
            Trip Planner
            {tripDetails.city && <span className="text-blue-600"> - {tripDetails.city}</span>}
          </h1>
          <p className="text-gray-600">
            Plan your perfect trip with flights, hotels, and activities
          </p>
        </div>

        {/* Error/Success Messages */}
        {error && (
          <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
            {error}
          </div>
        )}

        {success && (
          <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
            {success}
          </div>
        )}

        {/* Tab Navigation */}
        <div className="bg-white rounded-lg shadow-lg mb-6">
          <div className="border-b border-gray-200">
            <nav className="flex space-x-8 px-6">
              <button
                onClick={() => setActiveTab('itinerary')}
                className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                  activeTab === 'itinerary'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                My Itinerary
              </button>
              <button
                onClick={() => setActiveTab('flights')}
                className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                  activeTab === 'flights'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Search Flights
              </button>
              <button
                onClick={() => setActiveTab('hotels')}
                className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                  activeTab === 'hotels'
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Search Hotels
              </button>
            </nav>
          </div>

          {/* Tab Content */}
          <div className="p-6">
            {activeTab === 'itinerary' && (
              <div>
                {/* Itinerary Header */}
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
                  <div>
                    <h2 className="text-xl font-semibold text-gray-800">
                      {currentItinerary ? currentItinerary.tripName : 'No Itinerary Selected'}
                    </h2>
                    {currentItinerary && (
                      <p className="text-gray-600 text-sm">
                        {currentItinerary.startDate} to {currentItinerary.endDate}
                      </p>
                    )}
                  </div>
                  
                  <div className="flex gap-2">
                    <select
                      value={currentItinerary?.id || ''}
                      onChange={(e) => {
                        const itinerary = savedItineraries.find(it => it.id === parseInt(e.target.value));
                        setCurrentItinerary(itinerary);
                      }}
                      className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    >
                      <option value="">Select Itinerary</option>
                      {savedItineraries.map(itinerary => (
                        <option key={itinerary.id} value={itinerary.id}>
                          {itinerary.tripName}
                        </option>
                      ))}
                    </select>
                    
                    <button
                      onClick={createNewItinerary}
                      className="flex items-center bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
                    >
                      <Plus className="w-4 h-4 mr-1" />
                      New Trip
                    </button>
                    
                    {currentItinerary && (
                      <button
                        onClick={deleteItinerary}
                        className="flex items-center bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
                      >
                        <Trash2 className="w-4 h-4 mr-1" />
                        Delete
                      </button>
                    )}
                  </div>
                </div>

                {/* Itinerary Items */}
                {currentItinerary ? (
                  <div>
                    {itineraryItems.length === 0 ? (
                      <div className="text-center py-16 text-gray-500 border-2 border-dashed border-gray-300 rounded-lg">
                        <Info className="w-12 h-12 mx-auto mb-4 text-gray-300" />
                        <p className="text-lg font-medium mb-2">Your itinerary is empty</p>
                        <p>Use the Flight and Hotel tabs to search and add items to your trip</p>
                      </div>
                    ) : (
                      <div className="space-y-3">
                        {itineraryItems.map((item, index) => (
                          <div
                            key={`${item.id}-${item.position}`}
                            draggable
                            onDragStart={(e) => handleDragStart(e, item, index)}
                            onDragOver={(e) => handleDragOver(e, index)}
                            onDragEnter={handleDragEnter}
                            onDragLeave={handleDragLeave}
                            onDrop={(e) => handleDrop(e, index)}
                            className={`bg-white border rounded-lg p-4 cursor-move transition-all ${
                              dragOverIndex === index && isDragging
                                ? 'border-blue-500 bg-blue-50'
                                : 'border-gray-200 hover:border-gray-300'
                            }`}
                          >
                            <div className="flex items-start gap-3">
                              <GripVertical className="w-5 h-5 text-gray-400 mt-1 cursor-grab active:cursor-grabbing" />
                              
                              <div className={`p-2 rounded-lg ${
                                item.type === 'flight' ? 'bg-blue-100 text-blue-600' :
                                item.type === 'hotel' ? 'bg-green-100 text-green-600' :
                                'bg-gray-100 text-gray-600'
                              }`}>
                                {renderItemIcon(item.type)}
                              </div>
                              
                              <div className="flex-1 min-w-0">
                                <div className="flex items-start justify-between">
                                  <div>
                                    <h3 className="font-medium text-gray-900 truncate">
                                      {item.title}
                                    </h3>
                                    {item.description && (
                                      <p className="text-sm text-gray-600 mt-1">
                                        {item.description}
                                      </p>
                                    )}
                                    {renderItemDetails(item)}
                                  </div>
                                  
                                  <div className="flex items-center gap-2 ml-4">
                                    {item.price && (
                                      <span className="text-sm font-medium text-gray-900">
                                        {item.currency} {item.price}
                                      </span>
                                    )}
                                    
                                    <button
                                      onClick={() => removeItem(item.id)}
                                      className="text-red-600 hover:text-red-800 p-1 rounded hover:bg-red-50"
                                    >
                                      <X className="w-4 h-4" />
                                    </button>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ) : (
                  <div className="text-center py-16 text-gray-500">
                    <Info className="w-12 h-12 mx-auto mb-4 text-gray-300" />
                    <p className="text-lg font-medium mb-2">No itinerary selected</p>
                    <p>Create a new itinerary or select an existing one to start planning</p>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'flights' && (
              <FlightSearch
                onAddToItinerary={handleAddFromSearch}
                selectedItinerary={currentItinerary}
              />
            )}

            {activeTab === 'hotels' && (
              <HotelSearch
                onAddToItinerary={handleAddFromSearch}
                selectedItinerary={currentItinerary}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
}