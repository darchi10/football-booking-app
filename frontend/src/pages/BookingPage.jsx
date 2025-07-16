import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';

const BookingPage = () => {
    const { fieldId } = useParams();
    const navigate = useNavigate();
    const [field, setField] = useState(null);
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [reservations, setReservations] = useState([]);

    useEffect(() => {
        const today = new Date().toISOString().split('T')[0];
        setDate(today);

        api.get(`/fields/${fieldId}`)
            .then(response => setField(response.data))
            .catch(err => setError('Nije moguće učitati podatke o terenu.'));
    }, [fieldId]);

    useEffect(() => {
        const fetchReservations = async () => {
            try {
                const response = await api.get(`/reservations`);
                setReservations(response.data);
            } catch (err) {
                setError('Greška pri dohvaćanju terena');
            } finally {
                setLoading(false);
            }
        };
        fetchReservations();
    }, [])

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!date || !time) {
            setError('Molimo odaberite datum i vrijeme.');
        }

        setLoading(true);
        setError('');
        setSuccess('');

        const startTime = `${date}T${time}:00`;

        try {
            await api.post('/reservations', {
                fieldId: parseInt(fieldId),
                startTime: startTime,
            });

            setSuccess('Termin je uspješno rezerviran!');
            setTimeout(() => navigate('/'), 2000); 
        } catch (err) {
            if (err.response && err.response.status === 409) {
                setError(err.response.data);
            } else {
                setError('Došlo je do pogreške prilikom rezervacije.');
            }
        } finally {
            setLoading(false);
        }
    };

    if (!field) return <div>Učitavanje...</div>;

    return (
        <div className="container mx-auto p-8">
            <h1 className="text-3xl font-bold mb-4">Rezerviraj teren: {field.name}</h1>

            <div className="max-w-lg bg-white p-6 rounded-lg shadow-lg flex-1">
                <form onSubmit={handleSubmit}>
                    {error && <div className='bg-red-100 text-red-700 p-3 rounded mb-4'>{error}</div>}
                    {success && <div className='bg-green-100 text-green-700 p-3 rounded mb-4'>{success}</div>}

                    <div className='mb-4'>
                        <label className='block text-gray-700 mb-2'>Datum</label>
                        <input
                            type="date"
                            value={date}
                            onChange={e => setDate(e.target.value)}
                            min={new Date().toISOString().split('T')[0]}
                            className='w-full px-3 py-2 border rounded-lg'
                            required
                        />
                    </div>
                    <div className='mb-6'>
                        <label className='block text-gray-700 mb2'>Vrijeme</label>
                        <select
                            value={time}
                            onChange={e => setTime(e.target.value)}
                            className='w-full px-3 py-2 border rounded-lg"'
                            required
                        >
                            <option value="" disabled>Odaberite termin</option>
                            {Array.from({ length: 14 }, (_, i) => i + 9)
                                .filter(hour => {
                                    const selectedDate = new Date(date);
                                    const today = new Date();

                                    if (selectedDate.toDateString() === today.toDateString()) {
                                        return hour > today.getHours();
                                    }
                                    return true;
                                })
                                .map(hour => {
                                    const isBooked = reservations.some(reservation => {
                                        const reservationTime = new Date(reservation.startTime);
                                        return reservationTime.getHours() === hour && reservationTime.toDateString() === new Date(date).toDateString();
                                    });
                                    return (
                                        <option
                                            key={hour}
                                            value={`${hour.toString().padStart(2, '0')}:00`}
                                            disabled={isBooked}
                                        >
                                            {`${hour}:00 - ${hour + 1}:00`} {isBooked && '(Zauzeto)'}
                                        </option>
                                    );
                                })
                            }
                        </select>
                    </div>
                    <button type='submit' className='btn-primary w-full' disabled={loading}>
                        {loading ? 'Slanje...' : 'Potvrdi rezervaciju'}
                    </button>
                </form>
            </div>

        </div>
    );
};

export default BookingPage;