import { useAuth } from '../context/AuthContext';
import { Navigate, Outlet } from 'react-router-dom';

const AdminRoute = () => {
    const {user} = useAuth();
    const isAdmin = user && user.roles.includes('ADMIN');

    return isAdmin ? <Outlet /> : <Navigate to="/" />;
};

export default AdminRoute;