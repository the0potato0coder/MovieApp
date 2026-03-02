import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/api';

const BookTicket = () => {
    // Extract the URL parameters passed from the Home page
    const { movieName, theatreName } = useParams();
    const navigate = useNavigate();

    const [numberOfTickets, setNumberOfTickets] = useState(1);
    const [seatsInput, setSeatsInput] = useState('');
    const [error, setError] = useState('');
    const [availableSeats, setAvailableSeats] = useState([]);
    const [selectedSeats, setSelectedSeats] = useState([]);

    // Store all seats and booked seats separately
    const [allSeats, setAllSeats] = useState([]);
    const [bookedSeats, setBookedSeats] = useState([]);

    // Fetch available seats for visual selection
    React.useEffect(() => {
        async function fetchSeats() {
            try {
                const res = await API.get(`/seats/booked?movieName=${encodeURIComponent(movieName)}&theatreName=${encodeURIComponent(theatreName)}`);
                const all = [];
                ['A','B','C','D','E'].forEach(row => {
                    for (let i = 1; i <= 10; i++) {
                        all.push(`${row}${i}`);
                    }
                });
                setAllSeats(all);
                setBookedSeats(res.data || []);
            } catch {
                setAllSeats([]);
                setBookedSeats([]);
            }
        }
        fetchSeats();
    }, [movieName, theatreName]);

    const handleSeatSelect = seat => {
        if (bookedSeats.includes(seat)) return;
        // If the seat is already the first in the current selection, unselect all
        if (selectedSeats.length > 0 && selectedSeats[0] === seat) {
            setSelectedSeats([]);
            setError('');
            return;
        }
        // Find the row and number of the clicked seat
        const rowOrder = ['A','B','C','D','E'];
        const row = seat[0];
        const seatNum = parseInt(seat.slice(1), 10);
        let seatsToSelect = [];
        let seatsNeeded = Number(numberOfTickets);
        let rowIdx = rowOrder.indexOf(row);
        let colIdx = seatNum;
        while (seatsNeeded > 0 && rowIdx < rowOrder.length) {
            for (let i = colIdx; i <= 10 && seatsNeeded > 0; i++) {
                const candidate = `${rowOrder[rowIdx]}${i}`;
                if (allSeats.includes(candidate) && !bookedSeats.includes(candidate)) {
                    seatsToSelect.push(candidate);
                    seatsNeeded--;
                } else {
                    // If a seat is booked, skip to next row
                    break;
                }
            }
            rowIdx++;
            colIdx = 1;
        }
        if (seatsToSelect.length === Number(numberOfTickets)) {
            setSelectedSeats(seatsToSelect);
            setError('');
        } else {
            setError(`Not enough available seats starting from ${seat}.`);
            setSelectedSeats([]);
        }
    };

    const handleBookTicket = async (e) => {
        e.preventDefault();
        setError('');

        if (selectedSeats.length !== Number(numberOfTickets)) {
            setError(`Please select exactly ${numberOfTickets} seat(s).`);
            return;
        }

        const username = localStorage.getItem('username');
        const payload = {
            movieName: movieName,
            theatreName: theatreName,
            numberOfTickets: Number(numberOfTickets),
            seatNumbers: selectedSeats,
            username: username
        };

        try {
            await API.post(`${movieName}/add`, payload);
            alert('Ticket booked successfully!');
            navigate('/home'); 
        } catch (err) {
            setError(
                typeof err.response?.data === 'string'
                    ? err.response.data
                    : err.response?.data?.message || 'Failed to book ticket. Please try again.'
            );
        }
    };

    return (
        <div className="booking-container" style={{ maxWidth: '500px', margin: '0 auto', padding: '20px' }}>
            <h2>Book Tickets</h2>
            <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: '#f9f9f9', borderRadius: '8px' }}>
                <p><strong>Movie:</strong> {movieName}</p>
                <p><strong>Theatre:</strong> {theatreName}</p>
            </div>

            {error && <p style={{ color: 'red', fontWeight: 'bold' }}>{error}</p>}

            <form onSubmit={handleBookTicket} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Number of Tickets:</label>
                    <input 
                        type="number" 
                        min="1" 
                        value={numberOfTickets} 
                        onChange={e => {
                            setNumberOfTickets(e.target.value);
                            setSelectedSeats([]);
                        }} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Select Seats:</label>
                    {/* Render seat grid row by row */}
                    {['A','B','C','D','E'].map(row => (
                        <div key={row} style={{ display: 'flex', gap: '8px', marginBottom: '8px', alignItems: 'center' }}>
                            <span style={{ width: '30px', fontWeight: 'bold', color: '#333' }}>{row}</span>
                            {Array.from({ length: 10 }, (_, i) => `${row}${i+1}`).map(seat => (
                                <button
                                    key={seat}
                                    type="button"
                                    onClick={() => handleSeatSelect(seat)}
                                    disabled={bookedSeats.includes(seat)}
                                    style={{
                                        padding: '8px',
                                        backgroundColor: bookedSeats.includes(seat)
                                            ? '#bbb'
                                            : selectedSeats.includes(seat)
                                                ? '#28a745'
                                                : '#eee',
                                        color: bookedSeats.includes(seat)
                                            ? '#666'
                                            : selectedSeats.includes(seat)
                                                ? 'white'
                                                : '#333',
                                        border: '1px solid #ccc',
                                        borderRadius: '4px',
                                        cursor: bookedSeats.includes(seat)
                                            ? 'not-allowed'
                                            : 'pointer'
                                    }}
                                >
                                    {seat.slice(1)}
                                </button>
                            ))}
                        </div>
                    ))}
                    <small style={{ color: '#666' }}>Please select exactly {numberOfTickets} seat(s).</small>
                </div>

                <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                    <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', flex: 1 }}>
                        Confirm Booking
                    </button>
                    <button type="button" onClick={() => navigate('/home')} style={{ padding: '10px 20px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default BookTicket;