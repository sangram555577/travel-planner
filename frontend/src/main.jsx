import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { TripProvider } from './context/TripContext'; // <-- IMPORT THIS
import App from './App';
import './styles.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <TripProvider> {/* <-- WRAP WITH THIS */}
          <App />
        </TripProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);