import React, { useState } from 'react';
import API from '../services/api';
import { useNavigate, Link } from 'react-router-dom';

const inputClass = 'w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '', lastName: '', email: '',
        username: '', password: '', confirmPassword: '',
        contactNumber: '', role: 'USER'
    });
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});
    const [touched, setTouched] = useState({});
    const navigate = useNavigate();

    const validate = (data = formData) => {
        const errors = {};
        if (!data.firstName.trim()) errors.firstName = 'First name is required.';
        if (!data.lastName.trim()) errors.lastName = 'Last name is required.';
        if (!data.email.trim()) errors.email = 'Email is required.';
        else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email)) errors.email = 'Enter a valid email address.';
        if (!data.username.trim()) errors.username = 'Username is required.';
        else if (data.username.trim().length < 3) errors.username = 'Username must be at least 3 characters.';
        if (!data.password) errors.password = 'Password is required.';
        else if (data.password.length < 6) errors.password = 'Password must be at least 6 characters.';
        if (!data.confirmPassword) errors.confirmPassword = 'Please confirm your password.';
        else if (data.password !== data.confirmPassword) errors.confirmPassword = 'Passwords do not match.';
        if (!data.contactNumber.trim()) errors.contactNumber = 'Contact number is required.';
        else if (!/^\d{10}$/.test(data.contactNumber.trim())) errors.contactNumber = 'Enter a valid 10-digit phone number.';
        return errors;
    };

    const handleChange = (field, value) => {
        const updated = { ...formData, [field]: value };
        setFormData(updated);
        if (touched[field]) setFieldErrors(validate(updated));
    };

    const handleBlur = (field) => {
        setTouched((prev) => ({ ...prev, [field]: true }));
        setFieldErrors(validate());
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        const errors = validate();
        setFieldErrors(errors);
        const allTouched = Object.fromEntries(Object.keys(formData).map((k) => [k, true]));
        setTouched(allTouched);
        if (Object.keys(errors).length > 0) return;

        try {
            await API.post('/register', formData);
            alert('Successful Registration');
            navigate('/login');
        } catch (err) {
            setError(err.response?.data || 'Registration failed. Please check your details.');
        }
    };

    const fieldClass = (field) =>
        `${inputClass} ${touched[field] && fieldErrors[field] ? 'border-red-400 focus:ring-red-400' : ''}`;

    return (
        <div className="min-h-screen flex items-center justify-center px-4 py-8">
            <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
                <div className="text-center mb-6">
                    <h1 className="text-3xl font-bold text-gray-800">Create Account</h1>
                    <p className="text-gray-500 mt-1">Join MovieBooking today</p>
                </div>

                {error && <p className="text-red-600 text-sm font-medium text-center bg-red-50 rounded-lg p-3 mb-4">{error}</p>}

                <form onSubmit={handleRegister} className="space-y-3">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <input type="text" placeholder="First Name" value={formData.firstName} className={fieldClass('firstName')} onChange={(e) => handleChange('firstName', e.target.value)} onBlur={() => handleBlur('firstName')} />
                            {touched.firstName && fieldErrors.firstName && <p className="text-red-500 text-xs mt-1">{fieldErrors.firstName}</p>}
                        </div>
                        <div>
                            <input type="text" placeholder="Last Name" value={formData.lastName} className={fieldClass('lastName')} onChange={(e) => handleChange('lastName', e.target.value)} onBlur={() => handleBlur('lastName')} />
                            {touched.lastName && fieldErrors.lastName && <p className="text-red-500 text-xs mt-1">{fieldErrors.lastName}</p>}
                        </div>
                    </div>
                    <div>
                        <input type="email" placeholder="Email" value={formData.email} className={fieldClass('email')} onChange={(e) => handleChange('email', e.target.value)} onBlur={() => handleBlur('email')} />
                        {touched.email && fieldErrors.email && <p className="text-red-500 text-xs mt-1">{fieldErrors.email}</p>}
                    </div>
                    <div>
                        <input type="text" placeholder="Username" value={formData.username} className={fieldClass('username')} onChange={(e) => handleChange('username', e.target.value)} onBlur={() => handleBlur('username')} />
                        {touched.username && fieldErrors.username && <p className="text-red-500 text-xs mt-1">{fieldErrors.username}</p>}
                    </div>
                    <div>
                        <input type="password" placeholder="Password" value={formData.password} className={fieldClass('password')} onChange={(e) => handleChange('password', e.target.value)} onBlur={() => handleBlur('password')} />
                        {touched.password && fieldErrors.password && <p className="text-red-500 text-xs mt-1">{fieldErrors.password}</p>}
                    </div>
                    <div>
                        <input type="password" placeholder="Confirm Password" value={formData.confirmPassword} className={fieldClass('confirmPassword')} onChange={(e) => handleChange('confirmPassword', e.target.value)} onBlur={() => handleBlur('confirmPassword')} />
                        {touched.confirmPassword && fieldErrors.confirmPassword && <p className="text-red-500 text-xs mt-1">{fieldErrors.confirmPassword}</p>}
                    </div>
                    <div>
                        <input type="text" placeholder="Contact Number" value={formData.contactNumber} className={fieldClass('contactNumber')} onChange={(e) => handleChange('contactNumber', e.target.value)} onBlur={() => handleBlur('contactNumber')} />
                        {touched.contactNumber && fieldErrors.contactNumber && <p className="text-red-500 text-xs mt-1">{fieldErrors.contactNumber}</p>}
                    </div>
                    <select className={inputClass} value={formData.role} onChange={(e) => handleChange('role', e.target.value)}>
                        <option value="USER">User</option>
                        <option value="ADMIN">Admin</option>
                    </select>
                    <button type="submit" className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition duration-200 cursor-pointer">
                        Register
                    </button>
                </form>

                <p className="text-center text-sm text-gray-500 mt-5">
                    Already have an account?{' '}
                    <Link to="/login" className="text-blue-600 hover:text-blue-800 font-medium hover:underline">Sign in</Link>
                </p>
            </div>
        </div>
    );
};

export default Register;