import { useEffect, useState } from 'react'
import api from '../api/client'

type Appointment = {
    id: number
    startTime: string
    endTime: string
    status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
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

export default function DoctorTodaysAppointmentsPage() {
    const [appointments, setAppointments] = useState<Appointment[]>([])
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        api.get('/doctors/me/appointments/today')
            .then(res => setAppointments(res.data))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load appointments'))
    }, [])



    const handleComplete = async (id: number) => {
        try {
            await api.put(`/doctors/appointments/${id}/complete`)
            setAppointments(prev => prev.map(apt => 
                apt.id === id ? {...apt, status: 'COMPLETED'} : apt
            ))
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to complete appointment')
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'SCHEDULED': return '#10b981'
            case 'COMPLETED': return '#6366f1'
            case 'CANCELLED': return '#ef4444'
            default: return '#6b7280'
        }
    }

    const scheduledAppointments = appointments.filter(apt => apt.status === 'SCHEDULED')
    const completedAppointments = appointments.filter(apt => apt.status === 'COMPLETED')

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>📅 Today's Appointments</h2>
            
            <div className="grid grid-2">
                <div className="card">
                    <h3 style={{color: '#10b981'}}>📅 Scheduled ({scheduledAppointments.length})</h3>
                    {scheduledAppointments.map(apt => (
                        <div key={apt.id} style={{border: '1px solid #e5e7eb', borderRadius: '8px', padding: '1rem', marginBottom: '1rem'}}>
                            <div><strong>Time:</strong> {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}</div>
                            <div><strong>Patient:</strong> {apt.patient.user.fullName}</div>
                            <div><strong>Phone:</strong> {apt.patient.user.phone}</div>
                            {apt.notes && <div><strong>Notes:</strong> {apt.notes}</div>}
                            <button className="btn btn-primary" style={{width: '100%', marginTop: '0.5rem'}} onClick={() => handleComplete(apt.id)}>
                                Mark Complete
                            </button>
                        </div>
                    ))}
                    {scheduledAppointments.length === 0 && <p>No scheduled appointments</p>}
                </div>

                <div className="card">
                    <h3 style={{color: '#6366f1'}}>✔️ Completed ({completedAppointments.length})</h3>
                    {completedAppointments.map(apt => (
                        <div key={apt.id} style={{border: '1px solid #e5e7eb', borderRadius: '8px', padding: '1rem', marginBottom: '1rem'}}>
                            <div><strong>Time:</strong> {new Date(apt.startTime).toLocaleTimeString()} - {new Date(apt.endTime).toLocaleTimeString()}</div>
                            <div><strong>Patient:</strong> {apt.patient.user.fullName}</div>
                            <div><strong>Phone:</strong> {apt.patient.user.phone}</div>
                            {apt.notes && <div><strong>Notes:</strong> {apt.notes}</div>}
                            <div style={{
                                background: getStatusColor('COMPLETED'),
                                color: 'white',
                                padding: '0.25rem 0.5rem',
                                borderRadius: '4px',
                                fontSize: '0.875rem',
                                textAlign: 'center',
                                marginTop: '0.5rem'
                            }}>
                                Completed
                            </div>
                        </div>
                    ))}
                    {completedAppointments.length === 0 && <p>No completed appointments today</p>}
                </div>
            </div>
        </div>
    )
}