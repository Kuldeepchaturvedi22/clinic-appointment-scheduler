import { useEffect, useState } from 'react'
import api from '../api/client'

type User = {
    id: number
    type: 'PATIENT' | 'DOCTOR'
    fullName: string
    email: string
    phone: string
    dateOfBirth?: string
    gender?: string
    specialization?: string
}

export default function AdminUsersPage() {
    const [patients, setPatients] = useState<User[]>([])
    const [doctors, setDoctors] = useState<User[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const [editingUser, setEditingUser] = useState<User | null>(null)
    const [editForm, setEditForm] = useState<any>({})

    useEffect(() => {
        api.get('/admin/users')
            .then(res => {
                setPatients(res.data.patients)
                setDoctors(res.data.doctors)
                setLoading(false)
            })
            .catch(err => {
                setError(err?.response?.data?.message ?? 'Failed to load users')
                setLoading(false)
            })
    }, [])
    
    const handleDelete = async (type: string, id: number) => {
        if (!confirm('Are you sure you want to delete this user?')) return
        try {
            await api.delete(`/admin/users/${type}/${id}`)
            if (type === 'PATIENT') {
                setPatients(prev => prev.filter(p => p.id !== id))
            } else {
                setDoctors(prev => prev.filter(d => d.id !== id))
            }
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to delete user')
        }
    }
    
    const handleEdit = (user: User) => {
        setEditingUser(user)
        setEditForm({
            fullName: user.fullName,
            email: user.email,
            phone: user.phone,
            ...(user.type === 'PATIENT' ? { gender: user.gender } : { specialization: user.specialization })
        })
    }
    
    const handleUpdate = async () => {
        if (!editingUser) return
        try {
            await api.put(`/admin/users/${editingUser.type}/${editingUser.id}`, editForm)
            const updatedUser = { ...editingUser, ...editForm }
            if (editingUser.type === 'PATIENT') {
                setPatients(prev => prev.map(p => p.id === editingUser.id ? updatedUser : p))
            } else {
                setDoctors(prev => prev.map(d => d.id === editingUser.id ? updatedUser : d))
            }
            setEditingUser(null)
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to update user')
        }
    }

    if (loading) return (
        <div className="page-container">
            <div style={{textAlign: 'center', padding: '2rem'}}>
                <div className="loading"></div>
                <p>Loading users...</p>
            </div>
        </div>
    )

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>👥 Manage Users</h2>
            
            <div className="grid grid-2">
                <div className="card">
                    <h3 style={{color: '#667eea', marginBottom: '1rem'}}>👤 Patients ({patients.length})</h3>
                    <div style={{maxHeight: '500px', overflowY: 'auto'}}>
                        {patients.map(patient => (
                            <div key={patient.id} style={{
                                border: '1px solid #e5e7eb',
                                borderRadius: '8px',
                                padding: '1rem',
                                marginBottom: '1rem',
                                background: '#f9fafb'
                            }}>
                                <div style={{fontWeight: '600', marginBottom: '0.5rem'}}>
                                    {patient.fullName}
                                </div>
                                <div style={{fontSize: '0.875rem', color: '#666', display: 'grid', gap: '0.25rem'}}>
                                    <div><strong>ID:</strong> #{patient.id}</div>
                                    <div><strong>Email:</strong> {patient.email}</div>
                                    <div><strong>Phone:</strong> {patient.phone}</div>
                                    <div><strong>DOB:</strong> {patient.dateOfBirth}</div>
                                    <div><strong>Gender:</strong> {patient.gender}</div>
                                </div>
                                <div style={{display: 'flex', gap: '0.5rem', marginTop: '0.5rem'}}>
                                    <button className="btn btn-secondary" style={{fontSize: '0.75rem', padding: '0.25rem 0.5rem'}} onClick={() => handleEdit(patient)}>Edit</button>
                                    <button className="btn" style={{fontSize: '0.75rem', padding: '0.25rem 0.5rem', background: '#ef4444', color: 'white'}} onClick={() => handleDelete('PATIENT', patient.id)}>Delete</button>
                                </div>
                            </div>
                        ))}
                        {patients.length === 0 && (
                            <div style={{textAlign: 'center', color: '#666', padding: '2rem'}}>
                                No patients found
                            </div>
                        )}
                    </div>
                </div>

                <div className="card">
                    <h3 style={{color: '#667eea', marginBottom: '1rem'}}>👨‍⚕️ Doctors ({doctors.length})</h3>
                    <div style={{maxHeight: '500px', overflowY: 'auto'}}>
                        {doctors.map(doctor => (
                            <div key={doctor.id} style={{
                                border: '1px solid #e5e7eb',
                                borderRadius: '8px',
                                padding: '1rem',
                                marginBottom: '1rem',
                                background: '#f0fff4'
                            }}>
                                <div style={{fontWeight: '600', marginBottom: '0.5rem'}}>
                                    Dr. {doctor.fullName}
                                </div>
                                <div style={{fontSize: '0.875rem', color: '#666', display: 'grid', gap: '0.25rem'}}>
                                    <div><strong>ID:</strong> #{doctor.id}</div>
                                    <div><strong>Email:</strong> {doctor.email}</div>
                                    <div><strong>Phone:</strong> {doctor.phone}</div>
                                    <div><strong>Specialization:</strong> {doctor.specialization}</div>
                                </div>
                                <div style={{display: 'flex', gap: '0.5rem', marginTop: '0.5rem'}}>
                                    <button className="btn btn-secondary" style={{fontSize: '0.75rem', padding: '0.25rem 0.5rem'}} onClick={() => handleEdit(doctor)}>Edit</button>
                                    <button className="btn" style={{fontSize: '0.75rem', padding: '0.25rem 0.5rem', background: '#ef4444', color: 'white'}} onClick={() => handleDelete('DOCTOR', doctor.id)}>Delete</button>
                                </div>
                            </div>
                        ))}
                        {doctors.length === 0 && (
                            <div style={{textAlign: 'center', color: '#666', padding: '2rem'}}>
                                No doctors found
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <div className="card" style={{marginTop: '2rem', textAlign: 'center', background: '#f7fafc'}}>
                <h4 style={{color: '#4a5568', marginBottom: '0.5rem'}}>Total Users: {patients.length + doctors.length}</h4>
                <p style={{color: '#666', margin: 0}}>
                    {patients.length} Patients • {doctors.length} Doctors
                </p>
            </div>
            
            {editingUser && (
                <div className="modal-overlay" onClick={() => setEditingUser(null)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <h3>Edit {editingUser.type === 'PATIENT' ? 'Patient' : 'Doctor'}</h3>
                        <input
                            type="text"
                            placeholder="Full Name"
                            value={editForm.fullName || ''}
                            onChange={e => setEditForm({...editForm, fullName: e.target.value})}
                        />
                        <input
                            type="email"
                            placeholder="Email"
                            value={editForm.email || ''}
                            onChange={e => setEditForm({...editForm, email: e.target.value})}
                        />
                        <input
                            type="tel"
                            placeholder="Phone"
                            value={editForm.phone || ''}
                            onChange={e => setEditForm({...editForm, phone: e.target.value})}
                        />
                        {editingUser.type === 'PATIENT' ? (
                            <input
                                type="text"
                                placeholder="Gender"
                                value={editForm.gender || ''}
                                onChange={e => setEditForm({...editForm, gender: e.target.value})}
                            />
                        ) : (
                            <input
                                type="text"
                                placeholder="Specialization"
                                value={editForm.specialization || ''}
                                onChange={e => setEditForm({...editForm, specialization: e.target.value})}
                            />
                        )}
                        <div className="form-actions">
                            <button type="button" className="btn btn-secondary" onClick={() => setEditingUser(null)}>Cancel</button>
                            <button type="button" className="btn btn-primary" onClick={handleUpdate}>Update</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}