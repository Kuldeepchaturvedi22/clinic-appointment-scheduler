import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/client'
import { validateEmail, validatePhone, validateName, validateDate } from '../utils/validation'

type Patient = {
    id: number
    fullName: string
    email: string
    phone: string
    dateOfBirth: string
    gender?: string
}

type Appointment = {
    id: number
    startTime: string
    endTime: string
    status: 'SCHEDULED'
    doctorName: string
    doctorSpecialization: string
}

export default function PatientMePage() {
    const [data, setData] = useState<Patient | null>(null)
    const [scheduledAppointments, setScheduledAppointments] = useState<Appointment[]>([])
    const [error, setError] = useState<string | null>(null)
    const [isEditing, setIsEditing] = useState(false)
    const [formData, setFormData] = useState<Patient | null>(null)
    const [validationErrors, setValidationErrors] = useState<{[key: string]: string}>({})

    useEffect(() => {
        api.get('/patients/me')
            .then(res => setData(res.data as Patient))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load profile'))
        
        // Load scheduled appointments for chat access
        api.get('/patients/me/appointments/history')
            .then(res => {
                const scheduled = (res.data as Appointment[]).filter(apt => apt.status === 'SCHEDULED')
                setScheduledAppointments(scheduled)
            })
            .catch(() => {}) // Ignore errors for appointments
    }, [])

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )
    
    if (!data) return (
        <div className="page-container" style={{textAlign: 'center'}}>
            <div className="loading"></div>
            <p>Loading your profile...</p>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>👤 My Profile</h2>
            <div className="grid grid-2">
                <div className="card">
                    <h3 style={{margin: '0 0 1rem 0', color: '#667eea'}}>🆔 Patient Information</h3>
                    <div style={{display: 'grid', gap: '0.75rem'}}>
                        <div>
                            <strong>Patient ID:</strong> #{data.id}
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
                            <strong>Date of Birth:</strong> {new Date(data.dateOfBirth).toLocaleDateString()}
                        </div>
                        <div>
                            <strong>Gender:</strong> {data.gender || 'Not specified'}
                        </div>
                    </div>
                </div>
                <div className="card">
                    <h3 style={{margin: '0 0 1rem 0', color: '#667eea'}}>📅 Quick Actions</h3>
                    <div style={{display: 'grid', gap: '0.75rem'}}>
                        <Link to="/appointments/book" className="btn btn-primary" style={{width: '100%', textDecoration: 'none'}}>
                            📅 Book New Appointment
                        </Link>
                        <Link to="/me/patient/appointments/history" className="btn btn-secondary" style={{width: '100%', textDecoration: 'none'}}>
                            📊 View Appointment History
                        </Link>
                        <button className="btn btn-secondary" style={{width: '100%'}} onClick={() => {
                            setIsEditing(true)
                            setFormData(data)
                        }}>
                            ⚙️ Update Profile
                        </button>
                        <Link to="/help-chat" className="btn btn-secondary" style={{width: '100%', textDecoration: 'none'}}>
                            💬 Help & Support
                        </Link>
                    </div>
                </div>
            </div>
            
            {scheduledAppointments.length > 0 && (
                <div className="card" style={{marginTop: '2rem'}}>
                    <h3 style={{margin: '0 0 1rem 0', color: '#667eea'}}>💬 Active Chats</h3>
                    <div className="grid">
                        {scheduledAppointments.map(apt => (
                            <div key={apt.id} style={{
                                border: '1px solid #e5e7eb',
                                borderRadius: '8px',
                                padding: '1rem',
                                background: '#f9fafb'
                            }}>
                                <div style={{marginBottom: '0.5rem'}}>
                                    <strong>Dr. {apt.doctorName}</strong>
                                    <div style={{fontSize: '0.875rem', color: '#666'}}>{apt.doctorSpecialization}</div>
                                </div>
                                <div style={{fontSize: '0.875rem', color: '#666', marginBottom: '1rem'}}>
                                    {new Date(apt.startTime).toLocaleDateString()} • {new Date(apt.startTime).toLocaleTimeString()}
                                </div>
                                <Link to={`/chat/${apt.id}`} className="btn btn-primary" style={{textDecoration: 'none', width: '100%'}}>
                                    💬 Open Chat
                                </Link>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            
            {isEditing && formData && (
                <div className="modal-overlay" onClick={() => setIsEditing(false)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <h3>Update Profile</h3>
                        <form onSubmit={async (e) => {
                            e.preventDefault()
                            if (!formData) return
                            
                            const errors: {[key: string]: string} = {}
                            const nameError = validateName(formData.fullName)
                            const emailError = validateEmail(formData.email)
                            const phoneError = validatePhone(formData.phone)
                            const dateError = validateDate(formData.dateOfBirth)
                            
                            if (nameError) errors.fullName = nameError
                            if (emailError) errors.email = emailError
                            if (phoneError) errors.phone = phoneError
                            if (dateError) errors.dateOfBirth = dateError
                            
                            setValidationErrors(errors)
                            if (Object.keys(errors).length > 0) return
                            
                            try {
                                const res = await api.put('/patients/me', formData)
                                setData(res.data)
                                setIsEditing(false)
                                setValidationErrors({})
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
                            {validationErrors.fullName && <div className="alert alert-error">{validationErrors.fullName}</div>}
                            <input
                                type="email"
                                placeholder="Email"
                                value={formData.email}
                                onChange={e => setFormData({...formData, email: e.target.value})}
                                required
                            />
                            {validationErrors.email && <div className="alert alert-error">{validationErrors.email}</div>}
                            <input
                                type="tel"
                                placeholder="Phone"
                                value={formData.phone}
                                onChange={e => setFormData({...formData, phone: e.target.value})}
                                required
                            />
                            {validationErrors.phone && <div className="alert alert-error">{validationErrors.phone}</div>}
                            <input
                                type="date"
                                value={formData.dateOfBirth}
                                onChange={e => setFormData({...formData, dateOfBirth: e.target.value})}
                            />
                            {validationErrors.dateOfBirth && <div className="alert alert-error">{validationErrors.dateOfBirth}</div>}
                            <select
                                value={formData.gender || ''}
                                onChange={e => setFormData({...formData, gender: e.target.value})}
                            >
                                <option value="">Select Gender</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Other">Other</option>
                            </select>
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