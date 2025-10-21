import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/client'

type Appointment = {
    id: number
    startTime: string
    endTime: string
    status: 'PENDING' | 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
    notes?: string
    patientName: string
    doctorName: string
}

export default function DoctorAppointmentsPage() {
    const [pendingAppointments, setPendingAppointments] = useState<Appointment[]>([])
    const [todaysAppointments, setTodaysAppointments] = useState<Appointment[]>([])
    const [appointmentHistory, setAppointmentHistory] = useState<Appointment[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    console.log('DoctorAppointmentsPage rendered', { pendingAppointments, todaysAppointments, error, loading })

    useEffect(() => {
        console.log('Loading appointments...')
        setLoading(true)
        
        Promise.all([
            api.get('/doctors/me/appointments/pending'),
            api.get('/doctors/me/appointments/today'),
            api.get('/doctors/me/appointments/all')
        ])
        .then(([pendingRes, todayRes, historyRes]) => {
            console.log('Raw API responses:', { 
                pendingStatus: pendingRes.status, 
                pendingData: pendingRes.data,
                todayStatus: todayRes.status,
                todayData: todayRes.data,
                historyStatus: historyRes.status,
                historyData: historyRes.data
            })
            // Normalize data to arrays even if API wraps them (e.g., {items: []})
            const toArray = (d: any) => Array.isArray(d)
                ? d
                : Array.isArray(d?.items)
                    ? d.items
                    : Array.isArray(d?.data)
                        ? d.data
                        : []
            setPendingAppointments(toArray(pendingRes.data))
            setTodaysAppointments(toArray(todayRes.data))
            setAppointmentHistory(toArray(historyRes.data))
            setError(null)
            setLoading(false)
        })
        .catch((err: any) => {
            console.error('Error loading appointments:', err)
            console.error('Error details:', err?.response)
            setError(err?.response?.data?.message ?? err?.message ?? 'Failed to load appointments')
            setLoading(false)
        })
    }, [])

    const handleAccept = async (id: number) => {
        try {
            await api.put(`/doctors/appointments/${id}/accept`)
            setPendingAppointments(prev => prev.filter(apt => apt.id !== id))
            // Refresh today's appointments
            const res = await api.get('/doctors/me/appointments/today')
            const toArray = (d: any) => Array.isArray(d) ? d : Array.isArray(d?.items) ? d.items : Array.isArray(d?.data) ? d.data : []
            setTodaysAppointments(toArray(res.data))
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to accept appointment')
        }
    }

    const handleReject = async (id: number) => {
        try {
            await api.put(`/doctors/appointments/${id}/reject`)
            setPendingAppointments(prev => prev.filter(apt => apt.id !== id))
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to reject appointment')
        }
    }

    const handleComplete = async (id: number) => {
        try {
            await api.put(`/doctors/appointments/${id}/complete`)
            setTodaysAppointments(prev => prev.map(apt => 
                apt.id === id ? {...apt, status: 'COMPLETED'} : apt
            ))
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to complete appointment')
        }
    }

    if (loading) return (
        <div className="page-container">
            <div style={{textAlign: 'center', padding: '2rem'}}>Loading appointments...</div>
        </div>
    )

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>📋 Appointments Management</h2>
            
            <div className="grid grid-3">
                <div className="card">
                    <h3 style={{color: '#f59e0b'}}>⏳ Pending Appointments ({pendingAppointments.length})</h3>
                    {pendingAppointments.map(apt => (
                        <div key={apt.id} style={{border: '1px solid #e5e7eb', borderRadius: '8px', padding: '1rem', marginBottom: '1rem'}}>
                            <div><strong>Patient:</strong> {apt.patientName ?? '—'}</div>
                            <div><strong>Time:</strong> {new Date(apt.startTime).toLocaleDateString()} {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}</div>
                            {apt.notes && <div><strong>Notes:</strong> {apt.notes}</div>}
                            <div style={{display: 'flex', gap: '0.5rem', marginTop: '0.5rem'}}>
                                <button className="btn btn-primary" style={{flex: 1}} onClick={() => handleAccept(apt.id)}>
                                    ✅ Accept
                                </button>
                                <button className="btn btn-secondary" style={{flex: 1}} onClick={() => handleReject(apt.id)}>
                                    ❌ Reject
                                </button>
                            </div>
                        </div>
                    ))}
                    {pendingAppointments.length === 0 && !loading && !error && <p>No pending appointments</p>}
                </div>

                <div className="card">
                    <h3 style={{color: '#10b981'}}>📅 Scheduled Appointments ({todaysAppointments.length})</h3>
                    {todaysAppointments.map(apt => (
                        <div key={apt.id} style={{border: '1px solid #e5e7eb', borderRadius: '8px', padding: '1rem', marginBottom: '1rem'}}>
                            <div><strong>Patient:</strong> {apt.patientName ?? '—'}</div>
                            <div><strong>Time:</strong> {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}</div>
                            {apt.notes && <div><strong>Notes:</strong> {apt.notes}</div>}
                            <div style={{
                                background: apt.status === 'COMPLETED' ? '#6366f1' : '#10b981',
                                color: 'white',
                                padding: '0.25rem 0.5rem',
                                borderRadius: '4px',
                                fontSize: '0.875rem',
                                textAlign: 'center',
                                marginTop: '0.5rem'
                            }}>
                                {apt.status === 'COMPLETED' ? '✔️ Completed' : '📅 Scheduled'}
                            </div>
                            {apt.status === 'SCHEDULED' && (
                                <div style={{display: 'flex', gap: '0.5rem', marginTop: '0.5rem'}}>
                                    <button className="btn btn-primary" style={{flex: 1}} onClick={() => handleComplete(apt.id)}>
                                        Mark Complete
                                    </button>
                                    <Link to={`/chat/${apt.id}`} className="btn btn-secondary" style={{flex: 1, textDecoration: 'none', textAlign: 'center'}}>
                                        💬 Chat
                                    </Link>
                                </div>
                            )}
                        </div>
                    ))}
                    {todaysAppointments.length === 0 && !loading && !error && <p>No appointments today</p>}
                </div>

                <div className="card">
                    <h3 style={{color: '#6366f1'}}>📊 All Appointment History ({appointmentHistory.length})</h3>
                    <div style={{maxHeight: '400px', overflowY: 'auto'}}>
                        {appointmentHistory.slice(0, 10).map(apt => (
                            <div key={apt.id} style={{border: '1px solid #e5e7eb', borderRadius: '8px', padding: '1rem', marginBottom: '1rem'}}>
                                <div><strong>Patient:</strong> {apt.patientName ?? '—'}</div>
                                <div><strong>Date:</strong> {new Date(apt.startTime).toLocaleDateString()}</div>
                                <div><strong>Time:</strong> {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}</div>
                                <div><strong>Status:</strong> 
                                    <span style={{
                                        background: apt.status === 'COMPLETED' ? '#6366f1' : 
                                                   apt.status === 'SCHEDULED' ? '#10b981' : 
                                                   apt.status === 'PENDING' ? '#f59e0b' : '#ef4444',
                                        color: 'white',
                                        padding: '0.25rem 0.5rem',
                                        borderRadius: '4px',
                                        fontSize: '0.875rem',
                                        marginLeft: '0.5rem'
                                    }}>
                                        {apt.status}
                                    </span>
                                </div>
                                {apt.notes && <div><strong>Notes:</strong> {apt.notes}</div>}
                            </div>
                        ))}
                        {appointmentHistory.length > 10 && (
                            <div style={{textAlign: 'center', color: '#666', fontSize: '0.875rem'}}>
                                Showing latest 10 appointments
                            </div>
                        )}
                        {appointmentHistory.length === 0 && !loading && !error && <p>No appointment history</p>}
                    </div>
                </div>
            </div>
        </div>
    )
}