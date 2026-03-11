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
        } catch {
            setError('Failed to fetch movies.');
        }
    };

    const handleDeleteMovie = async (movieName, theatreName) => {
        if (!window.confirm(`Are you sure you want to delete ${movieName}?`)) return;
        try {
            await API.delete(`/${movieName}/delete/${theatreName}`);
            setSuccessMessage(`${movieName} deleted successfully.`);
            fetchMovies();
        } catch (err) {
            setError(err.response?.data || 'Failed to delete movie.');
        }
    };

    const handleUpdateStatus = async (movieName, theatreName, newStatus) => {
        try {
            await API.put(`/admin/status/${movieName}/${theatreName}?status=${encodeURIComponent(newStatus)}`);
            setSuccessMessage(`${movieName} status updated to ${newStatus}.`);
            fetchMovies();
        } catch (err) {
            setError(err.response?.data || 'Failed to update status.');
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

    const inputClass = 'w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition';

    const statusPill = (status) => {
        const map = {
            'SOLD OUT': 'bg-red-100 text-red-700',
            'BOOK ASAP': 'bg-amber-100 text-amber-700',
            'AVAILABLE': 'bg-green-100 text-green-700',
        };
        return `px-3 py-1 rounded-full text-xs font-semibold ${map[status] || 'bg-gray-100 text-gray-700'}`;
    };

    return (
        <div className="max-w-5xl mx-auto px-4 py-6">
            <header className="flex justify-between items-center bg-white rounded-xl shadow-sm px-6 py-4 mb-6">
                <h1 className="text-2xl font-bold text-gray-800">🎬 Admin Dashboard</h1>
                <div className="flex gap-3">
                    <button onClick={() => navigate('/home')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">Home</button>
                    <button onClick={handleLogout} className="px-4 py-2 text-sm bg-red-500 hover:bg-red-600 text-white rounded-lg transition cursor-pointer">Logout</button>
                </div>
            </header>

            {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-4">{error}</p>}
            {successMessage && <p className="text-green-600 text-sm font-medium text-center bg-green-50 rounded-lg p-3 mb-4">{successMessage}</p>}

            <div className="bg-white rounded-xl shadow-sm p-6 mb-8 max-w-xl">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">Add New Movie</h2>
                <form onSubmit={handleAddMovie} className="space-y-4">
                    <input type="text" placeholder="Movie Name" required value={newMovie.movieName} onChange={e => setNewMovie({ ...newMovie, movieName: e.target.value })} className={inputClass} />
                    <input type="text" placeholder="Theatre Name" required value={newMovie.theatreName} onChange={e => setNewMovie({ ...newMovie, theatreName: e.target.value })} className={inputClass} />
                    <input type="number" placeholder="Total Tickets" required min="1" value={newMovie.totalTicketsAllotted} onChange={e => setNewMovie({ ...newMovie, totalTicketsAllotted: Number(e.target.value) })} className={inputClass} />
                    <button type="submit" className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition cursor-pointer">Add Movie</button>
                </form>
            </div>

            <div className="bg-white rounded-xl shadow-sm overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="bg-gray-50 text-gray-500 uppercase text-xs tracking-wider">
                            <tr>
                                <th className="px-4 py-3">Movie</th>
                                <th className="px-4 py-3">Theatre</th>
                                <th className="px-4 py-3 text-center">Available</th>
                                <th className="px-4 py-3 text-center">Status</th>
                                <th className="px-4 py-3 text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {movies.map((movie, idx) => {
                                const movieName = movie.id?.movieName || movie.movieName;
                                const theatreName = movie.id?.theatreName || movie.theatreName;
                                const available = movie.ticketStatus === 'SOLD OUT' ? 0 : (movie.availableTickets ?? '—');
                                return (
                                    <tr key={idx} className="hover:bg-gray-50 transition">
                                        <td className="px-4 py-3 font-medium text-gray-800">{movieName}</td>
                                        <td className="px-4 py-3 text-gray-600">{theatreName}</td>
                                        <td className="px-4 py-3 text-center font-semibold text-gray-800">{available}</td>
                                        <td className="px-4 py-3 text-center">
                                            <span className={statusPill(movie.ticketStatus)}>{movie.ticketStatus}</span>
                                        </td>
                                        <td className="px-4 py-3">
                                            <div className="flex flex-wrap gap-2 justify-center">
                                                <button onClick={() => handleUpdateStatus(movieName, theatreName, 'SOLD OUT')} className="px-3 py-1 text-xs bg-red-100 hover:bg-red-200 text-red-700 rounded-lg transition cursor-pointer">Sold Out</button>
                                                <button onClick={() => handleUpdateStatus(movieName, theatreName, 'BOOK ASAP')} className="px-3 py-1 text-xs bg-amber-100 hover:bg-amber-200 text-amber-700 rounded-lg transition cursor-pointer">Book ASAP</button>
                                                <button onClick={() => handleUpdateStatus(movieName, theatreName, 'AVAILABLE')} className="px-3 py-1 text-xs bg-green-100 hover:bg-green-200 text-green-700 rounded-lg transition cursor-pointer">Available</button>
                                                <button onClick={() => handleDeleteMovie(movieName, theatreName)} className="px-3 py-1 text-xs bg-gray-800 hover:bg-gray-900 text-white rounded-lg transition cursor-pointer">Delete</button>
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
                {movies.length === 0 && (
                    <div className="text-center py-12 text-gray-400">
                        <p className="text-4xl mb-2">🎥</p>
                        <p>No movies yet. Add one above!</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminDashboard;