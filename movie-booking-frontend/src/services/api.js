import axios from 'axios';

const API = axios.create({
    baseURL: 'http://localhost:8080/api/v1.0/moviebooking'
});

// Automatically attach JWT token to every request if it exists in localStorage
API.interceptors.request.use((req) => {
    const token = localStorage.getItem('token');
    if (token) {
        req.headers.Authorization = `Bearer ${token}`;
    }
    return req;
});

export default API;