import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const ForgotPassword = () => {
    const [identifier, setIdentifier] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [token, setToken] = useState('');
    const navigate = useNavigate();

    const handleForgotPassword = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');
        setToken('');
        if (!identifier) {
            setError('Please enter your Username, Email, or Contact Number.');
            return;
        }
        try {
            const response = await API.post(`/forgot-password?identifier=${encodeURIComponent(identifier)}`);
            setMessage(response.data);
            // Try to extract token from response (for demo/testing)
            const match = response.data.match(/([a-f0-9\-]{36})/i);
            if (match) {
                setToken(match[1]);
                setTimeout(() => navigate(`/reset-password?token=${match[1]}`), 2000);
            }
        } catch (err) {
            setError('Failed to process forgot password request.');
        }
    };

    return (
        <div className="form-container">
            <h2>Forgot Password</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}
            <form onSubmit={handleForgotPassword}>
                <input
                    type="text"
                    placeholder="Username, Email, or Contact Number"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    required
                />
                <button type="submit">Request Reset Token</button>
            </form>
            {token && (
                <div style={{ marginTop: '10px' }}>
                    <p>Reset token: <b>{token}</b></p>
                    <p>Redirecting to reset password page...</p>
                </div>
            )}
        </div>
    );
};

export default ForgotPassword;
