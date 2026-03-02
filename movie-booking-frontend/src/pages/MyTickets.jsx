import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const MyTickets = () => {
    const [tickets, setTickets] = useState([]);
    const [passwordData, setPasswordData] = useState({ oldPassword: '', newPassword: '', confirmPassword: '' });
    const [ticketError, setTicketError] = useState('');
    const [passwordMessage, setPasswordMessage] = useState({ type: '', text: '' });
    const navigate = useNavigate();
    
    // Use username as the identifier
    const username = localStorage.getItem('username');

    useEffect(() => {
        if (!username) {
            navigate('/login');
            return;
        }
        fetchMyTickets();
    }, [username, navigate]);

    // Fetch Tickets Booked by Logged In User
    const fetchMyTickets = async () => {
        try {
            // Updated to match new backend endpoint
            const response = await API.get(`/my?username=${encodeURIComponent(username)}`);
            setTickets(response.data || []);
            setTicketError('');
        } catch (err) {
            setTicketError(
                typeof err.response?.data === 'string'
                    ? err.response.data
                    : err.response?.data?.message || 'Could not load your ticket history.'
            );
        }
    };

    // Change Password Validation Functionality
    const handleChangePassword = async (e) => {
        e.preventDefault();
        setPasswordMessage({ type: '', text: '' });

        // Change Password Validation
        if (passwordData.newPassword !== passwordData.confirmPassword) {
            setPasswordMessage({ type: 'error', text: 'New Passwords do not match!' });
            return;
        }

        try {
            // Assuming a backend endpoint exists to update the password
            await API.put(`/${username}/change-password`, {
                oldPassword: passwordData.oldPassword,
                newPassword: passwordData.newPassword
            });
            setPasswordMessage({ type: 'success', text: 'Password successfully updated!' });
            setPasswordData({ oldPassword: '', newPassword: '', confirmPassword: '' });
        } catch (err) {
            setPasswordMessage({ type: 'error', text: err.response?.data || 'Failed to change password.' });
        }
    };

    return (
        <div className="profile-container" style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
            <h2>My Profile & Tickets</h2>
            
            <div style={{ display: 'flex', gap: '20px', marginBottom: '30px' }}>
                <button onClick={() => navigate('/home')} style={{ padding: '8px 16px', cursor: 'pointer' }}>Back to Movies</button>
            </div>

            {/* Section: View Tickets Booked */}
            <section style={{ marginBottom: '40px', padding: '20px', backgroundColor: '#f9f9f9', borderRadius: '8px' }}>
                <h3>My Booked Tickets</h3>
                {ticketError && <p style={{ color: 'red' }}>{ticketError}</p>}
                {tickets.length > 0 ? (
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                        {tickets.map((ticket, index) => (
                            <div key={index} style={{ minWidth: '280px', maxWidth: '350px', background: '#fff', borderRadius: '10px', boxShadow: '0 2px 8px #ccc', padding: '18px', marginBottom: '10px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                <div style={{ fontWeight: 'bold', fontSize: '1.1em', marginBottom: '6px' }}>{ticket.movie?.id?.movieName || ticket.movieName}</div>
                                <div><strong>Theatre:</strong> {ticket.movie?.id?.theatreName || ticket.theatreName}</div>
                                <div><strong>Seats:</strong> {Array.isArray(ticket.seatNumbers) ? ticket.seatNumbers.join(', ') : ticket.seatNumbers}</div>
                                <div><strong>Total Tickets:</strong> {ticket.numberOfTickets}</div>
                                {ticket.transactionId && <div><strong>Transaction ID:</strong> {ticket.transactionId}</div>}
                                {ticket.showTime && <div><strong>Show Time:</strong> {ticket.showTime}</div>}
                                {ticket.bookingTime && <div><strong>Booking Time:</strong> {ticket.bookingTime}</div>}
                            </div>
                        ))}
                    </div>
                ) : (
                    <p>You have not booked any tickets yet.</p>
                )}
            </section>

            {/* Section: Change Password */}
            <section style={{ padding: '20px', backgroundColor: '#f9f9f9', borderRadius: '8px' }}>
                <h3>Change Password</h3>
                {passwordMessage.text && (
                    <p style={{ color: passwordMessage.type === 'error' ? 'red' : 'green', fontWeight: 'bold' }}>
                        {passwordMessage.text}
                    </p>
                )}
                <form onSubmit={handleChangePassword} style={{ display: 'flex', flexDirection: 'column', gap: '15px', maxWidth: '400px' }}>
                    <input 
                        type="password" 
                        placeholder="Current Password" 
                        value={passwordData.oldPassword} 
                        onChange={(e) => setPasswordData({...passwordData, oldPassword: e.target.value})} 
                        required 
                        style={{ padding: '8px' }}
                    />
                    <input 
                        type="password" 
                        placeholder="New Password" 
                        value={passwordData.newPassword} 
                        onChange={(e) => setPasswordData({...passwordData, newPassword: e.target.value})} 
                        required 
                        style={{ padding: '8px' }}
                    />
                    <input 
                        type="password" 
                        placeholder="Confirm New Password" 
                        value={passwordData.confirmPassword} 
                        onChange={(e) => setPasswordData({...passwordData, confirmPassword: e.target.value})} 
                        required 
                        style={{ padding: '8px' }}
                    />
                    <button type="submit" style={{ padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Update Password
                    </button>
                </form>
            </section>
        </div>
    );
};

export default MyTickets;