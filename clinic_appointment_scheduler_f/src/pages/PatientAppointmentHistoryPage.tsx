import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/client'

type Appointment = {
    id: number
    startTime: string
    endTime: string
    status: 'PENDING' | 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
    notes?: string
    doctorName: string
    doctorSpecialization: string
}

export default function PatientAppointmentHistoryPage() {
    const [appointments, setAppointments] = useState<Appointment[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const [ratingModal, setRatingModal] = useState<{appointmentId: number, doctorName: string} | null>(null)
    const [rating, setRating] = useState(5)
    const [comment, setComment] = useState('')

    useEffect(() => {
        api.get('/patients/me/appointments/history')
            .then(res => {
                setAppointments(res.data)
                setLoading(false)
            })
            .catch(err => {
                setError(err?.response?.data?.message ?? 'Failed to load appointment history')
                setLoading(false)
            })
    }, [])
    
    const handleRate = async () => {
        if (!ratingModal) return
        try {
            await api.post(`/ratings/appointment/${ratingModal.appointmentId}`, {
                stars: rating,
                comment: comment
            })
            setRatingModal(null)
            setRating(5)
            setComment('')
            alert('Rating submitted successfully!')
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to submit rating')
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'PENDING': return '#f59e0b'
            case 'SCHEDULED': return '#10b981'
            case 'COMPLETED': return '#6366f1'
            case 'CANCELLED': return '#ef4444'
            default: return '#6b7280'
        }
    }

    const getStatusIcon = (status: string) => {
        switch (status) {
            case 'PENDING': return '⏳'
            case 'SCHEDULED': return '📅'
            case 'COMPLETED': return '✅'
            case 'CANCELLED': return '❌'
            default: return '❓'
        }
    }

    if (loading) return (
        <div className="page-container">
            <div style={{textAlign: 'center', padding: '2rem'}}>
                <div className="loading"></div>
                <p>Loading appointment history...</p>
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
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>📊 My Appointment History</h2>
            
            {appointments.length === 0 ? (
                <div className="card" style={{textAlign: 'center', padding: '3rem'}}>
                    <h3>No appointments found</h3>
                    <p>You haven't booked any appointments yet.</p>
                </div>
            ) : (
                <div className="grid">
                    {appointments.map(apt => (
                        <div key={apt.id} className="card">
                            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem'}}>
                                <div>
                                    <h4 style={{margin: '0 0 0.5rem 0'}}>Dr. {apt.doctorName}</h4>
                                    <div style={{color: '#666', fontSize: '0.875rem'}}>
                                        {apt.doctorSpecialization}
                                    </div>
                                </div>
                                <div style={{
                                    background: getStatusColor(apt.status),
                                    color: 'white',
                                    padding: '0.25rem 0.75rem',
                                    borderRadius: '20px',
                                    fontSize: '0.875rem'
                                }}>
                                    {getStatusIcon(apt.status)} {apt.status}
                                </div>
                            </div>
                            
                            <div style={{display: 'grid', gap: '0.5rem', marginBottom: '1rem'}}>
                                <div><strong>Date:</strong> {new Date(apt.startTime).toLocaleDateString()}</div>
                                <div><strong>Time:</strong> {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}</div>
                                <div><strong>Appointment ID:</strong> #{apt.id}</div>
                                {apt.notes && (
                                    <div>
                                        <strong>Notes:</strong>
                                        <div style={{
                                            background: '#f9fafb',
                                            padding: '0.75rem',
                                            borderRadius: '6px',
                                            marginTop: '0.25rem',
                                            border: '1px solid #e5e7eb'
                                        }}>
                                            {apt.notes}
                                        </div>
                                    </div>
                                )}
                                {apt.status === 'SCHEDULED' && (
                                    <div style={{marginTop: '1rem'}}>
                                        <Link to={`/chat/${apt.id}`} className="btn btn-primary" style={{textDecoration: 'none'}}>
                                            💬 Chat with Doctor
                                        </Link>
                                    </div>
                                )}
                                {(apt.status === 'SCHEDULED' || apt.status === 'COMPLETED') && (
                                    <div style={{marginTop: '1rem'}}>
                                        <button 
                                            className="btn btn-secondary" 
                                            onClick={() => setRatingModal({appointmentId: apt.id, doctorName: apt.doctorName})}
                                        >
                                            ⭐ Rate Doctor
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
            
            {ratingModal && (
                <div className="modal-overlay" onClick={() => setRatingModal(null)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <h3>Rate Dr. {ratingModal.doctorName}</h3>
                        <div style={{marginBottom: '1rem'}}>
                            <label style={{display: 'block', marginBottom: '0.5rem'}}>Rating:</label>
                            <div style={{display: 'flex', gap: '0.5rem'}}>
                                {[1,2,3,4,5].map(star => (
                                    <button
                                        key={star}
                                        type="button"
                                        onClick={() => setRating(star)}
                                        style={{
                                            background: 'none',
                                            border: 'none',
                                            fontSize: '2rem',
                                            cursor: 'pointer',
                                            color: star <= rating ? '#f59e0b' : '#d1d5db'
                                        }}
                                    >
                                        ⭐
                                    </button>
                                ))}
                            </div>
                            <div style={{textAlign: 'center', marginTop: '0.5rem', fontSize: '0.875rem', color: '#666'}}>
                                {rating} star{rating !== 1 ? 's' : ''}
                            </div>
                        </div>
                        <textarea
                            className="form-textarea"
                            placeholder="Optional comment..."
                            value={comment}
                            onChange={e => setComment(e.target.value)}
                        />
                        <div className="form-actions">
                            <button className="btn btn-secondary" onClick={() => setRatingModal(null)}>Cancel</button>
                            <button className="btn btn-primary" onClick={handleRate}>Submit Rating</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}