import { FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { validateEmail, validatePassword } from '../utils/validation'

export default function LoginPage() {
  const { login } = useAuth()
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [validationErrors, setValidationErrors] = useState<{[key: string]: string}>({})

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    
    const errors: {[key: string]: string} = {}
    const emailError = validateEmail(email)
    const passwordError = validatePassword(password)
    
    if (emailError) errors.email = emailError
    if (passwordError) errors.password = passwordError
    
    setValidationErrors(errors)
    if (Object.keys(errors).length > 0) return
    
    setLoading(true)
    try {
      const role = await login(email, password)
      nav(role === 'DOCTOR' ? '/doctor/dashboard' : role === 'ADMIN' ? '/admin/dashboard' : '/')
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error?.response?.data?.message ?? 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="form-container">
      <h2>Welcome Back</h2>
      <form onSubmit={onSubmit}>
        <div className="form-group">
          <input 
            className="form-input" 
            placeholder="Email" 
            type="email" 
            value={email} 
            onChange={e=>setEmail(e.target.value)} 
            required 
          />
          {validationErrors.email && <div className="alert alert-error">{validationErrors.email}</div>}
        </div>
        <div className="form-group">
          <input 
            className="form-input" 
            placeholder="Password" 
            type="password" 
            value={password} 
            onChange={e=>setPassword(e.target.value)} 
            required 
          />
          {validationErrors.password && <div className="alert alert-error">{validationErrors.password}</div>}
        </div>
        <button className="btn btn-primary" type="submit" disabled={loading} style={{width: '100%'}}>
          {loading ? <><span className="loading"></span> Signing in...</> : 'Login'}
        </button>
        {error && <div className="alert alert-error">{error}</div>}
      </form>
    </div>
  )
}