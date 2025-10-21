import { useEffect, useState } from 'react'
import api from '../api/client'

type DashboardData = {
    doctorId: number
    fullName: string
    specialization: string
    status: string
    todaysAppointments: number
    pendingAppointments: number
    completedAppointments: number
}

export default function DoctorDashboardPage() {
    const [data, setData] = useState<DashboardData | null>(null)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        api.get('/doctors/me/dashboard')
            .then(res => setData(res.data))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load dashboard'))
    }, [])

    if (error) return (
        <div className="page-container">
            <div className="alert alert-error">{error}</div>
        </div>
    )
    
    if (!data) return (
        <div className="page-container" style={{textAlign: 'center'}}>
            <div className="loading"></div>
            <p>Loading dashboard...</p>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>👨⚕️ Doctor Dashboard</h2>
            
            <div className="card" style={{marginBottom: '2rem', textAlign: 'center'}}>
                <h3 style={{color: '#667eea', margin: '0 0 1rem 0'}}>Welcome, Dr. {data.fullName}</h3>
                <p><strong>Specialization:</strong> {data.specialization}</p>
                <p><strong>Status:</strong> <span style={{color: '#10b981'}}>{data.status}</span></p>
            </div>

            <div className="grid grid-3">
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '2.5rem', marginBottom: '0.5rem'}}>📅</div>
                    <h3 style={{color: '#10b981', margin: '0 0 0.5rem 0'}}>{data.todaysAppointments}</h3>
                    <p>Today's Appointments</p>
                </div>
                
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '2.5rem', marginBottom: '0.5rem'}}>⏳</div>
                    <h3 style={{color: '#f59e0b', margin: '0 0 0.5rem 0'}}>{data.pendingAppointments}</h3>
                    <p>Pending Appointments</p>
                </div>
                
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '2.5rem', marginBottom: '0.5rem'}}>✅</div>
                    <h3 style={{color: '#6366f1', margin: '0 0 0.5rem 0'}}>{data.completedAppointments}</h3>
                    <p>Completed Appointments</p>
                </div>
            </div>
        </div>
    )
}