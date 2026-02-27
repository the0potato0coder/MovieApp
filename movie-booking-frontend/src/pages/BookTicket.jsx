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

    const handleBookTicket = async (e) => {
        e.preventDefault();
        setError('');

        // Convert the comma-separated string into an array of seats
        // Example: "A1, A2" becomes ["A1", "A2"]
        const seatNumbers = seatsInput.split(',').map(seat => seat.trim()).filter(seat => seat !== '');

        // US_03 Validation: Ensure seat count matches requested ticket count
        if (seatNumbers.length !== Number(numberOfTickets)) {
            setError(`Validation Failed: You requested ${numberOfTickets} tickets, but provided ${seatNumbers.length} seats.`);
            return;
        }

        // Construct the DTO payload matching our Spring Boot backend
        const payload = {
            movieName: movieName,
            theatreName: theatreName,
            numberOfTickets: Number(numberOfTickets),
            seatNumbers: seatNumbers
        };

        try {
            // Call the exact endpoint specified in the rubric
            await API.post(`/${movieName}/add`, payload);
            
            // Expected Outcome: Successful Ticket Booked
            alert('Ticket booked successfully!');
            navigate('/home'); 
        } catch (err) {
            // Catches backend validation errors (e.g., "Only X tickets are available")
            setError(err.response?.data || 'Failed to book ticket. Please try again.');
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
                        onChange={(e) => setNumberOfTickets(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>

                <div>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Seat Numbers (comma separated):</label>
                    <input 
                        type="text" 
                        placeholder="e.g. A1, A2, A3" 
                        value={seatsInput} 
                        onChange={(e) => setSeatsInput(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                    />
                    <small style={{ color: '#666' }}>Please provide exactly {numberOfTickets} seat(s).</small>
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