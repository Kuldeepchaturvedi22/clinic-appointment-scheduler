import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/client'

type Doctor = {
    id: number
    specialization: string
    fullName: string
    email: string
    phone: string
}

export default function DoctorMePage() {
    const [data, setData] = useState<Doctor | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [isEditing, setIsEditing] = useState(false)
    const [formData, setFormData] = useState<any>(null)

    useEffect(() => {
        api.get('/doctors/me')
            .then(res => setData(res.data as Doctor))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load profile'))
    }, [])

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )
    
    if (!data) return (
        <div className="page-container" style={{textAlign: 'center'}}>
            <div className="loading"></div>
            <p>Loading your dashboard...</p>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>👨⚕️ My Profile</h2>
            <div className="grid grid-2">
                <div className="card">
                    <h3 style={{margin: '0 0 1rem 0', color: '#667eea'}}>🆔 Doctor Information</h3>
                    <div style={{display: 'grid', gap: '0.75rem'}}>
                        <div>
                            <strong>Doctor ID:</strong> #{data.id}
                        </div>
                        <div>
                            <strong>Full Name:</strong> {data.fullName}
                        </div>
                        <div>
                            <strong>Email:</strong> {data.email}
                        </div>
                        <div>
                            <strong>Phone:</strong> {data.phone}
                        </div>
                        <div>
                            <strong>Specialization:</strong> {data.specialization || 'General Practice'}
                        </div>
                        <div style={{marginTop: '1rem'}}>
                            <span style={{background: '#e6fffa', color: '#234e52', padding: '0.25rem 0.75rem', borderRadius: '20px', fontSize: '0.875rem'}}>
                                ✅ Active
                            </span>
                        </div>
                    </div>
                </div>
                <div className="card">
                    <h3 style={{margin: '0 0 1rem 0', color: '#667eea'}}>⚙️ Profile Actions</h3>
                    <div style={{display: 'grid', gap: '0.75rem'}}>
                        <button className="btn btn-primary" style={{width: '100%'}} onClick={() => {
                            setIsEditing(true)
                            setFormData({
                                fullName: data.fullName || '',
                                email: data.email || '',
                                phone: data.phone || '',
                                specialization: data.specialization || ''
                            })
                        }}>
                            ⚙️ Update Profile
                        </button>
                        <Link to="/help-chat" className="btn btn-secondary" style={{width: '100%', textDecoration: 'none'}}>
                            💬 Help & Support
                        </Link>
                    </div>
                </div>
            </div>
            
            {isEditing && formData && (
                <div className="modal-overlay" onClick={() => setIsEditing(false)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <h3>Update Profile</h3>
                        <form onSubmit={async (e) => {
                            e.preventDefault()
                            try {
                                const res = await api.put('/doctors/me', formData)
                                setData(res.data)
                                setIsEditing(false)
                            } catch (err: any) {
                                setError(err?.response?.data?.message ?? 'Failed to update profile')
                            }
                        }}>
                            <input
                                type="text"
                                placeholder="Full Name"
                                value={formData.fullName}
                                onChange={e => setFormData({...formData, fullName: e.target.value})}
                                required
                            />
                            <input
                                type="email"
                                placeholder="Email"
                                value={formData.email}
                                onChange={e => setFormData({...formData, email: e.target.value})}
                                required
                            />
                            <input
                                type="tel"
                                placeholder="Phone"
                                value={formData.phone}
                                onChange={e => setFormData({...formData, phone: e.target.value})}
                                required
                            />
                            <input
                                type="text"
                                placeholder="Specialization"
                                value={formData.specialization}
                                onChange={e => setFormData({...formData, specialization: e.target.value})}
                                required
                            />
                            <div className="form-actions">
                                <button type="button" className="btn btn-secondary" onClick={() => setIsEditing(false)}>Cancel</button>
                                <button type="submit" className="btn btn-primary">Update</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    )
}