import React, { useState } from 'react';
import API from '../services/api';
import { useNavigate } from 'react-router-dom';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '', lastName: '', email: '', 
        username: '', password: '', confirmPassword: '', 
        contactNumber: '', role: 'USER'
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');

        // US_01: Password and Confirm Password must be same 
        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match!");
            return;
        }

        try {
            // US_01: Register as new user [cite: 108]
            await API.post('/register', formData);
            alert("Successful Registration"); // Expected Outcome [cite: 158]
            navigate('/login');
        } catch (err) {
            // US_01: Validation message must be shown 
            setError(err.response?.data || "Registration failed. Please check your details.");
        }
    };

    return (
        <div className="form-container">
            <h2>Register</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <form onSubmit={handleRegister}>
                <input type="text" placeholder="First Name" required onChange={(e) => setFormData({...formData, firstName: e.target.value})} />
                <input type="text" placeholder="Last Name" required onChange={(e) => setFormData({...formData, lastName: e.target.value})} />
                <input type="email" placeholder="Email" required onChange={(e) => setFormData({...formData, email: e.target.value})} />
                <input type="text" placeholder="Username" required onChange={(e) => setFormData({...formData, username: e.target.value})} />
                <input type="password" placeholder="Password" required onChange={(e) => setFormData({...formData, password: e.target.value})} />
                <input type="password" placeholder="Confirm Password" required onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})} />
                <input type="text" placeholder="Contact Number" required onChange={(e) => setFormData({...formData, contactNumber: e.target.value})} />
                
                <select onChange={(e) => setFormData({...formData, role: e.target.value})}>
                    <option value="USER">User</option>
                    <option value="ADMIN">Admin</option>
                </select>
                
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default Register;