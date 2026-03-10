import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const MyTickets = () => {
    const [tickets, setTickets] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const username = localStorage.getItem('username');

    useEffect(() => {
        if (!username) { navigate('/login'); return; }
        fetchMyTickets();
    }, [username, navigate]);

    const fetchMyTickets = async () => {
        try {
            const response = await API.get(`/my?username=${encodeURIComponent(username)}`);
            setTickets(response.data || []);
            setError('');
        } catch (err) {
            setError(
                typeof err.response?.data === 'string'
                    ? err.response.data
                    : err.response?.data?.message || 'Could not load your ticket history.'
            );
        }
    };

    return (
        <div className="max-w-4xl mx-auto px-4 py-6">
            {/* Header */}
            <header className="flex justify-between items-center bg-white rounded-xl shadow-sm px-6 py-4 mb-8">
                <h1 className="text-2xl font-bold text-gray-800">🎟️ My Tickets</h1>
                <div className="flex gap-2">
                    <button onClick={() => navigate('/home')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                        Home
                    </button>
                    <button onClick={() => navigate('/profile')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                        Profile
                    </button>
                </div>
            </header>

            {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-6">{error}</p>}

            {tickets.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {tickets.map((ticket, index) => (
                        <div key={index} className="bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-md transition-shadow duration-200">
                            {/* Ticket Header */}
                            <div className="bg-gradient-to-r from-slate-700 to-blue-600 text-white px-5 py-4 flex justify-between items-center">
                                <span className="font-bold text-base">{ticket.movie?.id?.movieName || ticket.movieName}</span>
                                <span className="bg-white/20 text-xs px-3 py-1 rounded-full">
                                    {ticket.numberOfTickets} ticket(s)
                                </span>
                            </div>
                            {/* Ticket Body */}
                            <div className="px-5 py-4 space-y-0 divide-y divide-gray-100">
                                <div className="flex justify-between py-2.5 text-sm">
                                    <span className="text-gray-400 font-medium">Theatre</span>
                                    <span className="text-gray-700">{ticket.movie?.id?.theatreName || ticket.theatreName}</span>
                                </div>
                                <div className="flex justify-between items-center py-2.5 text-sm">
                                    <span className="text-gray-400 font-medium">Seats</span>
                                    <div className="flex gap-1 flex-wrap justify-end">
                                        {Array.isArray(ticket.seatNumbers)
                                            ? ticket.seatNumbers.map((s, i) => (
                                                <span key={i} className="bg-blue-50 text-blue-700 px-2 py-0.5 rounded text-xs font-semibold">{s}</span>
                                            ))
                                            : <span className="text-gray-700">{ticket.seatNumbers}</span>}
                                    </div>
                                </div>
                                {ticket.transactionId && (
                                    <div className="flex justify-between py-2.5 text-sm">
                                        <span className="text-gray-400 font-medium">Transaction ID</span>
                                        <span className="font-mono text-xs text-gray-500">{ticket.transactionId}</span>
                                    </div>
                                )}
                                {ticket.showTime && (
                                    <div className="flex justify-between py-2.5 text-sm">
                                        <span className="text-gray-400 font-medium">Show Time</span>
                                        <span className="text-gray-700">{ticket.showTime}</span>
                                    </div>
                                )}
                                {ticket.bookingTime && (
                                    <div className="flex justify-between py-2.5 text-sm">
                                        <span className="text-gray-400 font-medium">Booked On</span>
                                        <span className="text-gray-700">{ticket.bookingTime}</span>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="text-center py-20 text-gray-400">
                    <p className="text-5xl mb-4">🎬</p>
                    <p className="text-lg mb-4">You haven't booked any tickets yet.</p>
                    <button onClick={() => navigate('/home')} className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition cursor-pointer">
                        Browse Movies
                    </button>
                </div>
            )}
        </div>
    );
};

export default MyTickets;