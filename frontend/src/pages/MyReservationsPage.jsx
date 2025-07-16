import { useState, useEffect } from 'react';
import api from '../services/api';
import { format, parseISO, differenceInSeconds } from 'date-fns';
import CountdownTimer from '../services/CountDownTimer';

const MyReservationsPage = () => {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showAll, setShowAll] = useState(false);

    useEffect(() => {
        const fetchMyReservations = async () => {
            try {
                const response = await api.get('/reservations/my');
                setReservations(response.data);
            }
            catch (err) {
                setError('Greška pri dohvaćanju vaših rezervacija.');
                console.error(err);
            }
            finally {
                setLoading(false);
            }
        };

        fetchMyReservations();
    }, []);

    if (loading) return <div className='text-center mt-8'>Učitavanje vaših rezervacija...</div>;
    if (error) return <div className='text-center mt-8 text-red-500'>{error}</div>;

    return (
        <div className='container mx-auto p-8'>
            <h1 className='text-3xl font-bold mb-6 text-center'>Moje rezervacije</h1>
            {reservations.length === 0 ? (
                <p className='text-center text-gray-600'>Trenutno nemate aktivnih rezervacija.</p>
            ) : (
                <>
                    <div className='pl-6 mt-4'>
                        <input
                            type="checkbox"
                            id="showAll"
                            checked={showAll}
                            onChange={() => setShowAll(!showAll)}
                            className="mr-1"
                        />
                        <label htmlFor="showAll" className="text-gray-700">Prikaži sve rezervacije</label>
                    </div>
                    <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6'>
                        {reservations
                            .filter(reservation => {
                                if (showAll) return true;
                                const start = parseISO(reservation.startTime);
                                return start > new Date();
                            })
                            .map(reservation => (
                            <div key={reservation.id} className='bg-white p-6 rounded-lg shadow-lg'>
                                <h2 className='text-xl font-semibold mb-2 text-primary-700'>{reservation.fieldName}</h2>
                                <p className='text-gray-700 mb-1'>
                                    <span className='font-medium'>Početak: </span>
                                    {format(parseISO(reservation.startTime), 'dd.MM.yyyy. HH:mm')}
                                </p>
                                <p className='text-gray-700 mb-1'>
                                    <span className='font-medium'>Kraj: </span>
                                    {format(parseISO(reservation.endTime), 'dd.MM.yyyy. HH:mm')}
                                </p>
                                <p className='text-gray-500 text-sm mt-3'>
                                    Rezervirao: {reservation.username}
                                </p>
                                <CountdownTimer startTime={reservation.startTime} />
                            </div>
                        ))}
                    </div>
                </>
            )}
        </div>
    );
};

export default MyReservationsPage;