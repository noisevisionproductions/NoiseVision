import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json'
    }
});

interface IpResponse {
    ip: string;
}

const getClientIP = async () => {
    try {
        const response = await fetch('https://api.ipify.org?format=json');
        const data: IpResponse = await response.json();
        return data.ip;
    } catch (error) {
        console.error('Błąd podczas pobierania IP:', error);
        return null;
    }
}

api.interceptors.request.use(async (config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    const clientIp = await getClientIP();
    if (clientIp && config.headers) {
        config.headers['X-Forwarded-For'] = clientIp;
        config.headers['X-Real-IP'] = clientIp;
        config.headers['X-Forwarded-Proto'] = window.location.protocol.replace(':', '');
        config.headers['X-Forwarded-Host'] = window.location.host;
    }

    return config;
}, error => {
    return Promise.reject(error);
});

api.interceptors.response.use(
    response => response,
    error => {
        return Promise.reject(error);
    }
);

export default api;