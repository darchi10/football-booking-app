import { useEffect, useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';

const DashboardPage = () => {
    const [fields, setFields] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { logout } = useAuth();
    const [aiQuestion, setAiQuestion] = useState('');
    const [aiAnswer, setAiAnswer] = useState('');
    const [aiLoading, setAiLoading] = useState(false);

    const handleAskAi = async () => {
        if (!aiQuestion) return;
        setAiLoading(true);
        try {
            const response = await api.get('/ai/generate', {
                params: { message: aiQuestion },
            });
            setAiAnswer(response.data);
        } catch (err) {
            setAiAnswer('Greška pri dohvaćanju odgovora AI-a.');
        } finally {
            setAiLoading(false);
        }
    };


    useEffect(() => {
        const fetchFields = async () => {
            try {
                const response = await api.get('/fields');
                setFields(response.data);
            } catch (err) {
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
            <div className='my-6 w-full'>
                <h2 className='text-xl font-semibold mb-2'>Pitaj AI za slobodne termine</h2>
                <div className='flex flex-col md:flex-row gap-4'>
                    <input
                        type='text'
                        className='border border-gray-300 rounded px-4 py-2 flex-1'
                        placeholder='Npr. Koji su slobodni termini za 2025-07-20?'
                        value={aiQuestion}
                        onChange={(e) => setAiQuestion(e.target.value)}
                    />
                    <button
                        className='bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700'
                        onClick={handleAskAi}
                    >
                        {aiLoading ? 'Čekanje...' : 'Pitaj AI'}
                    </button>
                </div>
                {aiAnswer && (
                    <div className='mt-4 bg-gray-100 p-4 rounded shadow'>
                        <h3 className='font-semibold mb-2'>Odgovor AI-a:</h3>
                        <pre className='whitespace-pre-wrap'>{aiAnswer}</pre>
                    </div>
                )}
            </div>

        </div>
    );
};

export default DashboardPage;