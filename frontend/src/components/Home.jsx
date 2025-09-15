import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Globe, Shield, Sparkles, Route, DollarSign, Calendar, Star, ArrowRight, PlayCircle } from 'lucide-react';

const Home = () => {
  const { user } = useContext(AuthContext);

  const features = [
    { 
      icon: <Globe size={32} className="text-primary-600" />, 
      title: "Smart Destination Discovery", 
      description: "AI-powered recommendations tailored to your preferences" 
    },
    { 
      icon: <Calendar size={32} className="text-primary-600" />, 
      title: "Intelligent Planning", 
      description: "Automated itinerary creation with optimal scheduling" 
    },
    { 
      icon: <DollarSign size={32} className="text-primary-600" />, 
      title: "Budget Optimization", 
      description: "Real-time expense tracking and cost-saving suggestions" 
    },
    { 
      icon: <Route size={32} className="text-primary-600" />, 
      title: "Route Optimization", 
      description: "Efficient pathfinding for maximum exploration" 
    },
    { 
      icon: <Shield size={32} className="text-primary-600" />, 
      title: "Travel Insurance", 
      description: "Comprehensive coverage for peace of mind" 
    },
    { 
      icon: <Sparkles size={32} className="text-primary-600" />, 
      title: "Premium Experience", 
      description: "Curated recommendations from local experts" 
    },
  ];

  const stats = [
    { value: "8M+", label: "Trips planned" },
    { value: "33K+", label: "Reviews" },
    { value: "4.9★", label: "App Store Rating" },
    { value: "4.7★", label: "Google Play Rating" },
  ];

  return (
    <div className="bg-white">
      {/* Hero Section */}
      <section className="relative min-h-screen flex items-center justify-center overflow-hidden">
        {/* Background with overlay */}
        <div 
          className="absolute inset-0 bg-cover bg-center bg-no-repeat"
          style={{ backgroundImage: "url('https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=2070&auto=format&fit=crop')" }}
        >
          <div className="absolute inset-0 hero-gradient opacity-90"></div>
        </div>
        
        {/* Content */}
        <div className="relative z-10 text-center px-4 max-w-6xl mx-auto">
          <div className="animate-fade-in">
            <h1 className="text-6xl md:text-8xl font-extrabold text-white mb-6 tracking-tight">
              Trip<span className="text-yellow-400">Craft</span>
            </h1>
            <p className="text-xl md:text-2xl text-white/90 mb-8 max-w-3xl mx-auto leading-relaxed">
              Create extraordinary journeys with our AI-powered travel planning platform. 
              <span className="block mt-2 font-semibold text-yellow-400">Plan • Explore • Experience</span>
            </p>
            
            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
              {!user ? (
                <>
                  <Link
                    to="/login"
                    className="btn-primary text-lg px-8 py-4 inline-flex items-center gap-2 group"
                  >
                    Start Your Journey
                    <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                  </Link>
                  <Link
                    to="/explore-destination"
                    className="btn-secondary text-lg px-8 py-4 inline-flex items-center gap-2"
                  >
                    <PlayCircle className="w-5 h-5" />
                    Explore Destinations
                  </Link>
                </>
              ) : (
                <Link
                  to="/plan"
                  className="btn-primary text-lg px-8 py-4 inline-flex items-center gap-2 group"
                >
                  Plan New Trip
                  <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                </Link>
              )}
            </div>
          </div>
        </div>
        
        {/* Floating elements */}
        <div className="absolute top-20 left-10 w-4 h-4 bg-yellow-400 rounded-full animate-bounce" style={{animationDelay: '0s'}}></div>
        <div className="absolute top-32 right-20 w-3 h-3 bg-blue-300 rounded-full animate-bounce" style={{animationDelay: '0.5s'}}></div>
        <div className="absolute bottom-40 left-20 w-2 h-2 bg-white rounded-full animate-bounce" style={{animationDelay: '1s'}}></div>
      </section>

      {/* Features Section */}
      <section className="section bg-secondary-50">
        <div className="container mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-5xl font-bold text-secondary-900 mb-6">
              Why Choose <span className="text-gradient">TripCraft</span>?
            </h2>
            <p className="text-xl text-secondary-600 max-w-3xl mx-auto leading-relaxed">
              Experience the future of travel planning with our comprehensive suite of intelligent features
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, idx) => (
              <div 
                key={idx} 
                className="card group hover-lift animate-slide-up"
                style={{animationDelay: `${idx * 0.1}s`}}
              >
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0 p-3 bg-primary-50 rounded-xl group-hover:bg-primary-100 transition-colors">
                    {feature.icon}
                  </div>
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-secondary-900 mb-2 group-hover:text-primary-600 transition-colors">
                      {feature.title}
                    </h3>
                    <p className="text-secondary-600 leading-relaxed">
                      {feature.description}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="section bg-primary-600 text-white relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-primary-700 to-primary-500"></div>
        <div className="container mx-auto px-6 relative z-10">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold mb-4">Trusted by Millions</h2>
            <p className="text-primary-100 text-lg">Join our community of happy travelers worldwide</p>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat, idx) => (
              <div key={idx} className="text-center group">
                <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 group-hover:bg-white/20 transition-all duration-300">
                  <h3 className="text-4xl md:text-5xl font-extrabold mb-2 text-yellow-400">{stat.value}</h3>
                  <p className="text-primary-100 font-medium">{stat.label}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Reviews Section */}
      <section className="section bg-white">
        <div className="container mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-5xl font-bold text-secondary-900 mb-6">What Our Travelers Say</h2>
            <p className="text-xl text-secondary-600 max-w-3xl mx-auto">Don't just take our word for it - hear from real travelers who've transformed their adventures</p>
          </div>
          
          <div className="max-w-4xl mx-auto">
            <div className="card-elevated text-center relative">
              <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                <div className="flex space-x-1 bg-yellow-400 px-4 py-2 rounded-full">
                  {[...Array(5)].map((_, i) => (
                    <Star key={i} className="w-5 h-5 text-white fill-current" />
                  ))}
                </div>
              </div>
              
              <div className="pt-8">
                <h3 className="text-2xl font-bold text-secondary-900 mb-4">A Lifesaver for Family Adventures!</h3>
                <blockquote className="text-lg text-secondary-700 leading-relaxed mb-6 italic">
                  "Planning a family vacation used to be overwhelming, but TripCraft transformed the entire experience. 
                  The AI recommendations were spot-on, the budget tracking kept us on track, and the itinerary builder 
                  made coordination effortless. Our trip to Europe was seamless from start to finish!"
                </blockquote>
                <div className="flex items-center justify-center space-x-4">
                  <div className="w-12 h-12 bg-gradient-to-r from-primary-400 to-primary-600 rounded-full flex items-center justify-center">
                    <span className="text-white font-bold text-lg">JK</span>
                  </div>
                  <div className="text-left">
                    <p className="font-semibold text-secondary-900">Jennifer K.</p>
                    <p className="text-secondary-600">Family Traveler from Chicago, IL</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
      
      {/* CTA Section */}
      <section className="section bg-secondary-900 text-white relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-secondary-800 to-secondary-900"></div>
        <div className="container mx-auto px-6 text-center relative z-10">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">Ready to Craft Your Perfect Trip?</h2>
          <p className="text-xl text-secondary-300 mb-8 max-w-2xl mx-auto">
            Join thousands of travelers who've discovered the joy of stress-free planning
          </p>
          <Link 
            to={user ? "/plan" : "/login"}
            className="btn-primary text-lg px-10 py-4 inline-flex items-center gap-2 group"
          >
            {user ? "Plan Your Next Trip" : "Start Planning Today"}
            <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>
      </section>
    </div>
  );
};

export default Home;