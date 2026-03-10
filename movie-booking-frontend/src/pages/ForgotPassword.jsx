import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
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
            const match = response.data.match(/([a-f0-9\-]{36})/i);
            if (match) {
                setToken(match[1]);
                setTimeout(() => navigate(`/reset-password?token=${match[1]}`), 2000);
            }
        } catch {
            setError('Failed to process forgot password request.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center px-4">
            <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
                <div className="text-center mb-8">
                    <div className="mx-auto w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mb-4">
                        <span className="text-3xl">🔑</span>
                    </div>
                    <h1 className="text-2xl font-bold text-gray-800">Forgot Password</h1>
                    <p className="text-gray-500 mt-1">Enter your details to receive a reset token</p>
                </div>

                {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-4">{error}</p>}
                {message && <p className="text-green-600 text-sm font-medium text-center bg-green-50 rounded-lg p-3 mb-4">{message}</p>}

                <form onSubmit={handleForgotPassword} className="space-y-4">
                    <input
                        type="text"
                        placeholder="Username, Email, or Contact Number"
                        value={identifier}
                        onChange={(e) => setIdentifier(e.target.value)}
                        required
                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                    />
                    <button type="submit" className="w-full py-3 bg-amber-500 hover:bg-amber-600 text-white font-semibold rounded-lg transition duration-200 cursor-pointer">
                        Request Reset Token
                    </button>
                </form>

                {token && (
                    <div className="mt-4 p-4 bg-blue-50 rounded-lg text-center">
                        <p className="text-sm text-gray-600">Reset token: <span className="font-mono font-bold text-blue-700">{token}</span></p>
                        <p className="text-xs text-gray-400 mt-1">Redirecting to reset password page...</p>
                    </div>
                )}

                <p className="text-center text-sm text-gray-500 mt-6">
                    <Link to="/login" className="text-blue-600 hover:text-blue-800 hover:underline">← Back to Login</Link>
                </p>
            </div>
        </div>
    );
};

export default ForgotPassword;
