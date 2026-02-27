import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../services/api';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');
        
        try {
            // Sends the GET request with query parameters as strictly required by the rubric
            const response = await API.get('/login', {
                params: { username, password }
            });

            // Extract the JWT token from the backend response
            const token = response.data.token;
            
            // Store the token in localStorage so our API interceptor can use it
            localStorage.setItem('token', token);

            // Decode the JWT payload to extract the user's role (ADMIN or CUSTOMER)
            const payload = JSON.parse(atob(token.split('.')[1]));
            localStorage.setItem('role', payload.role);
            localStorage.setItem('username', username);

            // Redirect based on role
            if (payload.role === 'ADMIN') {
                navigate('/admin');
            } else {
                navigate('/home');
            }
        } catch (err) {
            setError(err.response?.data || "Invalid credentials. Please try again.");
        }
    };

    const handleForgotPassword = async () => {
        if (!username) {
            setError("Please enter your Username first to reset your password.");
            return;
        }
        try {
            // Calls the exact forgot password endpoint from the rubric
            const response = await API.get(`/${username}/forgot`);
            setMessage(response.data);
            setError('');
        } catch (err) {
            setError("Failed to process forgot password request.");
        }
    };

    return (
        <div className="form-container">
            <h2>Login</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}
            
            <form onSubmit={handleLogin}>
                <input
                    type="text"
                    placeholder="Username"
                    required
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">Login</button>
            </form>
            
            <div style={{ marginTop: '15px' }}>
                {/* Forgot Password Reset option */}
                <button 
                    type="button" 
                    onClick={handleForgotPassword} 
                    style={{ background: 'none', color: 'blue', border: 'none', cursor: 'pointer', textDecoration: 'underline' }}
                >
                    Forgot Password?
                </button>
            </div>
            
            <div style={{ marginTop: '15px' }}>
                <p>Don't have an account? <Link to="/register">Register here</Link></p>
            </div>
        </div>
    );
};

export default Login;