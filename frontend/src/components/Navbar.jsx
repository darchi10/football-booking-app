import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { token, user, logout } = useAuth();
    const isAdmin = user && user.roles.includes("ADMIN");

    return (
        <nav className='bg-primary-700 p-4 shadow-lg sticky top-0 z-50'>
            <div className='flex justify-between items-center'>
                <Link to="/" className='text-white text-2xl font-bold'>Football Booking</Link>
                <div className='space-x-4'>
                    {token ? (
                        <>
                            <Link to="/" className='text-white hover:text-primary-200 transition-colors'>Football fields</Link>
                            <Link to="/my-reservations" className='text-white hover:text-primary-200 transition-colors'>My Reservations</Link>
                            {isAdmin && (
                                <Link to="/admin/fields" className='text-white hover:text-primary-200 transition-colors'>Administration</Link>
                            )}
                            <button onClick={logout} className='text-white hover:text-primary-200 transition-colors'>Logout</button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className='text-white hover:text-primary-200 transition-colors'>Login</Link>
                            <Link to="/register" className='text-white hover:text-primary-200 transition-colors'>Register</Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;