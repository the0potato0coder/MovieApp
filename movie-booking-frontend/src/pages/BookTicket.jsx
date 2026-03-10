import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/api';

const ROWS = ['A', 'B', 'C', 'D', 'E'];
const COLS = Array.from({ length: 10 }, (_, i) => i + 1);

const BookTicket = () => {
    const { movieName, theatreName } = useParams();
    const navigate = useNavigate();

    const [numberOfTickets, setNumberOfTickets] = useState(1);
    const [error, setError] = useState('');
    const [selectedSeats, setSelectedSeats] = useState([]);
    const [allSeats, setAllSeats] = useState([]);
    const [bookedSeats, setBookedSeats] = useState([]);

    useEffect(() => {
        const fetchSeats = async () => {
            try {
                const res = await API.get(`/seats/booked?movieName=${encodeURIComponent(movieName)}&theatreName=${encodeURIComponent(theatreName)}`);
                const seats = [];
                ROWS.forEach(row => COLS.forEach(col => seats.push(`${row}${col}`)));
                setAllSeats(seats);
                setBookedSeats(res.data || []);
            } catch {
                setAllSeats([]);
                setBookedSeats([]);
            }
        };
        fetchSeats();
    }, [movieName, theatreName]);

    const handleSeatSelect = (seat) => {
        if (bookedSeats.includes(seat)) return;
        if (selectedSeats.length > 0 && selectedSeats[0] === seat) {
            setSelectedSeats([]);
            setError('');
            return;
        }
        const row = seat[0];
        const seatNum = parseInt(seat.slice(1), 10);
        const seatsToSelect = [];
        let seatsNeeded = Number(numberOfTickets);
        let rowIdx = ROWS.indexOf(row);
        let colIdx = seatNum;
        while (seatsNeeded > 0 && rowIdx < ROWS.length) {
            for (let i = colIdx; i <= 10 && seatsNeeded > 0; i++) {
                const candidate = `${ROWS[rowIdx]}${i}`;
                if (allSeats.includes(candidate) && !bookedSeats.includes(candidate)) {
                    seatsToSelect.push(candidate);
                    seatsNeeded--;
                } else break;
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

    const getSeatClass = (seat) => {
        if (bookedSeats.includes(seat)) return 'seat seat-booked';
        if (selectedSeats.includes(seat)) return 'seat seat-selected';
        return 'seat seat-available';
    };

    const handleBookTicket = async (e) => {
        e.preventDefault();
        setError('');
        if (selectedSeats.length !== Number(numberOfTickets)) {
            setError(`Please select exactly ${numberOfTickets} seat(s).`);
            return;
        }
        const username = localStorage.getItem('username');
        try {
            await API.post(`${movieName}/add`, {
                movieName, theatreName,
                numberOfTickets: Number(numberOfTickets),
                seatNumbers: selectedSeats, username
            });
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
        <div className="max-w-2xl mx-auto px-4 py-6">
            {/* Header */}
            <header className="flex justify-between items-center bg-white rounded-xl shadow-sm px-6 py-4 mb-6">
                <h1 className="text-2xl font-bold text-gray-800">🎬 Book Tickets</h1>
                <button onClick={() => navigate('/home')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                    ← Back
                </button>
            </header>

            {/* Movie Info */}
            <div className="flex gap-6 bg-white rounded-xl shadow-sm px-6 py-4 mb-6">
                <div>
                    <p className="text-xs text-gray-400 uppercase tracking-wide">Movie</p>
                    <p className="font-semibold text-gray-800">{decodeURIComponent(movieName)}</p>
                </div>
                <div>
                    <p className="text-xs text-gray-400 uppercase tracking-wide">Theatre</p>
                    <p className="font-semibold text-gray-800">{decodeURIComponent(theatreName)}</p>
                </div>
            </div>

            {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-4">{error}</p>}

            <form onSubmit={handleBookTicket} className="bg-white rounded-xl shadow-sm p-6 space-y-6">
                {/* Ticket Count */}
                <div className="flex items-center gap-4">
                    <label htmlFor="numTickets" className="text-sm font-medium text-gray-700">Tickets:</label>
                    <input
                        id="numTickets"
                        type="number"
                        min="1"
                        max="10"
                        value={numberOfTickets}
                        onChange={(e) => { setNumberOfTickets(e.target.value); setSelectedSeats([]); }}
                        required
                        className="w-20 px-3 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition text-center"
                    />
                </div>

                {/* Legend */}
                <div className="flex gap-6 justify-center bg-gray-50 rounded-lg py-3">
                    <div className="flex items-center gap-2 text-xs text-gray-600">
                        <span className="w-5 h-5 rounded-md bg-emerald-100 border border-emerald-300"></span> Available
                    </div>
                    <div className="flex items-center gap-2 text-xs text-gray-600">
                        <span className="w-5 h-5 rounded-md bg-gray-200 border border-gray-300"></span> Booked
                    </div>
                    <div className="flex items-center gap-2 text-xs text-gray-600">
                        <span className="w-5 h-5 rounded-md bg-blue-600 border border-blue-700"></span> Selected
                    </div>
                </div>

                {/* Screen */}
                <div className="text-center py-2 bg-gradient-to-r from-transparent via-gray-300 to-transparent text-gray-400 text-[11px] tracking-[4px] uppercase rounded">
                    SCREEN
                </div>

                {/* Seat Map */}
                <div className="flex flex-col items-center gap-1.5">
                    {ROWS.map(row => (
                        <div key={row} className="flex items-center gap-1.5">
                            <span className="w-6 text-center text-sm font-bold text-gray-400">{row}</span>
                            {COLS.map(col => {
                                const seat = `${row}${col}`;
                                return (
                                    <button
                                        key={seat}
                                        type="button"
                                        className={getSeatClass(seat)}
                                        onClick={() => handleSeatSelect(seat)}
                                        disabled={bookedSeats.includes(seat)}
                                    >
                                        {col}
                                    </button>
                                );
                            })}
                        </div>
                    ))}
                </div>

                {/* Selection Summary */}
                {selectedSeats.length > 0 && (
                    <div className="text-center bg-blue-50 text-blue-700 text-sm font-medium rounded-lg py-3">
                        Selected: <span className="font-bold">{selectedSeats.join(', ')}</span>
                    </div>
                )}

                {/* Actions */}
                <div className="flex gap-3">
                    <button type="submit" className="flex-1 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition cursor-pointer">
                        Confirm Booking
                    </button>
                    <button type="button" onClick={() => navigate('/home')} className="flex-1 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold rounded-lg transition cursor-pointer">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default BookTicket;