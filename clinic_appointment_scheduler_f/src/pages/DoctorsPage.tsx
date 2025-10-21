import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/client'

type Doctor = {
    id: number
    fullName: string
    email: string
    phone: string
    specialization: string
    gender?: string
    averageRating?: number
}

export default function DoctorsPage() {
    const [doctors, setDoctors] = useState<Doctor[]>([])
    const [allDoctors, setAllDoctors] = useState<Doctor[]>([])
    const [searchTerm, setSearchTerm] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [contactInfo, setContactInfo] = useState<{[key: number]: boolean}>({})
    const [showRatings, setShowRatings] = useState<{[key: number]: boolean}>({})
    const [ratings, setRatings] = useState<{[key: number]: any[]}>({})
    const navigate = useNavigate()

    useEffect(() => {
        api.get('/doctors')
            .then(res => {
                console.log('Doctors API response:', res.data)
                const doctorData = res.data as Doctor[]
                setDoctors(doctorData)
                setAllDoctors(doctorData)
            })
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load doctors'))
    }, [])

    const handleSearch = async (term: string) => {
        setSearchTerm(term)
        if (!term.trim()) {
            setDoctors(allDoctors)
            return
        }
        try {
            const res = await api.get(`/doctors?search=${encodeURIComponent(term)}`)
            setDoctors(res.data as Doctor[])
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to search doctors')
        }
    }
    
    const loadRatings = async (doctorId: number) => {
        try {
            const res = await api.get(`/ratings/doctor/${doctorId}`)
            setRatings(prev => ({...prev, [doctorId]: res.data}))
        } catch (err: any) {
            console.error('Failed to load ratings:', err)
        }
    }
    
    const toggleRatings = (doctorId: number) => {
        const isShowing = showRatings[doctorId]
        setShowRatings(prev => ({...prev, [doctorId]: !isShowing}))
        if (!isShowing && !ratings[doctorId]) {
            loadRatings(doctorId)
        }
    }

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>👨⚕️ Our Doctors</h2>
            
            <div style={{maxWidth: '500px', margin: '0 auto 2rem auto'}}>
                <div className="form-group">
                    <input 
                        className="form-input" 
                        type="text" 
                        placeholder="Search doctors by name or specialization..." 
                        value={searchTerm}
                        onChange={(e) => handleSearch(e.target.value)}
                        style={{width: '100%', padding: '1rem', fontSize: '1rem'}}
                    />
                </div>
            </div>
            
            {error && <div className="alert alert-error">{error}</div>}
            <div className="grid grid-3">
                {doctors.map(d => (
                    <div key={d.id} className="card">
                        <div style={{textAlign: 'center'}}>
                            <div style={{fontSize: '3rem', marginBottom: '1rem'}}>👨⚕️</div>
                            <h3 style={{margin: '0 0 0.5rem 0', color: '#333'}}>Dr. {d.fullName}</h3>
                            <p style={{color: '#666', margin: '0.25rem 0'}}>ID: #{d.id}</p>
                            <p style={{color: '#667eea', margin: '0.25rem 0', fontWeight: '600'}}>{d.specialization || 'General Practice'}</p>
                            <div style={{display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '1rem'}}>
                                <span style={{color: '#f59e0b', fontSize: '1.2rem'}}>⭐</span>
                                <span style={{fontWeight: '600', color: '#333'}}>
                                    {d.averageRating && d.averageRating > 0 ? d.averageRating.toFixed(1) : '0.0'}
                                </span>
                            </div>
                            
                            {contactInfo[d.id] && (
                                <div style={{background: '#f7fafc', padding: '0.75rem', borderRadius: '6px', marginBottom: '1rem', fontSize: '0.9rem'}}>
                                    <p style={{margin: '0.25rem 0'}}><strong>📧 Email:</strong> {d.email || 'Not available'}</p>
                                    <p style={{margin: '0.25rem 0'}}><strong>📞 Phone:</strong> {d.phone || 'Not available'}</p>
                                </div>
                            )}
                            
                            <div style={{display: 'grid', gap: '0.5rem'}}>
                                <button 
                                    className="btn btn-primary" 
                                    style={{width: '100%'}}
                                    onClick={() => navigate('/appointments/book', { state: { doctorId: d.id, doctorName: d.fullName, specialization: d.specialization } })}
                                >
                                    📅 Book Appointment
                                </button>
                                <button 
                                    className="btn btn-secondary" 
                                    style={{width: '100%'}}
                                    onClick={() => setContactInfo(prev => ({...prev, [d.id]: !prev[d.id]}))}
                                >
                                    📞 {contactInfo[d.id] ? 'Hide Contact' : 'Contact'}
                                </button>
                                <button 
                                    className="btn btn-secondary" 
                                    style={{width: '100%'}}
                                    onClick={() => toggleRatings(d.id)}
                                >
                                    💬 {showRatings[d.id] ? 'Hide Reviews' : 'View Reviews'}
                                </button>
                            </div>
                            
                            {showRatings[d.id] && ratings[d.id] && (
                                <div style={{marginTop: '1rem'}}>
                                    {ratings[d.id].length === 0 ? (
                                        <div style={{textAlign: 'center', color: '#666', padding: '1rem'}}>No reviews yet</div>
                                    ) : (
                                        ratings[d.id].map((rating: any) => (
                                            <div key={rating.id} style={{
                                                background: '#f9fafb',
                                                padding: '0.75rem',
                                                borderRadius: '6px',
                                                marginBottom: '0.5rem',
                                                border: '1px solid #e5e7eb'
                                            }}>
                                                <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem'}}>
                                                    <div style={{fontWeight: '600', fontSize: '0.875rem'}}>{rating.patientName}</div>
                                                    <div style={{color: '#f59e0b'}}>{'⭐'.repeat(rating.stars)}</div>
                                                </div>
                                                {rating.comment && (
                                                    <div style={{fontSize: '0.875rem', color: '#666'}}>{rating.comment}</div>
                                                )}
                                            </div>
                                        ))
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
            {doctors.length === 0 && !error && (
                <div style={{textAlign: 'center', color: '#666', marginTop: '2rem'}}>
                    <div className="loading"></div>
                    <p>Loading doctors...</p>
                </div>
            )}
        </div>
    )
}