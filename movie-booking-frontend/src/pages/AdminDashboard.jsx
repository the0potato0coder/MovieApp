import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const AdminDashboard = () => {
    const [movies, setMovies] = useState([]);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [newMovie, setNewMovie] = useState({
        movieName: '',
        theatreName: '',
        totalTicketsAllotted: 0,
        ticketStatus: 'AVAILABLE'
    });
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
            // Use the new backend endpoint for direct status update
            await API.put(`/admin/status/${movieName}/${theatreName}?status=${encodeURIComponent(newStatus)}`);
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

    const handleAddMovie = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        // Build payload with nested id object for composite key
        const payload = {
            id: {
                movieName: newMovie.movieName,
                theatreName: newMovie.theatreName
            },
            totalTicketsAlloted: Number(newMovie.totalTicketsAllotted),
            ticketStatus: newMovie.ticketStatus
        };
        try {
            await API.post('/admin/add', payload);
            setSuccessMessage('Movie added successfully.');
            setNewMovie({ movieName: '', theatreName: '', totalTicketsAllotted: 0, ticketStatus: 'AVAILABLE' });
            fetchMovies();
        } catch (err) {
            setError(err.response?.data || 'Failed to add movie.');
        }
    };

    return (
        <div className="admin-container" style={{ padding: '20px' }}>
            <header style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '2px solid #333', paddingBottom: '10px', marginBottom: '20px' }}>
                <h2 style={{ color: '#d9534f' }}>Admin Dashboard</h2>
                <button onClick={handleLogout} style={{ padding: '8px 16px', cursor: 'pointer' }}>Logout</button>
            </header>

            {error && <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>}
            {successMessage && <p style={{ color: 'green', fontWeight: 'bold' }}>{successMessage}</p>}

            {/* Add Movie Form */}
            <form onSubmit={handleAddMovie} style={{ marginBottom: '2rem', border: '1px solid #ccc', padding: '1rem' }}>
                <h3>Add New Movie</h3>
                <input type="text" placeholder="Movie Name" required value={newMovie.movieName} onChange={e => setNewMovie({ ...newMovie, movieName: e.target.value })} />
                <input type="text" placeholder="Theatre Name" required value={newMovie.theatreName} onChange={e => setNewMovie({ ...newMovie, theatreName: e.target.value })} />
                <input type="number" placeholder="Total Tickets" required min="1" value={newMovie.totalTicketsAllotted} onChange={e => setNewMovie({ ...newMovie, totalTicketsAllotted: Number(e.target.value) })} />
                <button type="submit">Add Movie</button>
            </form>

            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
                <thead>
                    <tr style={{ backgroundColor: '#f4f4f4', textAlign: 'left' }}>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Movie Name</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Theatre</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Total Tickets</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Tickets Available</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Current Status</th>
                        <th style={{ border: '1px solid #ddd', padding: '10px' }}>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {movies.map((movie, idx) => {
                        // Calculate tickets available if not present
                        const available = (movie.totalTicketsAllotted || 0) - (movie.ticketsBooked || 0);
                        return (
                            <tr key={idx}>
                                <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.movieName}</td>
                                <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.theatreName}</td>
                                <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.totalTicketsAllotted}</td>
                                <td style={{ border: '1px solid #ddd', padding: '10px' }}>{available}</td>
                                <td style={{ border: '1px solid #ddd', padding: '10px' }}>{movie.ticketStatus}</td>
                                <td style={{ border: '1px solid #ddd', padding: '10px' }}>
                                    <button onClick={() => handleUpdateStatus(movie.movieName, movie.theatreName, 'SOLD OUT')} style={{ marginRight: 8 }}>Mark Sold Out</button>
                                    <button onClick={() => handleUpdateStatus(movie.movieName, movie.theatreName, 'BOOK ASAP')} style={{ marginRight: 8 }}>Mark Book ASAP</button>
                                    <button onClick={() => handleUpdateStatus(movie.movieName, movie.theatreName, 'AVAILABLE')} style={{ marginRight: 8 }}>Mark Available</button>
                                    <button onClick={() => handleDeleteMovie(movie.movieName, movie.theatreName)} style={{ color: 'red' }}>Delete</button>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboard;