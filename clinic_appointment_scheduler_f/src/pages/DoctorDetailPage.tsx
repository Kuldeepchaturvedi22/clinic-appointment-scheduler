import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../api/client'

type Doctor = {
    id: number
    fullName: string
    email: string
    phone: string
    specialization: string
    gender?: string
}

export default function DoctorDetailPage() {
    const { id } = useParams()
    const [doctor, setDoctor] = useState<Doctor | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [showContact, setShowContact] = useState(false)
    const navigate = useNavigate()

    useEffect(() => {
        if (!id) return
        api.get(`/doctors/${id}`)
            .then(res => setDoctor(res.data as Doctor))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load doctor'))
    }, [id])

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )
    
    if (!doctor) return (
        <div className="page-container" style={{textAlign: 'center'}}>
            <div className="loading"></div>
            <p>Loading doctor profile...</p>
        </div>
    )

    return (
        <div className="page-container">
            <div className="card" style={{maxWidth: '600px', margin: '0 auto', textAlign: 'center'}}>
                <div style={{fontSize: '4rem', marginBottom: '1rem'}}>👨⚕️</div>
                <h2 style={{margin: '0 0 1rem 0', color: '#333'}}>Dr. {doctor.fullName}</h2>
                
                <div style={{display: 'grid', gap: '1rem', marginBottom: '2rem'}}>
                    <div style={{background: '#f7fafc', padding: '1rem', borderRadius: '8px'}}>
                        <h3 style={{margin: '0 0 0.5rem 0', color: '#667eea'}}>Doctor Information</h3>
                        <p style={{margin: '0.25rem 0'}}><strong>ID:</strong> #{doctor.id}</p>
                        <p style={{margin: '0.25rem 0'}}><strong>Specialization:</strong> {doctor.specialization || 'General Practice'}</p>
                        <p style={{margin: '0.25rem 0'}}><strong>Gender:</strong> {doctor.gender || 'Not specified'}</p>
                    </div>
                    
                    {showContact && (
                        <div style={{background: '#e6fffa', padding: '1rem', borderRadius: '8px', border: '1px solid #81e6d9'}}>
                            <h3 style={{margin: '0 0 0.5rem 0', color: '#2c7a7b'}}>Contact Information</h3>
                            <p style={{margin: '0.25rem 0'}}><strong>📧 Email:</strong> {doctor.email}</p>
                            <p style={{margin: '0.25rem 0'}}><strong>📞 Phone:</strong> {doctor.phone}</p>
                        </div>
                    )}
                </div>
                
                <div style={{display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap'}}>
                    <button 
                        className="btn btn-primary" 
                        style={{minWidth: '160px'}}
                        onClick={() => navigate('/book-appointment', { state: { doctorId: doctor.id, doctorName: doctor.fullName, specialization: doctor.specialization } })}
                    >
                        📅 Book Appointment
                    </button>
                    <button 
                        className="btn btn-secondary" 
                        style={{minWidth: '120px'}}
                        onClick={() => setShowContact(!showContact)}
                    >
                        📞 {showContact ? 'Hide Contact' : 'Contact'}
                    </button>
                </div>
            </div>
        </div>
    )
}