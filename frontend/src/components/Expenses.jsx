import React, { useEffect, useState, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';
import { Plus, Trash2, Wallet, AlertTriangle } from 'lucide-react';

const Expenses = () => {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({ amount: '', category: '', description: '' });
  const [adding, setAdding] = useState(false);
  const { user, token } = useContext(AuthContext);

  useEffect(() => {
    if (!user) {
      setError("Please log in to manage expenses.");
      setLoading(false);
      return;
    }

    const fetchExpenses = async () => {
      setLoading(true);
      try {
        const response = await axios.get(`http://localhost:8080/api/v1/expenses/user/${user.id}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        setExpenses(response.data);
      } catch (err) {
        setError('Unable to fetch expenses. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchExpenses();
  }, [user, token]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleAddExpense = async (e) => {
    e.preventDefault();
    setAdding(true);
    setError(null);
    try {
      const expenseData = {
        amount: parseFloat(form.amount),
        category: form.category,
        description: form.description,
        userId: user.id,
      };
      const response = await axios.post('http://localhost:8080/api/v1/expenses', expenseData, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setExpenses(prev => [...prev, response.data].sort((a,b) => new Date(b.date) - new Date(a.date)));
      setForm({ amount: '', category: '', description: '' });
    } catch (err) {
      setError('Failed to add expense. Please check your input and try again.');
    } finally {
      setAdding(false);
    }
  };

  const handleDeleteExpense = async (id) => {
    const originalExpenses = [...expenses];
    setExpenses(expenses.filter((exp) => exp.id !== id));
    setError(null);
    try {
      await axios.delete(`http://localhost:8080/api/v1/expenses/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
    } catch {
      setError('Failed to delete expense. Please try again.');
      setExpenses(originalExpenses); // Revert on failure
    }
  };
  
  const totalAmount = expenses.reduce((sum, exp) => sum + exp.amount, 0);

  return (
    <main className="min-h-screen bg-secondary-50 pt-20">
      <div className="container mx-auto px-4 py-10 max-w-6xl">
        <div className="text-center mb-12 animate-fade-in">
          <h1 className="text-5xl md:text-6xl font-extrabold text-secondary-900 mb-4">
            <span className="text-gradient">Expense</span> Tracker
          </h1>
          <p className="text-xl text-secondary-600 max-w-2xl mx-auto">
            Keep track of your travel expenses and stay within budget
          </p>
        </div>
      
        <div className="card-elevated mb-8">
          <h3 className="text-2xl font-bold text-secondary-900 mb-6 flex items-center">
            <Plus className="w-6 h-6 mr-3 text-primary-500" />
            Add New Expense
          </h3>
          <form className="grid grid-cols-1 md:grid-cols-4 gap-6 items-end" onSubmit={handleAddExpense}>
            <div className="space-y-2">
              <label className="block text-sm font-semibold text-secondary-700">Amount (₹)</label>
              <input 
                type="number" 
                name="amount" 
                className="input" 
                placeholder="50.75" 
                value={form.amount} 
                onChange={handleChange} 
                required 
                min="0.01" 
                step="0.01"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm font-semibold text-secondary-700">Category</label>
              <input 
                type="text" 
                name="category" 
                className="input" 
                placeholder="Food, Transport, etc." 
                value={form.category} 
                onChange={handleChange} 
                required
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm font-semibold text-secondary-700">Description</label>
              <input 
                type="text" 
                name="description" 
                className="input" 
                placeholder="Lunch at local restaurant" 
                value={form.description} 
                onChange={handleChange}
              />
            </div>
            <div>
              <button 
                className="btn-primary w-full flex justify-center items-center py-3 disabled:bg-primary-300 disabled:transform-none" 
                type="submit" 
                disabled={adding}
              >
                {adding ? ( 
                  <div className="loading-spinner h-5 w-5"></div> 
                ) : ( 
                  <>Add Expense</> 
                )}
              </button>
            </div>
        </form>
          {error && (
            <div className="mt-6 bg-accent-100 border border-accent-400 text-accent-700 px-4 py-3 rounded-lg relative flex items-center animate-slide-up" role="alert">
              <AlertTriangle className="w-5 h-5 mr-2 flex-shrink-0" />
              <span className="font-medium">{error}</span>
            </div>
          )}
      </div>

        <div className="card-elevated">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
            <h3 className="text-3xl font-bold text-secondary-900 flex items-center">
              <Wallet className="w-8 h-8 mr-3 text-primary-500" />
              Your Expenses
            </h3>
            <div className="bg-gradient-to-r from-primary-500 to-primary-600 text-white px-6 py-3 rounded-2xl shadow-soft">
              <div className="text-sm font-medium opacity-90">Total Spent</div>
              <div className="text-2xl font-bold">₹{totalAmount.toFixed(2)}</div>
            </div>
          </div>
          {loading ? ( 
            <div className="text-center py-12">
              <div className="loading-spinner h-8 w-8 mx-auto mb-4"></div>
              <p className="text-secondary-600 font-medium">Loading expenses...</p>
            </div> 
          ) : expenses.length === 0 ? (
            <div className="text-center py-16">
              <div className="p-6 bg-secondary-100 rounded-full w-24 h-24 mx-auto mb-6 flex items-center justify-center">
                <Wallet className="w-12 h-12 text-secondary-500" />
              </div>
              <h4 className="text-2xl font-bold text-secondary-900 mb-2">No expenses yet</h4>
              <p className="text-secondary-600 mb-6">Start tracking your travel expenses by adding your first entry above!</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="bg-secondary-50 border-b border-secondary-200">
                    <th className="p-4 font-bold text-secondary-900 text-left">Amount</th>
                    <th className="p-4 font-bold text-secondary-900 text-left">Category</th>
                    <th className="p-4 font-bold text-secondary-900 text-left hidden md:table-cell">Description</th>
                    <th className="p-4 font-bold text-secondary-900 text-left hidden md:table-cell">Date</th>
                    <th className="p-4 font-bold text-secondary-900 text-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {expenses.map((exp, idx) => (
                    <tr 
                      key={exp.id} 
                      className="border-b border-secondary-200 hover:bg-secondary-50 transition-colors animate-slide-up"
                      style={{animationDelay: `${idx * 0.1}s`}}
                    >
                      <td className="p-4 font-bold text-secondary-900">₹{exp.amount.toFixed(2)}</td>
                      <td className="p-4 text-secondary-700">
                        <span className="bg-primary-100 text-primary-700 px-3 py-1 rounded-full text-sm font-medium">
                          {exp.category}
                        </span>
                      </td>
                      <td className="p-4 text-secondary-600 hidden md:table-cell">{exp.description}</td>
                      <td className="p-4 text-secondary-600 hidden md:table-cell font-medium">
                        {new Date(exp.date).toLocaleDateString()}
                      </td>
                      <td className="p-4 text-right">
                        <button 
                          onClick={() => handleDeleteExpense(exp.id)} 
                          className="text-accent-500 hover:text-accent-700 hover:bg-accent-100 p-2 rounded-lg transition-all duration-200"
                          title="Delete expense"
                        >
                          <Trash2 size={18} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </main>
  );
};

export default Expenses;
