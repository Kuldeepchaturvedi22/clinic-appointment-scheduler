import { FormEvent, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { validateEmail, validatePassword, validatePhone, validateName, validateDate } from '../utils/validation'

type Mode = 'PATIENT' | 'DOCTOR'

export default function RegisterPage() {
    const { registerPatient, registerDoctor } = useAuth()
    const [mode, setMode] = useState<Mode>('PATIENT')
    const [form, setForm] = useState({
        email: '',
        password: '',
        fullName: '',
        phone: '',
        dateOfBirth: '',
        gender: '',
        specialization: ''
    })
    const [message, setMessage] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)
    const [validationErrors, setValidationErrors] = useState<{[key: string]: string}>({})

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault()
        setMessage(null)
        setError(null)
        
        const errors: {[key: string]: string} = {}
        const nameError = validateName(form.fullName)
        const emailError = validateEmail(form.email)
        const phoneError = validatePhone(form.phone)
        const passwordError = validatePassword(form.password)
        
        if (nameError) errors.fullName = nameError
        if (emailError) errors.email = emailError
        if (phoneError) errors.phone = phoneError
        if (passwordError) errors.password = passwordError
        
        if (mode === 'PATIENT') {
            const dateError = validateDate(form.dateOfBirth)
            if (dateError) errors.dateOfBirth = dateError
        } else {
            if (!form.specialization) errors.specialization = 'Specialization is required'
        }
        
        setValidationErrors(errors)
        if (Object.keys(errors).length > 0) return
        
        setLoading(true)
        try {
            if (mode === 'PATIENT') {
                const res = await registerPatient({
                    email: form.email,
                    password: form.password,
                    fullName: form.fullName,
                    phone: form.phone,
                    dateOfBirth: form.dateOfBirth,
                    gender: form.gender || undefined
                })
                setMessage(res.message)
            } else {
                const res = await registerDoctor({
                    email: form.email,
                    password: form.password,
                    fullName: form.fullName,
                    phone: form.phone,
                    specialization: form.specialization
                })
                setMessage(res.message)
            }
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } }
            setError(error?.response?.data?.message ?? 'Registration failed')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="form-container" style={{maxWidth: '500px'}}>
            <h2>Join ClinicCare</h2>
            <div className="mode-toggle">
                <button 
                    type="button"
                    className={mode === 'PATIENT' ? 'active' : ''}
                    onClick={()=>setMode('PATIENT')}
                >
                    👤 Patient
                </button>
                <button 
                    type="button"
                    className={mode === 'DOCTOR' ? 'active' : ''}
                    onClick={()=>setMode('DOCTOR')}
                >
                    👨‍⚕️ Doctor
                </button>
            </div>

            <form onSubmit={onSubmit}>
                <div className="form-group">
                    <input 
                        className="form-input" 
                        placeholder="Full name" 
                        value={form.fullName} 
                        onChange={e=>setForm({...form, fullName: e.target.value})} 
                        required 
                    />
                    {validationErrors.fullName && <div className="alert alert-error">{validationErrors.fullName}</div>}
                </div>
                <div className="form-group">
                    <input 
                        className="form-input" 
                        placeholder="Email" 
                        type="email" 
                        value={form.email} 
                        onChange={e=>setForm({...form, email: e.target.value})} 
                        required 
                    />
                    {validationErrors.email && <div className="alert alert-error">{validationErrors.email}</div>}
                </div>
                <div className="form-group">
                    <input 
                        className="form-input" 
                        placeholder="Phone (+country code)" 
                        value={form.phone} 
                        onChange={e=>setForm({...form, phone: e.target.value})} 
                        required 
                    />
                    {validationErrors.phone && <div className="alert alert-error">{validationErrors.phone}</div>}
                </div>
                <div className="form-group">
                    <input 
                        className="form-input" 
                        placeholder="Password" 
                        type="password" 
                        value={form.password} 
                        onChange={e=>setForm({...form, password: e.target.value})} 
                        required 
                    />
                    {validationErrors.password && <div className="alert alert-error">{validationErrors.password}</div>}
                </div>

                {mode === 'PATIENT' && (
                    <>
                        <div className="form-group">
                            <input 
                                className="form-input" 
                                placeholder="Date of birth" 
                                type="date" 
                                value={form.dateOfBirth} 
                                onChange={e=>setForm({...form, dateOfBirth: e.target.value})} 
                                required 
                            />
                            {validationErrors.dateOfBirth && <div className="alert alert-error">{validationErrors.dateOfBirth}</div>}
                        </div>
                        <div className="form-group">
                            <input 
                                className="form-input" 
                                placeholder="Gender (optional)" 
                                value={form.gender} 
                                onChange={e=>setForm({...form, gender: e.target.value})} 
                            />
                        </div>
                    </>
                )}

                {mode === 'DOCTOR' && (
                    <div className="form-group">
                        <input 
                            className="form-input" 
                            placeholder="Specialization" 
                            value={form.specialization} 
                            onChange={e=>setForm({...form, specialization: e.target.value})} 
                            required 
                        />
                        {validationErrors.specialization && <div className="alert alert-error">{validationErrors.specialization}</div>}
                    </div>
                )}

                <button className="btn btn-primary" type="submit" disabled={loading} style={{width: '100%'}}>
                    {loading ? <><span className="loading"></span> Creating account...</> : 'Create Account'}
                </button>
                {message && <div className="alert alert-success">{message}</div>}
                {error && <div className="alert alert-error">{error}</div>}
            </form>
        </div>
    )
}