import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import AdminDashboard from './pages/AdminDashboard';
import BookTicket from './pages/BookTicket';
import MyTickets from './pages/MyTickets';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import Profile from './pages/Profile';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50 text-gray-800 font-sans">
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/home" element={<Home />} />
          <Route path="/book/:movieName/:theatreName" element={<BookTicket />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/my-tickets" element={<MyTickets />} />
          <Route path="/profile" element={<Profile />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;