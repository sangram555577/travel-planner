import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';
import { TripContext } from '../context/TripContext'; // Assuming a TripContext exists
import { Plus, Save, Trash2, GripVertical, Calendar, Info } from 'lucide-react';

export default function ItineraryPlanner() {
  const { user, token } = useContext(AuthContext);
  const { tripDetails } = useContext(TripContext); // Get trip details from context

  const [destinations, setDestinations] = useState([]);
  const [days, setDays] = useState([]);
  const [draggedItem, setDraggedItem] = useState(null);
  const [savedItineraries, setSavedItineraries] = useState([]);

  useEffect(() => {
    // Fetch destinations for the selected city
    if (tripDetails.city) {
      axios.get(`http://localhost:8080/api/v1/destinations`)
        .then(res => {
          const cityDestinations = res.data.filter(d => d.city.toLowerCase() === tripDetails.city.toLowerCase());
          setDestinations(cityDestinations);
        })
        .catch(err => console.error('Error fetching destinations:', err));
    }

    // Fetch user's saved itineraries
    if (user?.id) {
      axios.get(`http://localhost:8080/api/v1/itineraries/user/${user.id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      .then(res => setSavedItineraries(res.data))
      .catch(err => console.error('Error fetching itineraries:', err));
    }
  }, [user, token, tripDetails.city]);

  const addDay = () => setDays([...days, { date: '', activities: [] }]);
  const handleDateChange = (index, date) => {
      const newDays = [...days];
      newDays[index].date = date;
      setDays(newDays);
  };
  const addActivityToDay = (dayIndex, activity) => {
    const newDays = [...days];
    newDays[dayIndex].activities.push(activity);
    setDays(newDays);
  };
  const handleSave = async () => {
    if (!user) return alert('Please log in to save.');
    if (days.length === 0) return alert('Add at least one day to your itinerary.');
    
    const itineraryData = {
      tripName: `${tripDetails.city} Trip from ${tripDetails.startDate}`,
      startDate: tripDetails.startDate,
      endDate: tripDetails.endDate,
      activitiesJson: JSON.stringify(days),
      userId: user.id,
    };

    try {
      await axios.post('http://localhost:8080/api/v1/itineraries', itineraryData, { headers: { 'Authorization': `Bearer ${token}` } });
      alert('Itinerary saved!');
       // Refresh list
      const res = await axios.get(`http://localhost:8080/api/v1/itineraries/user/${user.id}`, { headers: { 'Authorization': `Bearer ${token}` } });
      setSavedItineraries(res.data);
    } catch (err) {
      alert('Failed to save itinerary.');
    }
  };
  const clearItinerary = () => {
    if (window.confirm('Are you sure?')) setDays([]);
  };

  return (
    <main className="container mx-auto my-10 pt-20 px-4">
      <h2 className="text-4xl font-extrabold text-gray-800 mb-8 text-center">
        Itinerary Planner: <span className="text-blue-600">{tripDetails.city}</span>
      </h2>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Activities */}
        <div className="lg:col-span-1 space-y-4">
           <div className="bg-white p-4 rounded-lg shadow-lg">
             <h3 className="font-bold text-xl mb-3">Activities in {tripDetails.city}</h3>
             <div className="space-y-2 max-h-96 overflow-y-auto">
              {destinations.map(place => (
                <div key={place.id} draggable onDragStart={() => setDraggedItem({name: place.location, type: 'destination'})}
                     className="p-3 bg-gray-100 border rounded-lg flex items-center cursor-grab active:cursor-grabbing">
                  <GripVertical className="h-5 w-5 text-gray-400 mr-3"/>
                  <span>{place.location}</span>
                </div>
              ))}
             </div>
           </div>
           {savedItineraries.length > 0 && (
             <div className="bg-white p-4 rounded-lg shadow-lg">
                <h3 className="font-bold text-xl mb-3">Your Saved Trips</h3>
                <ul className="list-disc list-inside space-y-1 text-gray-700">
                    {savedItineraries.map(it => <li key={it.id}>{it.tripName}</li>)}
                </ul>
             </div>
           )}
        </div>

        {/* Right Column: Itinerary */}
        <div className="lg:col-span-2">
          <div className="bg-white p-6 rounded-lg shadow-lg">
            <div className="flex flex-wrap justify-between items-center mb-6 gap-4">
              <h3 className="text-2xl font-bold text-gray-800">Your Plan</h3>
              <div className="flex gap-2">
                <button onClick={addDay} className="flex items-center bg-blue-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-blue-600"><Plus size={18} className="mr-1"/> Add Day</button>
                <button onClick={handleSave} className="flex items-center bg-green-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-green-600"><Save size={18} className="mr-1"/> Save</button>
                <button onClick={clearItinerary} className="flex items-center bg-red-500 text-white font-bold py-2 px-4 rounded-lg hover:bg-red-600"><Trash2 size={18} className="mr-1"/> Clear</button>
              </div>
            </div>
            {days.length === 0 ? (
              <div className="text-center py-16 text-gray-500 border-2 border-dashed rounded-lg">
                  <Info className="w-12 h-12 mx-auto mb-4 text-gray-300" />
                  <p>Your itinerary is empty.</p>
                  <p>Click "+ Add Day" to start or drag an activity here.</p>
              </div>
            ) : (
              <div className="space-y-6">
                {days.map((day, index) => (
                  <div key={index} onDragOver={e => e.preventDefault()} onDrop={() => addActivityToDay(index, draggedItem)}
                       className="bg-gray-50 p-4 rounded-lg border">
                    <div className="flex justify-between items-center mb-3">
                      <h4 className="font-bold text-lg">Day {index + 1}</h4>
                      <div className="relative">
                        <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400"/>
                        <input type="date" value={day.date} onChange={e => handleDateChange(index, e.target.value)}
                               min={tripDetails.startDate} max={tripDetails.endDate}
                               className="pl-9 pr-2 py-1 border rounded-md text-sm"/>
                      </div>
                    </div>
                    <div className="min-h-[80px] bg-white rounded border-dashed border-2 p-2 space-y-2">
                      {day.activities.map((act, actIndex) => (
                        <div key={actIndex} className="bg-blue-100 text-blue-800 p-2 rounded">{act.name}</div>
                      ))}
                      {day.activities.length === 0 && <p className="text-xs text-center text-gray-400 p-4">Drop activities here</p>}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}