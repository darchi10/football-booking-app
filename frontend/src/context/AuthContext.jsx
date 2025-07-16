import { createContext, useState, useContext, useEffect } from 'react';
import api from '../services/api';
import {jwtDecode} from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem('token');
        if (!token) return null;
        const decodedToken = jwtDecode(token);
        return { roles: decodedToken.roles || []};
    });
    const [loading, setLoading] = useState(false);

    const login = async (username, password) => {
        setLoading(true);
        try {
            const response = await api.post('/auth/login', { username, password });
            const {token} = response.data;
            localStorage.setItem('token', token);
            setToken(token);
            const decodedToken = jwtDecode(token);
            setUser({ roles: decodedToken.roles || [] });
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        setToken(null);
        setUser(null);
    };

    const value = {
        token,
        user,
        loading,
        login,
        logout,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
};

export const useAuth = () => {
    return useContext(AuthContext);
};