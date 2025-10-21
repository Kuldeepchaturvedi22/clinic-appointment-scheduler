import { Link } from 'react-router-dom'

export default function DoctorFeaturesPage() {
    return (
        <div className="page-container">
            <div style={{textAlign: 'center', marginBottom: '3rem'}}>
                <h1 style={{color: '#667eea', marginBottom: '1rem'}}>👨⚕️ Welcome, Doctor!</h1>
                <p style={{fontSize: '1.2rem', color: '#666'}}>Streamline your practice with ClinicCare's comprehensive appointment management system</p>
            </div>

            <div className="grid grid-2" style={{marginBottom: '3rem'}}>
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>📋</div>
                    <h3 style={{color: '#667eea'}}>Appointment Management</h3>
                    <p>Efficiently manage all your appointments in one place. Accept, reject, or complete appointments with just a click. Track pending, scheduled, and completed appointments.</p>
                    <Link to="/doctor/appointments" className="btn btn-primary" style={{marginTop: '1rem'}}>Manage Appointments</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>📊</div>
                    <h3 style={{color: '#667eea'}}>Dashboard Overview</h3>
                    <p>Get a comprehensive view of your practice with real-time statistics. Monitor today's appointments, pending requests, and completed consultations.</p>
                    <Link to="/doctor/dashboard" className="btn btn-primary" style={{marginTop: '1rem'}}>View Dashboard</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>👤</div>
                    <h3 style={{color: '#667eea'}}>Profile Management</h3>
                    <p>Keep your professional profile updated. Manage your contact information, specialization, and ensure patients can reach you easily.</p>
                    <Link to="/doctor/profile" className="btn btn-primary" style={{marginTop: '1rem'}}>Edit Profile</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>⏰</div>
                    <h3 style={{color: '#667eea'}}>Flexible Scheduling</h3>
                    <p>Patients can book 2-hour slots from 9 AM to 8 PM. You have full control to accept or reject appointment requests based on your availability.</p>
                    <Link to="/doctor/appointments" className="btn btn-secondary" style={{marginTop: '1rem'}}>View Requests</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>💬</div>
                    <h3 style={{color: '#667eea'}}>Patient Communication</h3>
                    <p>Chat directly with patients during scheduled appointments. Provide guidance, answer questions, and maintain continuous care communication.</p>
                    <Link to="/doctor/appointments" className="btn btn-secondary" style={{marginTop: '1rem'}}>Access Chats</Link>
                </div>
            </div>

            <div className="card" style={{background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white', textAlign: 'center'}}>
                <h2 style={{color: 'white', marginBottom: '1rem'}}>How to Manage Your Practice</h2>
                <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '2rem', marginTop: '2rem'}}>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>1️⃣</div>
                        <h4>Review Requests</h4>
                        <p>Check pending appointment requests from patients</p>
                    </div>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>2️⃣</div>
                        <h4>Accept/Reject</h4>
                        <p>Approve appointments that fit your schedule</p>
                    </div>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>3️⃣</div>
                        <h4>Conduct Sessions</h4>
                        <p>Meet with patients during scheduled times</p>
                    </div>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>4️⃣</div>
                        <h4>Mark Complete</h4>
                        <p>Update appointment status after consultation</p>
                    </div>
                </div>
                <Link to="/doctor/dashboard" className="btn" style={{background: 'white', color: '#667eea', marginTop: '2rem', fontWeight: '600'}}>
                    Go to Dashboard
                </Link>
            </div>

            <div className="grid grid-3" style={{marginTop: '3rem'}}>
                <div className="card" style={{textAlign: 'center', background: '#f0fff4', border: '1px solid #9ae6b4'}}>
                    <h4 style={{color: '#22543d'}}>✅ Easy Approval Process</h4>
                    <p style={{color: '#2d3748'}}>Simple one-click approval for appointment requests</p>
                </div>
                <div className="card" style={{textAlign: 'center', background: '#e6fffa', border: '1px solid #81e6d9'}}>
                    <h4 style={{color: '#2c7a7b'}}>📱 Real-time Updates</h4>
                    <p style={{color: '#2d3748'}}>Instant notifications for new appointment requests</p>
                </div>
                <div className="card" style={{textAlign: 'center', background: '#f0f4ff', border: '1px solid #c6d2fd'}}>
                    <h4 style={{color: '#553c9a'}}>📈 Practice Analytics</h4>
                    <p style={{color: '#2d3748'}}>Track your appointment statistics and patient flow</p>
                </div>
            </div>
        </div>
    )
}