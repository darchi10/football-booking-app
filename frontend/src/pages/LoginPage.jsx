import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login, loading } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await login(username, password);
            navigate('/');
        } catch (err) {
            setError('Neuspješna prijava. Provjerite korisničko ime i lozinku.');
        }
    };

    return (
        <div className='min-h-screen bg-gray-100'>
            <Navbar /> 
            <div className="flex items-center justify-center mt-10">
                <div className="p-8 bg-white rounded-lg shadow-lg w-full max-w-sm">
                    <h2 className='text-2xl font-bold mb-6 text-center'>Prijava</h2>
                    <form onSubmit={handleSubmit}>
                        {error && <p className='text-red-500 text-sm mb-4'>{error}</p>}
                        <div className='mb-4'>
                            <label className='block text-gray-700 mb-2'>Korisničko ime</label>
                            <input 
                                type="text"
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                className='w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-blue-200'
                                required
                            />
                        </div>
                        <div className='mb-6'>
                            <label className='block text-gray-700 mb-2' htmlFor="password">Lozinka</label>
                            <input 
                                type='password'
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className='w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-blue-200'
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className='btn-primary w-full'
                            disabled={loading}
                        >
                            {loading ? 'Prijavljujem se...' : "Prijavi se"}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;