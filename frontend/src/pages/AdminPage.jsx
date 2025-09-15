import React, { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import './AdminPage.css';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [statistics, setStatistics] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [activeTab, setActiveTab] = useState('users');

  useEffect(() => {
    loadAdminData();
  }, []);

  const loadAdminData = async () => {
    setLoading(true);
    setError('');
    
    try {
      const [usersData, bookingsData, statsData] = await Promise.all([
        adminAPI.getAllUsers(),
        adminAPI.getAllBookings(),
        adminAPI.getSystemStatistics()
      ]);
      
      setUsers(usersData);
      setBookings(bookingsData);
      setStatistics(statsData);
    } catch (err) {
      setError('Failed to load admin data: ' + err.message);
      console.error('Admin data loading error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    if (!window.confirm(`Are you sure you want to change this user's role to ${newRole}?`)) {
      return;
    }

    try {
      setError('');
      setSuccess('');
      
      await adminAPI.updateUserRole(userId, newRole);
      setSuccess('User role updated successfully');
      
      // Refresh users data
      const usersData = await adminAPI.getAllUsers();
      setUsers(usersData);
      
    } catch (err) {
      setError('Failed to update user role: ' + err.message);
      console.error('Role update error:', err);
    }
  };

  const handleDeleteBooking = async (bookingId) => {
    if (!window.confirm('Are you sure you want to delete this booking? This action cannot be undone.')) {
      return;
    }

    try {
      setError('');
      setSuccess('');
      
      await adminAPI.deleteBooking(bookingId);
      setSuccess('Booking deleted successfully');
      
      // Refresh bookings data
      const bookingsData = await adminAPI.getAllBookings();
      setBookings(bookingsData);
      
      // Refresh statistics
      const statsData = await adminAPI.getSystemStatistics();
      setStatistics(statsData);
      
    } catch (err) {
      setError('Failed to delete booking: ' + err.message);
      console.error('Booking deletion error:', err);
    }
  };

  const clearMessages = () => {
    setError('');
    setSuccess('');
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatCurrency = (amount, currency = 'USD') => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(amount);
  };

  const renderSystemStats = () => (
    <div className="admin-stats">
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Users</h3>
          <div className="stat-value">{statistics.totalUsers || 0}</div>
        </div>
        <div className="stat-card">
          <h3>Admin Users</h3>
          <div className="stat-value">{statistics.adminUsers || 0}</div>
        </div>
        <div className="stat-card">
          <h3>Regular Users</h3>
          <div className="stat-value">{statistics.regularUsers || 0}</div>
        </div>
        <div className="stat-card">
          <h3>Total Bookings</h3>
          <div className="stat-value">{statistics.totalBookings || 0}</div>
        </div>
        <div className="stat-card">
          <h3>Total Itineraries</h3>
          <div className="stat-value">{statistics.totalItineraries || 0}</div>
        </div>
      </div>
    </div>
  );

  const renderUsersTable = () => (
    <div className="admin-table-container">
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Role</th>
            <th>Itineraries</th>
            <th>Bookings</th>
            <th>Last Activity</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.fullName}</td>
              <td>{user.email}</td>
              <td>{user.phone}</td>
              <td>
                <span className={`role-badge ${user.role.toLowerCase()}`}>
                  {user.role}
                </span>
              </td>
              <td>{user.totalItineraries}</td>
              <td>{user.totalBookings}</td>
              <td>{formatDate(user.lastActivity)}</td>
              <td>
                <span className={`status-badge ${user.isActive ? 'active' : 'inactive'}`}>
                  {user.isActive ? 'Active' : 'Inactive'}
                </span>
              </td>
              <td>
                <select
                  value={user.role}
                  onChange={(e) => handleRoleChange(user.id, e.target.value)}
                  className="role-select"
                >
                  <option value="USER">User</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  const renderBookingsTable = () => (
    <div className="admin-table-container">
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Reference</th>
            <th>User</th>
            <th>Type</th>
            <th>Provider</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Booking Date</th>
            <th>Confirmed</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {bookings.map(booking => (
            <tr key={booking.id}>
              <td>{booking.id}</td>
              <td>{booking.bookingReference}</td>
              <td>
                <div>
                  <div>{booking.user.fullName}</div>
                  <div className="user-email">{booking.user.email}</div>
                </div>
              </td>
              <td>
                <span className={`type-badge ${booking.bookingType.toLowerCase()}`}>
                  {booking.bookingType}
                </span>
              </td>
              <td>{booking.provider || 'N/A'}</td>
              <td>{formatCurrency(booking.totalAmount, booking.currency)}</td>
              <td>
                <span className={`status-badge ${booking.status.toLowerCase()}`}>
                  {booking.status}
                </span>
              </td>
              <td>{formatDate(booking.bookingDate)}</td>
              <td>{formatDate(booking.confirmationDate)}</td>
              <td>
                {booking.status !== 'CONFIRMED' && booking.status !== 'COMPLETED' && (
                  <button
                    className="delete-btn"
                    onClick={() => handleDeleteBooking(booking.id)}
                    title="Delete booking"
                  >
                    Delete
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

  if (loading) {
    return (
      <div className="admin-page">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading admin data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <div className="admin-header">
        <h1>Admin Dashboard</h1>
        <button className="refresh-btn" onClick={loadAdminData}>
          Refresh Data
        </button>
      </div>

      {/* Messages */}
      {error && (
        <div className="alert alert-error">
          {error}
          <button className="close-btn" onClick={clearMessages}>×</button>
        </div>
      )}
      
      {success && (
        <div className="alert alert-success">
          {success}
          <button className="close-btn" onClick={clearMessages}>×</button>
        </div>
      )}

      {/* System Statistics */}
      {renderSystemStats()}

      {/* Tab Navigation */}
      <div className="admin-tabs">
        <button
          className={`tab-button ${activeTab === 'users' ? 'active' : ''}`}
          onClick={() => setActiveTab('users')}
        >
          Users ({users.length})
        </button>
        <button
          className={`tab-button ${activeTab === 'bookings' ? 'active' : ''}`}
          onClick={() => setActiveTab('bookings')}
        >
          Bookings ({bookings.length})
        </button>
      </div>

      {/* Tab Content */}
      <div className="admin-content">
        {activeTab === 'users' && renderUsersTable()}
        {activeTab === 'bookings' && renderBookingsTable()}
      </div>
    </div>
  );
};

export default AdminPage;