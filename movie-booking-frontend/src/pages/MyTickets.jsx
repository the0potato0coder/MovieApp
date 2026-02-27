import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const MyTickets = () => {
    const [tickets, setTickets] = useState([]);
    const [passwordData, setPasswordData] = useState({ oldPassword: '', newPassword: '', confirmPassword: '' });
    const [ticketError, setTicketError] = useState('');
    const [passwordMessage, setPasswordMessage] = useState({ type: '', text: '' });
    const navigate = useNavigate();
    
    const loginId = localStorage.getItem('loginId');

    useEffect(() => {
        if (!loginId) {
            navigate('/login');
            return;
        }
        fetchMyTickets();
    }, [loginId, navigate]);

    // Fetch Tickets Booked by Logged In User
    const fetchMyTickets = async () => {
        try {
            // Assuming a backend endpoint exists to fetch tickets for this user
            const response = await API.get(`/tickets/user/${loginId}`);
            setTickets(response.data || []);
            setTicketError('');
        } catch (err) {
            setTicketError("Could not load your ticket history.");
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
            await API.put(`/${loginId}/change-password`, {
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
                    <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#eee', textAlign: 'left' }}>
                                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Movie Name</th>
                                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Theatre</th>
                                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Seats</th>
                                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Total Tickets</th>
                            </tr>
                        </thead>
                        <tbody>
                            {tickets.map((ticket, index) => (
                                <tr key={index}>
                                    <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ticket.movieName}</td>
                                    <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ticket.theatreName}</td>
                                    <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ticket.seatNumbers.join(', ')}</td>
                                    <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{ticket.numberOfTickets}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
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