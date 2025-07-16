import { useEffect, useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';

const DashboardPage = () => {
    const [fields, setFields] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const {logout} = useAuth(); 

    useEffect(() => {
        const fetchFields = async () => {
            try {
                const response = await api.get('/fields');
                setFields(response.data);
            } catch(err) {
                setError('Greška pri dohvaćanju terena');
            } finally {
                setLoading(false);
            }
        };

        fetchFields();
    }, []);

    if (loading) return <div className='text-center mt-8'>Učitavanje...</div>;
    if (error) return <div className='text-center mt-8 text-red-500'>{error}</div>;

    return (
        <div className='container mx-auto p-8'>
            <div className='flex justify-between items-center mb-6'>
                <h1 className='text-3xl font-bold '>Dostupni tereni</h1>
            </div>

            <div className='grid grid-cols-1 md:grid-cols-2 lg:gid-cols-3 gap-6'>
                {fields.map(field => (
                    <div key={field.id} className='bg-white p-6 rounded-lg shadow-lg flex flex-col justify-between'>
                        <div>
                            <h2 className='text-xl font-semibold mb-2'>{field.name}</h2>
                            <p className='text-gray-600 mb-2'>{field.address}</p>
                            <p className='text-lg font-bold text-primary-600'>{field.price_per_hour} EUR/h</p>
                        </div>
                        <Link to={`/book/${field.id}`} className='btn-primary text-center mt-4'> Rezerviraj</Link>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default DashboardPage;