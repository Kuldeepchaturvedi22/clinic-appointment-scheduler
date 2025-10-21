import { FormEvent, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import api from '../api/client'
import dayjs from 'dayjs'

type Doctor = { id: number; fullName: string; specialization: string }
type Patient = { id: number }
type TimeSlot = {
    date: string
    time: string
    startTime: string
    endTime: string
    available: boolean
}

export default function BookAppointmentPage() {
    const location = useLocation()
    const [doctors, setDoctors] = useState<Doctor[]>([])
    const [patient, setPatient] = useState<Patient | null>(null)
    const [doctorId, setDoctorId] = useState<number | ''>('')
    const [selectedSlot, setSelectedSlot] = useState<TimeSlot | null>(null)
    const [availableSlots, setAvailableSlots] = useState<TimeSlot[]>([])
    const [notes, setNotes] = useState<string>('')
    const [message, setMessage] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [submitting, setSubmitting] = useState(false)

    useEffect(() => {
        api.get('/doctors').then(res => setDoctors(res.data as Doctor[]))
            .catch(err => setError(err?.response?.data?.message ?? 'Failed to load doctors'))

        // Try to load patient profile; if user is doctor, this may 404 — that’s fine
        api.get('/patients/me').then(res => setPatient(res.data as Patient)).catch(() => {})
        
        const state = location.state as { doctorId?: number } | null
        if (state?.doctorId) {
            setDoctorId(state.doctorId)
            loadAvailableSlots(state.doctorId)
        }
    }, [location.state])

    const loadAvailableSlots = async (docId: number) => {
        try {
            const res = await api.get(`/doctors/${docId}/available-slots`)
            setAvailableSlots(res.data as TimeSlot[])
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to load available slots')
        }
    }

    const handleDoctorChange = (docId: number | '') => {
        setDoctorId(docId)
        setSelectedSlot(null)
        if (docId) {
            loadAvailableSlots(docId as number)
        } else {
            setAvailableSlots([])
        }
    }

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setError(null)
        setMessage(null)
        if (!doctorId) { setError('Please select a doctor'); return }
        if (!patient?.id) { setError('Only patients can book appointments'); return }
        if (!selectedSlot) { setError('Please select a time slot'); return }
        if (!selectedSlot.available) { setError('Selected slot is not available'); return }

        setSubmitting(true)
        try {
            const payload = {
                doctorId: Number(doctorId),
                patientId: patient.id,
                startTime: selectedSlot.startTime,
                endTime: selectedSlot.endTime,
                notes: notes || undefined
            }
            const res = await api.post('/appointments/book', payload)
            const appt = res.data as { id: number }
            setMessage(`Appointment booked with ID: ${appt.id}`)
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } }
            setError(error?.response?.data?.message ?? 'Failed to book')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>📅 Book Appointment</h2>
            <div style={{maxWidth: '500px', margin: '0 auto'}}>
                {location.state?.doctorName && (
                    <div className="alert" style={{background: '#e6fffa', color: '#2c7a7b', border: '1px solid #81e6d9', marginBottom: '1rem'}}>
                        👨‍⚕️ Booking appointment with <strong>Dr. {location.state.doctorName}</strong> ({location.state.specialization})
                    </div>
                )}
                <form onSubmit={onSubmit}>
                    <div className="form-group">
                        <label style={{display: 'block', marginBottom: '0.5rem', fontWeight: '600'}}>Select Doctor</label>
                        <select 
                            className="form-select" 
                            value={doctorId} 
                            onChange={e => handleDoctorChange(e.target.value ? Number(e.target.value) : '')} 
                            required
                        >
                            <option value="">Choose a doctor...</option>
                            {doctors.map(d => (
                                <option key={d.id} value={d.id}>
                                    Dr. {d.fullName || `#${d.id}`} - {d.specialization || 'General Practice'}
                                </option>
                            ))}
                        </select>
                    </div>
                    {doctorId && (
                        <div className="form-group">
                            <label style={{display: 'block', marginBottom: '0.5rem', fontWeight: '600'}}>Available Time Slots</label>
                            <div style={{display: 'grid', gap: '0.5rem', maxHeight: '300px', overflowY: 'auto'}}>
                                {availableSlots.map((slot, index) => (
                                    <div 
                                        key={index}
                                        onClick={() => slot.available && setSelectedSlot(slot)}
                                        style={{
                                            padding: '0.75rem',
                                            border: '2px solid',
                                            borderColor: selectedSlot === slot ? '#667eea' : slot.available ? '#e2e8f0' : '#fed7d7',
                                            borderRadius: '8px',
                                            cursor: slot.available ? 'pointer' : 'not-allowed',
                                            background: selectedSlot === slot ? '#f0f4ff' : slot.available ? 'white' : '#fed7d7',
                                            color: slot.available ? '#333' : '#666',
                                            opacity: slot.available ? 1 : 0.6
                                        }}
                                    >
                                        <div style={{fontWeight: '600'}}>{slot.date}</div>
                                        <div>{slot.time}</div>
                                        <div style={{fontSize: '0.875rem', color: slot.available ? '#10b981' : '#ef4444'}}>
                                            {slot.available ? '✅ Available' : '❌ Booked'}
                                        </div>
                                    </div>
                                ))}
                                {availableSlots.length === 0 && doctorId && (
                                    <div style={{textAlign: 'center', color: '#666', padding: '1rem'}}>No available slots</div>
                                )}
                            </div>
                        </div>
                    )}
                    <div className="form-group">
                        <label style={{display: 'block', marginBottom: '0.5rem', fontWeight: '600'}}>Notes (Optional)</label>
                        <textarea 
                            className="form-textarea" 
                            placeholder="Any specific concerns or notes for the doctor..." 
                            value={notes} 
                            onChange={e=>setNotes(e.target.value)} 
                        />
                    </div>
                    <button className="btn btn-primary" type="submit" disabled={submitting} style={{width: '100%'}}>
                        {submitting ? <><span className="loading"></span> Booking...</> : '📅 Book Appointment'}
                    </button>
                    {message && <div className="alert alert-success">{message}</div>}
                    {error && <div className="alert alert-error">{error}</div>}
                </form>
                {!patient && (
                    <div className="alert" style={{background: '#f7fafc', color: '#4a5568', border: '1px solid #e2e8f0', marginTop: '1rem'}}>
                        💡 Tip: Login as a Patient to book appointments.
                    </div>
                )}
            </div>
        </div>
    )
}