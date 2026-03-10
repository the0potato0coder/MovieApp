import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';

const Profile = () => {
    const [passwordData, setPasswordData] = useState({ oldPassword: '', newPassword: '', confirmPassword: '' });
    const [passwordMessage, setPasswordMessage] = useState({ type: '', text: '' });
    const navigate = useNavigate();
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');

    const handleChangePassword = async (e) => {
        e.preventDefault();
        setPasswordMessage({ type: '', text: '' });

        if (passwordData.newPassword !== passwordData.confirmPassword) {
            setPasswordMessage({ type: 'error', text: 'New passwords do not match.' });
            return;
        }

        if (passwordData.newPassword.length < 6) {
            setPasswordMessage({ type: 'error', text: 'Password must be at least 6 characters.' });
            return;
        }

        try {
            await API.put(`/${username}/change-password`, {
                oldPassword: passwordData.oldPassword,
                newPassword: passwordData.newPassword
            });
            setPasswordMessage({ type: 'success', text: 'Password updated successfully!' });
            setPasswordData({ oldPassword: '', newPassword: '', confirmPassword: '' });
        } catch (err) {
            setPasswordMessage({ type: 'error', text: err.response?.data || 'Failed to change password.' });
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    return (
        <div className="max-w-xl mx-auto px-4 py-6">
            {/* Header */}
            <header className="flex justify-between items-center bg-white rounded-xl shadow-sm px-6 py-4 mb-8">
                <h1 className="text-2xl font-bold text-gray-800">My Profile</h1>
                <div className="flex gap-2">
                    <button onClick={() => navigate('/home')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                        Home
                    </button>
                    <button onClick={() => navigate('/my-tickets')} className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition cursor-pointer">
                        My Tickets
                    </button>
                    <button onClick={handleLogout} className="px-4 py-2 text-sm bg-red-500 hover:bg-red-600 text-white rounded-lg transition cursor-pointer">
                        Logout
                    </button>
                </div>
            </header>

            {/* Profile Card */}
            <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
                <div className="flex items-center gap-5">
                    <div className="w-16 h-16 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 text-white flex items-center justify-center text-2xl font-bold shrink-0">
                        {username?.charAt(0).toUpperCase()}
                    </div>
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800">{username}</h2>
                        <span className={`inline-block mt-1 px-3 py-0.5 rounded-full text-xs font-semibold uppercase tracking-wide ${
                            role === 'ADMIN' ? 'bg-amber-100 text-amber-700' : 'bg-gray-200 text-gray-600'
                        }`}>
                            {role}
                        </span>
                    </div>
                </div>
            </div>

            {/* Change Password Card */}
            <div className="bg-white rounded-xl shadow-sm p-6">
                <h3 className="text-lg font-semibold text-gray-800 mb-4">Change Password</h3>

                {passwordMessage.text && (
                    <p className={`text-sm font-medium text-center rounded-lg p-3 mb-4 ${
                        passwordMessage.type === 'error' ? 'text-red-600 bg-red-50' : 'text-green-600 bg-green-50'
                    }`}>
                        {passwordMessage.text}
                    </p>
                )}

                <form onSubmit={handleChangePassword} className="space-y-3">
                    <input
                        type="password"
                        placeholder="Current Password"
                        value={passwordData.oldPassword}
                        onChange={(e) => setPasswordData({ ...passwordData, oldPassword: e.target.value })}
                        required
                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                    />
                    <input
                        type="password"
                        placeholder="New Password"
                        value={passwordData.newPassword}
                        onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                        required
                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                    />
                    <input
                        type="password"
                        placeholder="Confirm New Password"
                        value={passwordData.confirmPassword}
                        onChange={(e) => setPasswordData({ ...passwordData, confirmPassword: e.target.value })}
                        required
                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                    />
                    <button type="submit" className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition duration-200 cursor-pointer">
                        Update Password
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Profile;
