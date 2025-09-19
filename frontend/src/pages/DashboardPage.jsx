import { useEffect, useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import LoadingSpinner from '../components/LoadingSpinner';

const DashboardPage = () => {
    const [fields, setFields] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { logout, user } = useAuth();
    const [aiQuestion, setAiQuestion] = useState('');
    const [aiAnswer, setAiAnswer] = useState('');
    const [messages, setMessages] = useState([]);
    const [aiLoading, setAiLoading] = useState(false);

    const handleAskAi = async () => {
        if (!aiQuestion) return;
        setAiLoading(true);
        try {
            setMessages(prev => [...prev, { role: 'user', content: aiQuestion }]);
            const response = await api.get('/ai/generate', {
                params: { message: aiQuestion, userId: user.id },
            });
            setAiAnswer(response.data);
            setMessages(prev => [...prev, { role: 'assistant', content: response.data }]);
            setAiQuestion('');
        } catch (err) {
            setAiAnswer('Error fetching AI response.');
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
                if (err.response && err.response.status === 401) {
                    // Konkretna greška za istekli token
                    setError('Your session has expired. Please log in again.');
                    setTimeout(() => { }, 3000);
                    logout();
                    navigate('/login');
                    // Možete preusmjeriti korisnika na login stranicu
                    // navigate('/login'); 
                } else {
                    // Generička greška
                    setError('Error fetching fields.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchFields();
    }, []);

    if (loading) return <div className='min-h-screen flex items-center justify-center'><LoadingSpinner /></div>;
    if (error) return <div className='text-center mt-8 text-red-500'>{error}</div>;

    return (
        <div>
            <Navbar />
            <div className='container mx-auto p-8'>
                <div className='flex justify-between items-center mb-6'>
                    <h1 className='text-3xl font-bold '>Available football fields</h1>
                </div>

                <div className='grid grid-cols-1 md:grid-cols-2 lg:gid-cols-3 gap-6'>
                    {fields.map(field => (
                        <div key={field.id} className='bg-white p-6 rounded-lg shadow-lg flex flex-col justify-between'>
                            <div>
                                <h2 className='text-xl font-semibold mb-2'>{field.name}</h2>
                                <p className='text-gray-600 mb-2'>{field.address}</p>
                                <p className='text-lg font-bold text-primary-600'>{field.price_per_hour} EUR/h</p>
                            </div>
                            <Link to={`/book/${field.id}`} className='btn-primary text-center mt-4'> Book</Link>
                        </div>
                    ))}
                </div>
                <div className='my-6 w-full'>
                    <h2 className='text-xl font-semibold mb-2'>Ask AI for available slots</h2>
                    <div className='mt-4 mb-4 px-4 py-2 border shadow-lg rounded bg-gray-200 max-w-screen-lg flex flex-col mx-auto'>
                        {messages.length > 0 && (
                            <div className='mt-4 bg-gray-100 p-4 rounded shadow'>
                                {messages.map((msg, index) => (
                                    <div key={index} className={`mb-2 ${msg.role === 'user' ? 'text-right' : 'text-left'}`}>
                                        <span className={`whitespace-pre-wrap inline-block px-4 py-2 rounded ${msg.role === 'user' ? 'bg-blue-500 text-white' : 'bg-gray-300 text-black'}`}>
                                            {msg.content}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                    <div className='flex flex-col md:flex-row gap-4 max-w-screen-lg mx-auto'>
                        <input
                            type='text'
                            className='border border-gray-300 rounded px-4 py-2 flex-1'
                            placeholder='Ask something like "What slots are available next Saturday?"'
                            value={aiQuestion}
                            onChange={(e) => setAiQuestion(e.target.value)}
                            onKeyDown={(e => { if (e.key === 'Enter') handleAskAi(); })}
                        />
                        <button
                            className='bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700'
                            disabled={aiLoading}
                            onClick={handleAskAi}
                        >
                            {aiLoading ? 'Waiting...' : 'Ask AI'}
                        </button>
                    </div>

                    {aiLoading && <LoadingSpinner />}

                </div>

            </div>
        </div>
    );
};

export default DashboardPage;