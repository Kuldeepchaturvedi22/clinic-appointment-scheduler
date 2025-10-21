import { Route, Routes, Navigate, Link, useNavigate } from 'react-router-dom'
import './App.css'
import { useAuth } from './context/AuthContext'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DoctorsPage from './pages/DoctorsPage'
import DoctorDetailPage from './pages/DoctorDetailPage'
import PatientMePage from './pages/PatientMePage'
import DoctorMePage from './pages/DoctorMePage'
import DoctorDashboardPage from './pages/DoctorDashboardPage'
import DoctorAppointmentsPage from './pages/DoctorAppointmentsPage'
import BookAppointmentPage from './pages/BookAppointmentPage'
import PatientAppointmentHistoryPage from './pages/PatientAppointmentHistoryPage'
import PatientFeaturesPage from './pages/PatientFeaturesPage'
import DoctorFeaturesPage from './pages/DoctorFeaturesPage'
import ChatPage from './pages/ChatPage'
import HelpChatPage from './pages/HelpChatPage'
import AdminHelpChatPage from './pages/AdminHelpChatPage'
import AdminDashboardPage from './pages/AdminDashboardPage'
import AdminUsersPage from './pages/AdminUsersPage'
import AdminAppointmentsPage from './pages/AdminAppointmentsPage'
import type {JSX} from "react"

function ProtectedRoute({ children }: { children: JSX.Element }) {
    const { token, role } = useAuth()
    if (!token) {
        return <Navigate to="/login" replace />
    }
    return children
}

function DoctorRedirect() {
    const { role } = useAuth()
    if (role === 'DOCTOR') {
        return <Navigate to="/doctor/features" replace />
    }
    if (role === 'ADMIN') {
        return <Navigate to="/admin/dashboard" replace />
    }
    return <HomePage />
}

function AppHeader() {
    const { token, role, logout } = useAuth()
    const nav = useNavigate()
    return (
        <header className="header">
            <div className="header-content">
                <div className="nav-links">
                    <Link to={token && role === 'DOCTOR' ? '/doctor/features' : token && role === 'ADMIN' ? '/admin/dashboard' : '/'}>🏥 ClinicCare</Link>
                    {token && role === 'PATIENT' && <Link to="/doctors">Find Doctors</Link>}
                    {token && role === 'PATIENT' && <Link to="/me/patient">My Profile</Link>}
                    {token && role === 'PATIENT' && <Link to="/appointments/book">Book Appointment</Link>}
                    {token && role === 'PATIENT' && <Link to="/help-chat">Help & Support</Link>}
                    {token && role === 'DOCTOR' && <Link to="/doctor/dashboard">Dashboard</Link>}
                    {token && role === 'DOCTOR' && <Link to="/doctor/profile">Profile</Link>}
                    {token && role === 'DOCTOR' && <Link to="/doctor/appointments">Appointments</Link>}
                    {token && role === 'DOCTOR' && <Link to="/help-chat">Help & Support</Link>}
                    {token && role === 'ADMIN' && <Link to="/admin/dashboard">Admin Dashboard</Link>}
                    {token && role === 'ADMIN' && <Link to="/admin/users">Manage Users</Link>}
                    {token && role === 'ADMIN' && <Link to="/admin/appointments">All Appointments</Link>}
                    {token && role === 'ADMIN' && <Link to="/admin/help-chat">Help Chat</Link>}
                </div>
                <div className="auth-buttons">
                    {!token && <Link to="/login" className="btn btn-secondary">Login</Link>}
                    {!token && <Link to="/register" className="btn btn-primary">Register</Link>}
                    {token && <button className="btn btn-secondary" onClick={() => { logout(); nav('/'); }}>Logout</button>}
                </div>
            </div>
        </header>
    )
}

