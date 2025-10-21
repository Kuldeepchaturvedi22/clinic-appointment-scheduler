import { Link } from 'react-router-dom'

export default function AdminDashboardPage() {
    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>🔧 Admin Dashboard</h2>
            
            <div className="grid grid-2" style={{marginBottom: '2rem'}}>
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>👥</div>
                    <h3 style={{color: '#667eea'}}>Manage Users</h3>
                    <p>View and manage all patients and doctors in the system.</p>
                    <Link to="/admin/users" className="btn btn-primary" style={{marginTop: '1rem'}}>View Users</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>📅</div>
                    <h3 style={{color: '#667eea'}}>All Appointments</h3>
                    <p>Monitor all appointments across the platform.</p>
                    <Link to="/admin/appointments" className="btn btn-primary" style={{marginTop: '1rem'}}>View Appointments</Link>
                </div>
                
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>💬</div>
                    <h3 style={{color: '#667eea'}}>Help Chat</h3>
                    <p>Respond to patient and doctor support messages.</p>
                    <Link to="/admin/help-chat" className="btn btn-primary" style={{marginTop: '1rem'}}>Open Chat</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>⭐</div>
                    <h3 style={{color: '#667eea'}}>All Ratings</h3>
                    <p>View and manage all patient ratings and reviews.</p>
                    <Link to="/admin/ratings" className="btn btn-primary" style={{marginTop: '1rem'}}>View Ratings</Link>
                </div>
            </div>

            <div className="card" style={{marginTop: '2rem', textAlign: 'center', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white'}}>
                <h3 style={{color: 'white', marginBottom: '1rem'}}>Admin Access</h3>
                <p>You have full administrative access to all system features and data.</p>
            </div>
        </div>
    )
}