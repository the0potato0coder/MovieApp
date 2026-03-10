import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import API from '../services/api';

const ResetPassword = () => {
    const [token, setToken] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
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
        } catch {
            setError('Failed to reset password. Token may be invalid or expired.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center px-4">
            <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
                <div className="text-center mb-8">
                    <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
                        <span className="text-3xl">🔒</span>
                    </div>
                    <h1 className="text-2xl font-bold text-gray-800">Reset Password</h1>
                    <p className="text-gray-500 mt-1">Enter your token and new password</p>
                </div>

                {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-4">{error}</p>}
                {message && <p className="text-green-600 text-sm font-medium text-center bg-green-50 rounded-lg p-3 mb-4">{message}</p>}

                <form onSubmit={handleResetPassword} className="space-y-4">
                    <input
                        type="text"
                        placeholder="Reset Token"
                        value={token}
                        onChange={(e) => setToken(e.target.value)}
                        required
                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition font-mono text-sm"
                    />
                    <input
                        type="password"
                        placeholder="New Password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                    />
                    <button type="submit" className="w-full py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition duration-200 cursor-pointer">
                        Reset Password
                    </button>
                </form>

                <p className="text-center text-sm text-gray-500 mt-6">
                    <Link to="/login" className="text-blue-600 hover:text-blue-800 hover:underline">← Back to Login</Link>
                </p>
            </div>
        </div>
    );
};

export default ResetPassword;