function HomePage() {
    const { token, role, userName } = useAuth()
    
    const getStartedLink = () => {
        if (!token) return '/register'
        return role === 'PATIENT' ? '/patient/features' : role === 'DOCTOR' ? '/doctor/features' : '/admin/dashboard'
    }
    
    return (
        <div className="hero">
            <h1>{token && userName ? `Welcome, ${userName}!` : 'Welcome to ClinicCare'}</h1>
            <p>Your trusted partner for seamless healthcare appointments</p>
            <div style={{display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap'}}>
                <Link to="/doctors" className="btn btn-primary" style={{minWidth: '140px'}}>Find Doctors</Link>
                <Link to={getStartedLink()} className="btn btn-secondary" style={{minWidth: '140px'}}>Get Started</Link>
            </div>
        </div>
    )
}

function AppFooter() {
    return (
        <footer style={{
            background: 'rgba(255, 255, 255, 0.95)',
            backdropFilter: 'blur(10px)',
            padding: '2rem',
            marginTop: 'auto',
            borderTop: '1px solid #e2e8f0'
        }}>
            <div style={{maxWidth: '1200px', margin: '0 auto'}}>
                <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '2rem', marginBottom: '2rem'}}>
                    <div>
                        <h3 style={{color: '#667eea', marginBottom: '1rem'}}>🏥 ClinicCare</h3>
                        <p style={{color: '#666', lineHeight: '1.6'}}>Your trusted partner for seamless healthcare appointments. Connecting patients with qualified doctors for better health outcomes.</p>
                    </div>
                    <div>
                        <h4 style={{color: '#333', marginBottom: '1rem'}}>Quick Links</h4>
                        <div style={{display: 'flex', flexDirection: 'column', gap: '0.5rem'}}>
                            <Link to="/doctors" style={{color: '#666', textDecoration: 'none'}}>Find Doctors</Link>
                            <Link to="/appointments/book" style={{color: '#666', textDecoration: 'none'}}>Book Appointment</Link>
                            <Link to="/register" style={{color: '#666', textDecoration: 'none'}}>Register</Link>
                        </div>
                    </div>
                    <div>
                        <h4 style={{color: '#333', marginBottom: '1rem'}}>Contact Info</h4>
                        <div style={{color: '#666', lineHeight: '1.8'}}>
                            <div>📧 support@cliniccare.com</div>
                            <div>📞 +1 (555) 123-4567</div>
                            <div>📍 123 Healthcare Ave, Medical City</div>
                        </div>
                    </div>
                </div>
                <div style={{textAlign: 'center', paddingTop: '1rem', borderTop: '1px solid #e2e8f0', color: '#666'}}>
                    <p>© 2024 ClinicCare. All rights reserved. | Privacy Policy | Terms of Service</p>
                </div>
            </div>
        </footer>
    )
}

export default function App() {
    return (
        <div className="app-container">
            <AppHeader />
            <main className="main-content">
                <Routes>
                    <Route path="/" element={<DoctorRedirect />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />

                    <Route path="/doctors" element={
                        <ProtectedRoute>
                            <DoctorsPage />
                        </ProtectedRoute>
                    } />
                    <Route path="/doctors/:id" element={
                        <ProtectedRoute>
                            <DoctorDetailPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/me/patient" element={
                        <ProtectedRoute>
                            <PatientMePage />
                        </ProtectedRoute>
                    } />
                    <Route path="/me/patient/appointments/history" element={
                        <ProtectedRoute>
                            <PatientAppointmentHistoryPage />
                        </ProtectedRoute>
                    } />
                    <Route path="/doctor/dashboard" element={
                        <ProtectedRoute>
                            <DoctorDashboardPage />
                        </ProtectedRoute>
                    } />
                    <Route path="/doctor/profile" element={
                        <ProtectedRoute>
                            <DoctorMePage />
                        </ProtectedRoute>
                    } />
                    <Route path="/doctor/appointments" element={
                        <ProtectedRoute>
                            <DoctorAppointmentsPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/appointments/book" element={
                        <ProtectedRoute>
                            <BookAppointmentPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/patient/features" element={
                        <ProtectedRoute>
                            <PatientFeaturesPage />
                        </ProtectedRoute>
                    } />
                    <Route path="/doctor/features" element={
                        <ProtectedRoute>
                            <DoctorFeaturesPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/chat/:appointmentId" element={
                        <ProtectedRoute>
                            <ChatPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/help-chat" element={
                        <ProtectedRoute>
                            <HelpChatPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/admin/help-chat" element={
                        <ProtectedRoute>
                            <AdminHelpChatPage />
                        </ProtectedRoute>
                    } />

                    <Route path="/admin/dashboard" element={
                        <ProtectedRoute>
                            <AdminDashboardPage />
                        </ProtectedRoute>
                    } />
                    <Route path="/admin/users" element={
                        <ProtectedRoute>
                            <AdminUsersPage />
                        </ProtectedRoute>
                    } />
                    <Route path="/admin/appointments" element={
                        <ProtectedRoute>
                            <AdminAppointmentsPage />
                        </ProtectedRoute>
                    } />

                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </main>
            <AppFooter />
        </div>
    )
}