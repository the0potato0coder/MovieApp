import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const Home = () => {
    const [movies, setMovies] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const fetchMovies = async () => {
        try {
            const response = await API.get('/all');
            setMovies(response.data || []);
            setError('');
        } catch {
            setError('Failed to fetch movies.');
        }
    };

    useEffect(() => { fetchMovies(); }, []);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchTerm.trim()) { fetchMovies(); return; }
        try {
            const response = await API.get(`/movies/search/${searchTerm}`);
            setMovies(response.data || []);
            setError('');
        } catch (err) {
            if (err.response?.status === 404) { setMovies([]); setError('No movies found matching your search.'); }
            else setError('Search failed.');
        }
    };

    const handleLogout = () => { localStorage.clear(); navigate('/login'); };

    const statusConfig = {
        'SOLD OUT': { bg: 'bg-red-100', text: 'text-red-700' },
        'BOOK ASAP': { bg: 'bg-amber-100', text: 'text-amber-700' },
    };

    const getStatus = (status) => statusConfig[status] || { bg: 'bg-green-100', text: 'text-green-700' };

    return (
        <div className="max-w-6xl mx-auto px-4 py-6">
            {/* Header */}
            <header className="flex flex-col sm:flex-row justify-between items-center bg-white rounded-xl shadow-sm px-6 py-4 mb-8 gap-4">
                <h1 className="text-2xl font-bold text-gray-800">🎬 MovieBooking</h1>
                <div className="flex gap-2">
                    <button onClick={() => navigate('/my-tickets')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                        My Tickets
                    </button>
                    <button onClick={() => navigate('/profile')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                        Profile
                    </button>
                    <button onClick={handleLogout} className="px-4 py-2 text-sm bg-red-500 hover:bg-red-600 text-white rounded-lg transition cursor-pointer">
                        Logout
                    </button>
                </div>
            </header>

            {/* Search */}
            <form onSubmit={handleSearch} className="flex gap-3 mb-8">
                <input
                    type="text"
                    placeholder="Search by movie name..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="flex-1 max-w-sm px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                />
                <button type="submit" className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition cursor-pointer">
                    Search
                </button>
            </form>

            {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-6">{error}</p>}

            {/* Movie Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {movies.length > 0 ? movies.map((movie, index) => {
                    const status = movie.ticketStatus || 'AVAILABLE';
                    const sc = getStatus(status);
                    const isSoldOut = status === 'SOLD OUT';

                    return (
                        <div key={index} className="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow duration-200 overflow-hidden flex flex-col">
                            {/* Card top accent */}
                            <div className={`h-1.5 ${isSoldOut ? 'bg-red-400' : 'bg-blue-500'}`} />
                            <div className="p-5 flex flex-col flex-1">
                                <h3 className="text-lg font-bold text-gray-800 mb-2">{movie.id.movieName}</h3>
                                <p className="text-sm text-gray-500 mb-1">🎭 {movie.id.theatreName}</p>
                                <p className="text-sm text-gray-500 mb-3">
                                    🎟️ {isSoldOut ? 0 : (movie.availableTickets ?? '—')} tickets available
                                </p>
                                <p className="mb-4">
                                    <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${sc.bg} ${sc.text}`}>
                                        {status}
                                    </span>
                                </p>
                                <button
                                    onClick={() => navigate(`/book/${movie.id.movieName}/${movie.id.theatreName}`)}
                                    disabled={isSoldOut}
                                    className={`mt-auto w-full py-2.5 rounded-lg font-semibold text-sm transition cursor-pointer ${
                                        isSoldOut
                                            ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                                            : 'bg-blue-600 hover:bg-blue-700 text-white'
                                    }`}
                                >
                                    {isSoldOut ? 'Sold Out' : 'Book Ticket →'}
                                </button>
                            </div>
                        </div>
                    );
                }) : (
                    !error && (
                        <div className="col-span-full text-center py-16 text-gray-400">
                            <p className="text-5xl mb-4">🎬</p>
                            <p className="text-lg">No movies available right now.</p>
                        </div>
                    )
                )}
            </div>
        </div>
    );
};

export default Home;