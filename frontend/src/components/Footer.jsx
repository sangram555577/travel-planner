import React from 'react';
import { Link } from 'react-router-dom';
import { Compass, MapPin, Calendar, DollarSign, Mail, Phone, MapIcon } from 'lucide-react';

const Footer = () => {
  const currentYear = new Date().getFullYear();
  
  return (
    <footer className="bg-secondary-900 text-white">
      <div className="container mx-auto px-6 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand Section */}
          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <div className="p-2 bg-primary-500 rounded-xl">
                <Compass className="h-6 w-6 text-white" />
              </div>
              <span className="text-2xl font-bold">TripCraft</span>
            </div>
            <p className="text-secondary-300 leading-relaxed">
              Your ultimate travel companion for planning extraordinary journeys. 
              Discover, plan, and experience the world like never before.
            </p>
          </div>
          
          {/* Quick Links */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">Quick Links</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="text-secondary-300 hover:text-white transition-colors flex items-center">
                  <span className="w-2 h-2 bg-primary-500 rounded-full mr-2"></span>
                  Home
                </Link>
              </li>
              <li>
                <Link to="/plan" className="text-secondary-300 hover:text-white transition-colors flex items-center">
                  <span className="w-2 h-2 bg-primary-500 rounded-full mr-2"></span>
                  Plan Trip
                </Link>
              </li>
              <li>
                <Link to="/hotels" className="text-secondary-300 hover:text-white transition-colors flex items-center">
                  <span className="w-2 h-2 bg-primary-500 rounded-full mr-2"></span>
                  Hotels
                </Link>
              </li>
              <li>
                <Link to="/explore-destination" className="text-secondary-300 hover:text-white transition-colors flex items-center">
                  <span className="w-2 h-2 bg-primary-500 rounded-full mr-2"></span>
                  Destinations
                </Link>
              </li>
            </ul>
          </div>
          
          {/* Features */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">Features</h3>
            <ul className="space-y-3">
              <li className="flex items-center text-secondary-300">
                <MapPin className="w-4 h-4 mr-2 text-primary-400" />
                Smart Destination Discovery
              </li>
              <li className="flex items-center text-secondary-300">
                <Calendar className="w-4 h-4 mr-2 text-primary-400" />
                Intelligent Itinerary Planning
              </li>
              <li className="flex items-center text-secondary-300">
                <DollarSign className="w-4 h-4 mr-2 text-primary-400" />
                Budget Optimization
              </li>
            </ul>
          </div>
          
          {/* Contact Info */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">Get in Touch</h3>
            <div className="space-y-3">
              <div className="flex items-center text-secondary-300">
                <Mail className="w-4 h-4 mr-3 text-primary-400" />
                <span>hello@tripcraft.com</span>
              </div>
              <div className="flex items-center text-secondary-300">
                <Phone className="w-4 h-4 mr-3 text-primary-400" />
                <span>+1 (555) 123-4567</span>
              </div>
              <div className="flex items-center text-secondary-300">
                <MapIcon className="w-4 h-4 mr-3 text-primary-400" />
                <span>San Francisco, CA</span>
              </div>
            </div>
          </div>
        </div>
        
        {/* Bottom Bar */}
        <div className="border-t border-secondary-700 mt-12 pt-8 flex flex-col md:flex-row justify-between items-center">
          <div className="text-secondary-300 text-sm mb-4 md:mb-0">
            © {currentYear} TripCraft. All rights reserved. Made with ❤️ for travelers worldwide.
          </div>
          <div className="flex space-x-6 text-sm text-secondary-300">
            <Link to="/privacy" className="hover:text-white transition-colors">
              Privacy Policy
            </Link>
            <Link to="/terms" className="hover:text-white transition-colors">
              Terms of Service
            </Link>
            <Link to="/support" className="hover:text-white transition-colors">
              Support
            </Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
