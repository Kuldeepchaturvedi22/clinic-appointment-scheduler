import { useEffect, useState } from 'react'
import api from '../api/client'

type Appointment = {
    id: number
    startTime: string
    endTime: string
    status: 'COMPLETED'
    notes?: string
    patient: {
        id: number
        user: {
            fullName: string
            email: string
            phone: string
        }
    }
}

export default function DoctorAppointmentHistoryPage() {
    const [appointments, setAppointments] = useState<Appointment[]>([])
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        api.get('/doctors/me/appointments/history')
            .then(res => setAppointments(res.data))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load appointment history'))
    }, [])

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>📊 Appointment History</h2>
            
            {appointments.length === 0 ? (
                <div className="card" style={{textAlign: 'center', padding: '3rem'}}>
                    <h3>No completed appointments yet</h3>
                    <p>Your completed appointments will appear here.</p>
                </div>
            ) : (
                <div className="grid">
                    {appointments.map(apt => (
                        <div key={apt.id} className="card">
                            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem'}}>
                                <div>
                                    <h4 style={{margin: '0 0 0.5rem 0'}}>{apt.patient.user.fullName}</h4>
                                    <div style={{color: '#666', fontSize: '0.875rem'}}>
                                        {new Date(apt.startTime).toLocaleDateString()} • {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}
                                    </div>
                                </div>
                                <div style={{
                                    background: '#6366f1',
                                    color: 'white',
                                    padding: '0.25rem 0.75rem',
                                    borderRadius: '20px',
                                    fontSize: '0.875rem'
                                }}>
                                    ✔️ Completed
                                </div>
                            </div>
                            
                            <div style={{display: 'grid', gap: '0.5rem', marginBottom: '1rem'}}>
                                <div><strong>Email:</strong> {apt.patient.user.email}</div>
                                <div><strong>Phone:</strong> {apt.patient.user.phone}</div>
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
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}