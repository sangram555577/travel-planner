import React, { useContext, useState } from 'react';
import { NavLink, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Compass, LogOut, LogIn, X, Menu } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const navLinks = [
    { to: "/", text: "Home" },
    { to: "/hotels", text: "Hotels" },
    { to: "/plan", text: "Plan Trip", private: true },
    { to: "/expenses", text: "Expenses", private: true },
    { to: "/itinerary", text: "Itinerary", private: true },
  ];

  const activeLinkStyle = {
    color: '#3b82f6', // blue-500
    fontWeight: '600',
  };

  return (
    <nav className="bg-white/95 backdrop-blur-md shadow-soft fixed top-0 left-0 right-0 z-50 border-b border-secondary-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div className="flex-shrink-0">
            <Link to="/" className="flex items-center space-x-2 text-2xl font-bold text-secondary-900 hover:text-primary-600 transition-colors">
              <div className="p-2 bg-primary-500 rounded-xl">
                <Compass className="h-6 w-6 text-white" />
              </div>
              <span className="text-gradient">TripCraft</span>
            </Link>
          </div>

          {/* Desktop Menu */}
          <div className="hidden md:block">
            <div className="ml-10 flex items-baseline space-x-4">
              {navLinks.map((link) => (
                (!link.private || user) && (
                  <NavLink
                    key={link.to}
                    to={link.to}
                    className={({ isActive }) => 
                      `nav-link ${isActive ? 'active' : ''} relative group`
                    }
                  >
                    {link.text}
                    <span className="absolute inset-x-0 -bottom-1 h-0.5 bg-primary-500 scale-x-0 group-hover:scale-x-100 transition-transform origin-left"></span>
                  </NavLink>
                )
              ))}
            </div>
          </div>

          {/* Login/Logout Button (Desktop) */}
          <div className="hidden md:block">
            {user ? (
              <button
                onClick={logout}
                className="btn-accent flex items-center py-2 px-4 text-sm"
              >
                <LogOut className="mr-2 h-4 w-4" />
                Logout
              </button>
            ) : (
              <Link
                to="/login"
                className="btn-primary flex items-center py-2 px-4 text-sm"
              >
                <LogIn className="mr-2 h-4 w-4" />
                Login
              </Link>
            )}
          </div>
          
          {/* Mobile Menu Button */}
          <div className="md:hidden flex items-center">
            <button onClick={() => setIsMenuOpen(!isMenuOpen)} className="inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-white hover:bg-gray-700 focus:outline-none">
              {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden border-t border-secondary-200">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3 bg-white/95 backdrop-blur-md">
            {navLinks.map((link) => (
              (!link.private || user) && (
                <NavLink
                  key={link.to}
                  to={link.to}
                  onClick={() => setIsMenuOpen(false)}
                  className={({ isActive }) => 
                    `nav-link ${isActive ? 'active' : ''} block px-3 py-2 rounded-md text-base font-medium`
                  }
                >
                  {link.text}
                </NavLink>
              )
            ))}
            {/* Login/Logout Button (Mobile) */}
            <div className="pt-4 pb-3 border-t border-secondary-200">
               {user ? (
                <button
                  onClick={() => { logout(); setIsMenuOpen(false); }}
                  className="btn-accent w-full flex items-center justify-center py-3"
                >
                  <LogOut className="mr-2 h-5 w-5" />
                  Logout
                </button>
              ) : (
                <Link
                  to="/login"
                  onClick={() => setIsMenuOpen(false)}
                  className="btn-primary w-full flex items-center justify-center py-3"
                >
                  <LogIn className="mr-2 h-5 w-5" />
                  Login
                </Link>
              )}
            </div>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;