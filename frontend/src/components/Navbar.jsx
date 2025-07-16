import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { token, user, logout } = useAuth();
    const isAdmin = user && user.roles.includes("ADMIN");

    return (
        <nav className='bg-primary-700 p-4 shadow-lg'>
            <div className='container mx-auto flex justify-between items-center'>
                <Link to="/" className='text-white text-2xl font-bold'>Football Booking</Link>
                <div className='space-x-4'>
                    {token ? (
                        <>
                            <Link to="/" className='text-white hover:text-primary-200 transition-colors'>Tereni</Link>
                            <Link to="/my-reservations" className='text-white hover:text-primary-200 transition-colors'>Moje rezervacije</Link>
                            {isAdmin && (
                                <Link to="/admin/fields" className='text-white hover:text-primary-200 transition-colors'>Administracija</Link>
                            )}
                            <button onClick={logout} className='text-white hover:text-primary-200 transition-colors'>Odjavi se</button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className='text-white hover:text-primary-200 transition-colors'>Prijavi se</Link>
                            <Link to="/register" className='text-white hover:text-primary-200 transition-colors'>Registriraj se</Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;