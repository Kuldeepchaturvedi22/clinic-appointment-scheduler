import { useEffect, useState } from 'react'
import api from '../api/client'

type Appointment = {
    id: number
    startTime: string
    endTime: string
    status: 'PENDING' | 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
    notes: string
    doctorName: string
    doctorSpecialization: string
    patientName: string
    patientEmail: string
}

export default function AdminAppointmentsPage() {
    const [appointments, setAppointments] = useState<Appointment[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        api.get('/admin/appointments')
            .then(res => {
                setAppointments(res.data)
                setLoading(false)
            })
            .catch(err => {
                setError(err?.response?.data?.message ?? 'Failed to load appointments')
                setLoading(false)
            })
    }, [])

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
                <p>Loading appointments...</p>
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
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>📅 All Appointments</h2>
            
            <div className="card" style={{marginBottom: '2rem', textAlign: 'center', background: '#f7fafc'}}>
                <h4 style={{color: '#4a5568', marginBottom: '0.5rem'}}>Total Appointments: {appointments.length}</h4>
                <p style={{color: '#666', margin: 0}}>
                    {appointments.filter(a => a.status === 'PENDING').length} Pending • 
                    {appointments.filter(a => a.status === 'SCHEDULED').length} Scheduled • 
                    {appointments.filter(a => a.status === 'COMPLETED').length} Completed • 
                    {appointments.filter(a => a.status === 'CANCELLED').length} Cancelled
                </p>
            </div>

            {appointments.length === 0 ? (
                <div className="card" style={{textAlign: 'center', padding: '3rem'}}>
                    <h3>No appointments found</h3>
                    <p>No appointments have been booked yet.</p>
                </div>
            ) : (
                <div className="grid">
                    {appointments.map(apt => (
                        <div key={apt.id} className="card">
                            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem'}}>
                                <div>
                                    <h4 style={{margin: '0 0 0.5rem 0'}}>Appointment #{apt.id}</h4>
                                    <div style={{color: '#666', fontSize: '0.875rem'}}>
                                        {new Date(apt.startTime).toLocaleDateString()} • {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}
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
                                <div><strong>👨⚕️ Doctor:</strong> Dr. {apt.doctorName} ({apt.doctorSpecialization})</div>
                                <div><strong>👤 Patient:</strong> {apt.patientName}</div>
                                <div><strong>📧 Patient Email:</strong> {apt.patientEmail}</div>
                                {apt.notes && (
                                    <div>
                                        <strong>📝 Notes:</strong>
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