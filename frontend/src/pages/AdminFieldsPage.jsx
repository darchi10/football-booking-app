import React, { useState, useEffect } from 'react';
import api from '../services/api';

const AdminFieldsPage = () => {
    const [fields, setFields] = useState([]);
    const [formData, setFormData] = useState({name: '', address: '', price_per_hour: ''});
    const [isEditing, setIsEditing] = useState(false);
    const [editingId, setEditingId] = useState(null);

    useEffect(() => {
        api.get('/fields').then(res => setFields(res.data));
    }, []);

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({...prev, [name]: value}));
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        const apiCall = isEditing ? api.put(`/fields/${editingId}`, formData) : api.post('/fields', formData);

        try {
            const response = await apiCall;
            
            if (isEditing) {
                setFields(fields.map(f => f.id === editingId ? response.data : f));
            }
            else {
                setFields([...fields, response.data]);
            }

            resetForm();
        } catch (error) {
            console.error("Error saving field", error);
        }
    };

    const handleEdit = (field) => {
        setIsEditing(true);
        setEditingId(field.id);
        setFormData({name: field.name, address: field.address, price_per_hour: field.price_per_hour});
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure?')) {
            await api.delete(`/fields/${id}`);
            setFields(fields.filter(f => f.id !== id));
        }
    };

    const resetForm = () => {
        setIsEditing(false);
        setEditingId(null);
        setFormData({name: '', address: '', price_per_hour: ''});
    };

    return (
        <div className='container mx-auto p-8'>
            <h1 className='text-3xl font-bold mb-6'>Field Management</h1>
            <div className='bg-white p-6 rounded-lg shadow-md mb-8'>
                <h2 className='text-2xl font-semibold mb-4'>{isEditing ? 'Edit Field' : 'Add New Field'}</h2>
                <form onSubmit={handleSubmit}>
                    <div className='grid grid-cols-1 md:grid-cols-3 gap-4 mb-4'>
                        <input type='text' name="name" value={formData.name} onChange={handleChange} placeholder='Field Name' 
                        className='p-2 border rounded' required />
                        <input type='text' name='address' value={formData.address} onChange={handleChange} placeholder='Address' 
                        className='p-2 border rounded' required />
                        <input type='number' name='price_per_hour' value={formData.price_per_hour} onChange={handleChange} 
                        placeholder='Price per hour' className='p-2 border rounded' required />
                    </div>
                    <div className='flex gap-4'>
                        <button type='submit' className='btn-primary'>{isEditing ? 'Save Changes' : 'Add Field'}</button>
                        {isEditing && <button type='button' onClick={resetForm} className='btn-secondary'>Cancel</button>}
                    </div>
                </form>
            </div>

            <div className='bg-white p-6 rounded-lg shadow-md'>
                <h2 className='text-2xl font-semibold mb-4'>Existing Fields</h2>
                <div className='overflow-x-auto'>
                    <table className='min-w-full'>
                        <thead className='bg-gray-100'>
                            <tr>
                                <th className='p-3 text-left'>Name</th>
                                <th className='p-3 text-left'>Address</th>
                                <th className='p-3 text-left'>Price</th>
                                <th className='p-3 text-left'>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {fields.map(field => {
                                return (
                                <tr key={field.id} className='border-b'>
                                    <td className='p-3'>{field.name}</td>
                                    <td className='p-3'>{field.address}</td>
                                    <td className='p-3'>{field.price_per_hour}</td>
                                    <td className='p-3 flex gap-2'>
                                        <button onClick={() => handleEdit(field)} className='text-blue-500 hover:underline'>Edit</button>
                                        <button onClick={() => handleDelete(field.id)} className='text-red-500 hover:underline'>Delete</button>
                                    </td>
                                </tr>)
                            })}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminFieldsPage;