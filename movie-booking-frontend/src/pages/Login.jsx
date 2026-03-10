import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../services/api';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});
    const [touched, setTouched] = useState({});
    const navigate = useNavigate();

    const validate = () => {
        const errors = {};
        if (!username.trim()) errors.username = 'Username is required.';
        else if (username.trim().length < 3) errors.username = 'Username must be at least 3 characters.';
        if (!password) errors.password = 'Password is required.';
        else if (password.length < 6) errors.password = 'Password must be at least 6 characters.';
        return errors;
    };

    const handleBlur = (field) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
        setFieldErrors(validate());
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');
        const errors = validate();
        setFieldErrors(errors);
        setTouched({ username: true, password: true });
        if (Object.keys(errors).length > 0) return;
        try {
            const response = await API.get('/login', { params: { username, password } });
            const token = response.data.token;
            localStorage.setItem('token', token);
            const payload = JSON.parse(atob(token.split('.')[1]));
            localStorage.setItem('role', payload.role);
            localStorage.setItem('username', username);
            navigate(payload.role === 'ADMIN' ? '/admin' : '/home');
        } catch (err) {
            setError(err.response?.data || 'Invalid credentials. Please try again.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center px-4">
            <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-800">🎬 MovieBooking</h1>
                    <p className="text-gray-500 mt-1">Sign in to your account</p>
                </div>

                {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-4">{error}</p>}
                {message && <p className="text-green-600 text-sm font-medium text-center bg-green-50 rounded-lg p-3 mb-4">{message}</p>}

                <form onSubmit={handleLogin} className="space-y-4">
                    <div>
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            onBlur={() => handleBlur('username')}
                            className={`w-full px-4 py-3 rounded-lg border ${touched.username && fieldErrors.username ? 'border-red-400 focus:ring-red-400' : 'border-gray-300 focus:ring-blue-500'} focus:ring-2 focus:border-transparent outline-none transition`}
                        />
                        {touched.username && fieldErrors.username && <p className="text-red-500 text-xs mt-1">{fieldErrors.username}</p>}
                    </div>
                    <div>
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            onBlur={() => handleBlur('password')}
                            className={`w-full px-4 py-3 rounded-lg border ${touched.password && fieldErrors.password ? 'border-red-400 focus:ring-red-400' : 'border-gray-300 focus:ring-blue-500'} focus:ring-2 focus:border-transparent outline-none transition`}
                        />
                        {touched.password && fieldErrors.password && <p className="text-red-500 text-xs mt-1">{fieldErrors.password}</p>}
                    </div>
                    <button
                        type="submit"
                        className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition duration-200 cursor-pointer"
                    >
                        Sign In
                    </button>
                </form>

                <div className="mt-6 text-center text-sm space-y-2">
                    <Link to="/forgot-password" className="text-blue-600 hover:text-blue-800 hover:underline">
                        Forgot Password?
                    </Link>
                    <p className="text-gray-500">
                        Don't have an account?{' '}
                        <Link to="/register" className="text-blue-600 hover:text-blue-800 font-medium hover:underline">
                            Register here
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;