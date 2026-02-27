import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const AdminDashboard = () => {
    const [movies, setMovies] = useState([]);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();

    // Verify Admin Role on load
    useEffect(() => {
        const role = localStorage.getItem('role');
        if (role !== 'ADMIN') {
            alert('Access Denied: Admins only.');
            navigate('/home');
        } else {
            fetchMovies();
        }
    }, [navigate]);

    const fetchMovies = async () => {
        try {
            const response = await API.get('/all');
            setMovies(response.data || []);
            setError('');
        } catch (err) {
            setError("Failed to fetch movies.");
        }
    };

    // US_04: Admin Can Delete the Movie
    const handleDeleteMovie = async (movieName, theatreName) => {
        if (!window.confirm(`Are you sure you want to delete ${movieName}?`)) return;
        
        try {
            // DELETE /api/v1.0/moviebooking/<moviename>/delete/<id>
            await API.delete(`/${movieName}/delete/${theatreName}`);
            setSuccessMessage(`${movieName} deleted successfully.`);
            fetchMovies(); // Refresh the dashboard after deletion
        } catch (err) {
            setError(err.response?.data || "Failed to delete movie.");
        }
    };

    // US_04: Admin Can Manually Mark Movie As SOLD OUT / BOOK ASAP
    const handleUpdateStatus = async (movieName, theatreName, newStatus) => {
        try {
            // PUT /api/v1.0/moviebooking/<moviename>/update/<ticket>
            // We pass the theatreName as the ID identifier based on our composite key backend setup
            await API.put(`/${movieName}/update/${theatreName}?newStatus=${newStatus}`);
            setSuccessMessage(`${movieName} status updated to ${newStatus}.`);
            fetchMovies(); // Refresh Availability Option
        } catch (err) {
            setError(err.response?.data || "Failed to update status.");
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    return (
        <div className="admin-container" style={{ padding: '20px' }}>
            <header style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '2px solid #333', paddingBottom: '10px', marginBottom: '20px' }}>
                <h2 style={{ color: '#d9534f' }}>Admin Dashboard</h2>
                <button onClick={handleLogout} style={{ padding: '8px 16px', cursor: 'pointer' }}>Logout</button>
            </header>

            {error && <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>}
            {successMessage && <p style={{ color: 'green', fontWeight: 'bold' }}>{successMessage}</p>}

            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                <thead>
                    <tr style={{ backgroundColor: '#f4f4f4', textAlign: 'left' }}>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Movie Name</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Theatre</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Total Tickets</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Current Status</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {movies.map((movie, index) => (
                        <tr key={index}>
                            <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.id.movieName}</td>
                            <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.id.theatreName}</td>
                            <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.totalTicketsAllotted}</td>
                            <td style={{ border: '1px solid #ddd', padding: '10px', fontWeight: 'bold', color: movie.ticketStatus === 'SOLD OUT' ? 'red' : 'green' }}>
                                {movie.ticketStatus || 'AVAILABLE'}
                            </td>
                            <td style={{ border: '1px solid #ddd', padding: '10px', display: 'flex', gap: '5px' }}>
                                <button onClick={() => handleUpdateStatus(movie.id.movieName, movie.id.theatreName, 'SOLD OUT')} style={{ backgroundColor: '#ffc107', padding: '5px 10px', border: 'none', cursor: 'pointer' }}>
                                    Mark Sold Out
                                </button>
                                <button onClick={() => handleUpdateStatus(movie.id.movieName, movie.id.theatreName, 'BOOK ASAP')} style={{ backgroundColor: '#17a2b8', color: 'white', padding: '5px 10px', border: 'none', cursor: 'pointer' }}>
                                    Mark Book ASAP
                                </button>
                                <button onClick={() => handleDeleteMovie(movie.id.movieName, movie.id.theatreName)} style={{ backgroundColor: '#dc3545', color: 'white', padding: '5px 10px', border: 'none', cursor: 'pointer' }}>
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboard;