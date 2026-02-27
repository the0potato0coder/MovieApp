import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import AdminDashboard from './pages/AdminDashboard';
import BookTicket from './pages/BookTicket';
import MyTickets from './pages/MyTickets';

function App() {
  return (
    <Router>
      <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
        <Routes>
          {/* Default route redirects to login */}
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          {/* US_02: Home Page After Successful Login */}
          <Route path="/home" element={<Home />} />
          <Route path="/book/:movieName/:theatreName" element={<BookTicket />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/my-tickets" element={<MyTickets />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;