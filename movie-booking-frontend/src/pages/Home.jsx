import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const Home = () => {
    const [movies, setMovies] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Fetch all movies when the component loads
    const fetchMovies = async () => {
        try {
            const response = await API.get('/all');
            setMovies(response.data || []);
            setError('');
        } catch (err) {
            setError("Failed to fetch movies.");
        }
    };

    useEffect(() => {
        fetchMovies();
    }, []);

    // US_02: Search By Movie Name 
    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchTerm.trim()) {
            fetchMovies(); // If search is empty, reload all movies
            return;
        }
        try {
            const response = await API.get(`/movies/search/${searchTerm}`);
            setMovies(response.data || []);
            setError('');
        } catch (err) {
            if (err.response && err.response.status === 404) {
                setMovies([]);
                setError("No movies found matching your search.");
            } else {
                setError("Search failed.");
            }
        }
    };

    // US_01: Can logout from their account 
    const handleLogout = () => {
        localStorage.clear(); // Clear the JWT token and user details
        navigate('/login');
    };

    const handleBookClick = (movieName, theatreName) => {
        // We will build this route next!
        navigate(`/book/${movieName}/${theatreName}`); 
    };

    return (
        <div className="home-container">
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #eee', paddingBottom: '10px' }}>
                <h2>MovieBookingApp Home</h2>
                <div>
                    {/* Add this new button */}
                    <button onClick={() => navigate('/my-tickets')} style={{ padding: '8px 16px', cursor: 'pointer', marginRight: '10px' }}>My Tickets</button>
                    
                    <button onClick={handleLogout} style={{ padding: '8px 16px', cursor: 'pointer' }}>Logout</button>
                </div>
            </header>

            <div className="search-bar" style={{ margin: '20px 0' }}>
                <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px' }}>
                    <input 
                        type="text" 
                        placeholder="Search by Movie Name..." 
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ padding: '8px', width: '300px' }}
                    />
                    <button type="submit" style={{ padding: '8px 16px' }}>Search</button>
                </form>
            </div>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            <div className="movie-list" style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', marginTop: '20px' }}>
                {movies.length > 0 ? movies.map((movie, index) => (
                    <div key={index} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px', width: '250px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
                        <h3 style={{ margin: '0 0 10px 0' }}>{movie.id.movieName}</h3>
                        <p style={{ margin: '5px 0' }}><strong>Theatre:</strong> {movie.id.theatreName}</p>
                        <p style={{ margin: '5px 0' }}><strong>Status:</strong> <span style={{ color: movie.ticketStatus === 'SOLD OUT' ? 'red' : 'green' }}>{movie.ticketStatus || 'AVAILABLE'}</span></p>
                        
                        {/* US_03: Sold Out Option Disables Tickets Booking  */}
                        <button 
                            onClick={() => handleBookClick(movie.id.movieName, movie.id.theatreName)}
                            disabled={movie.ticketStatus === 'SOLD OUT'}
                            style={{
                                marginTop: '15px',
                                width: '100%',
                                padding: '10px',
                                border: 'none',
                                borderRadius: '4px',
                                backgroundColor: movie.ticketStatus === 'SOLD OUT' ? '#ccc' : '#007bff',
                                color: 'white',
                                cursor: movie.ticketStatus === 'SOLD OUT' ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {movie.ticketStatus === 'SOLD OUT' ? 'Sold Out' : 'Book Ticket'}
                        </button>
                    </div>
                )) : (
                    !error && <p>No movies available right now.</p>
                )}
            </div>
        </div>
    );
};

export default Home;