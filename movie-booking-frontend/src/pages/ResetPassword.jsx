import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import API from '../services/api';

const ResetPassword = () => {
    const [token, setToken] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // Get token from query param if present
        const params = new URLSearchParams(location.search);
        const t = params.get('token');
        if (t) setToken(t);
    }, [location]);

    const handleResetPassword = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');
        if (!token || !newPassword) {
            setError('Please enter both the reset token and new password.');
            return;
        }
        try {
            const response = await API.post(`/reset-password?token=${encodeURIComponent(token)}&newPassword=${encodeURIComponent(newPassword)}`);
            setMessage(response.data);
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setError('Failed to reset password. Token may be invalid or expired.');
        }
    };

    return (
        <div className="form-container">
            <h2>Reset Password</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}
            <form onSubmit={handleResetPassword}>
                <input
                    type="text"
                    placeholder="Reset Token"
                    value={token}
                    onChange={(e) => setToken(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="New Password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                />
                <button type="submit">Reset Password</button>
            </form>
        </div>
    );
};

export default ResetPassword;
