import { useAuth } from '../context/AuthContext';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRouteSigned = () => {
    const {token} = useAuth();

    if (token) {
        return <Navigate to="/" />;
    }

    return <Outlet />
};

export default ProtectedRouteSigned;