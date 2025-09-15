import React, { useState, useContext, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Compass, AlertCircle } from 'lucide-react';

const LoginSignup = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ fullName: '', email: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { user, login, register } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate('/plan');
    }
  }, [user, navigate]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      let result;
      if (isLogin) {
        result = await login({ 
          email: formData.email, 
          password: formData.password 
        });
      } else {
        result = await register({ 
          fullName: formData.fullName, 
          email: formData.email, 
          phone: formData.phone, 
          password: formData.password 
        });
      }
      
      if (!result.success) {
        setError(result.message || 'Authentication failed');
      }
    } catch (err) {
      setError('An unexpected error occurred. Please try again.');
      console.error('Authentication error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main
      className="flex items-center justify-center min-h-screen bg-cover bg-center px-4 relative"
      style={{ backgroundImage: "url('https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=2070&auto=format&fit=crop')" }}
    >
      <div className="absolute inset-0 hero-gradient opacity-80"></div>
      <div className="w-full max-w-md glass rounded-2xl shadow-hard p-8 text-center relative z-10 animate-slide-up">
        <div className="flex flex-col items-center mb-8">
          <div className="p-3 bg-primary-500 rounded-2xl mb-4">
            <Compass className="h-10 w-10 text-white" />
          </div>
          <h2 className="text-3xl font-bold text-white mb-2">
            Welcome to <span className="text-yellow-400">TripCraft</span>
          </h2>
          <p className="text-white/80">Your journey begins here</p>
        </div>

        <div className="w-full bg-white/20 backdrop-blur-sm rounded-full p-1 flex mb-8">
          <button
            onClick={() => { setIsLogin(true); setError(''); }}
            className={`w-1/2 p-3 rounded-full font-semibold transition-all duration-300 ${isLogin ? 'bg-white text-primary-600 shadow-soft' : 'text-white/80 hover:text-white'}`}
          >
            Login
          </button>
          <button
            onClick={() => { setIsLogin(false); setError(''); }}
            className={`w-1/2 p-3 rounded-full font-semibold transition-all duration-300 ${!isLogin ? 'bg-white text-primary-600 shadow-soft' : 'text-white/80 hover:text-white'}`}
          >
            Signup
          </button>
        </div>
        
        <form onSubmit={handleSubmit} className="space-y-6">
          {!isLogin && (
            <input 
              type="text" 
              name="fullName" 
              value={formData.fullName} 
              onChange={handleChange} 
              className="input bg-white/90 backdrop-blur-sm text-secondary-900 placeholder-secondary-500" 
              placeholder="Full Name" 
              required 
            />
          )}
          <input 
            type="email" 
            name="email" 
            value={formData.email} 
            onChange={handleChange} 
            className="input bg-white/90 backdrop-blur-sm text-secondary-900 placeholder-secondary-500" 
            placeholder="Email" 
            required 
          />
          {!isLogin && (
            <input 
              type="tel" 
              name="phone" 
              value={formData.phone} 
              onChange={handleChange} 
              className="input bg-white/90 backdrop-blur-sm text-secondary-900 placeholder-secondary-500" 
              placeholder="Phone" 
              required 
            />
          )}
          <input 
            type="password" 
            name="password" 
            value={formData.password} 
            onChange={handleChange} 
            className="input bg-white/90 backdrop-blur-sm text-secondary-900 placeholder-secondary-500" 
            placeholder="Password" 
            required 
          />
          
          {error && (
            <div className="bg-accent-100 border border-accent-400 text-accent-700 px-4 py-3 rounded-lg relative flex items-center backdrop-blur-sm animate-slide-up" role="alert">
              <AlertCircle className="w-5 h-5 mr-2 flex-shrink-0" />
              <span className="block sm:inline font-medium">{error}</span>
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full py-4 disabled:bg-primary-300 disabled:cursor-not-allowed disabled:transform-none flex justify-center items-center text-lg font-semibold"
          >
            {loading ? (
              <div className="loading-spinner h-6 w-6"></div>
            ) : (
              isLogin ? 'Welcome Back' : 'Create Account'
            )}
          </button>
        </form>

        <p className="mt-8 text-sm">
          <Link to={'/'} className="font-semibold text-white/80 hover:text-white transition-colors inline-flex items-center gap-1">
            ← Back to Home
          </Link>
        </p>
      </div>
    </main>
  );
};

export default LoginSignup;