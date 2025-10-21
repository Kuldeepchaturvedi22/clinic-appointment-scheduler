import { Link } from 'react-router-dom'

export default function PatientFeaturesPage() {
    return (
        <div className="page-container">
            <div style={{textAlign: 'center', marginBottom: '3rem'}}>
                <h1 style={{color: '#667eea', marginBottom: '1rem'}}>👤 Welcome, Patient!</h1>
                <p style={{fontSize: '1.2rem', color: '#666'}}>Discover how ClinicCare makes healthcare appointments simple and convenient</p>
            </div>

            <div className="grid grid-2" style={{marginBottom: '3rem'}}>
                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>🔍</div>
                    <h3 style={{color: '#667eea'}}>Find Qualified Doctors</h3>
                    <p>Browse through our network of verified healthcare professionals. View their specializations, contact information, and book appointments instantly.</p>
                    <Link to="/doctors" className="btn btn-primary" style={{marginTop: '1rem'}}>Browse Doctors</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>📅</div>
                    <h3 style={{color: '#667eea'}}>Easy Appointment Booking</h3>
                    <p>Book appointments in just a few clicks. Choose from available time slots and get instant confirmation. No more waiting on hold!</p>
                    <Link to="/appointments/book" className="btn btn-primary" style={{marginTop: '1rem'}}>Book Now</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>📊</div>
                    <h3 style={{color: '#667eea'}}>Track Your Health Journey</h3>
                    <p>View your appointment history, manage your profile, and keep track of all your healthcare interactions in one place.</p>
                    <Link to="/me/patient" className="btn btn-primary" style={{marginTop: '1rem'}}>My Profile</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>⏰</div>
                    <h3 style={{color: '#667eea'}}>Flexible Scheduling</h3>
                    <p>Choose from available 2-hour time slots between 9 AM to 8 PM. Book for today or tomorrow based on your convenience.</p>
                    <Link to="/appointments/book" className="btn btn-secondary" style={{marginTop: '1rem'}}>View Slots</Link>
                </div>

                <div className="card" style={{textAlign: 'center'}}>
                    <div style={{fontSize: '3rem', marginBottom: '1rem'}}>💬</div>
                    <h3 style={{color: '#667eea'}}>Real-time Chat</h3>
                    <p>Communicate directly with your doctor during scheduled appointments. Ask questions, share concerns, and get instant responses.</p>
                    <Link to="/me/patient" className="btn btn-secondary" style={{marginTop: '1rem'}}>Access Chats</Link>
                </div>
            </div>

            <div className="card" style={{background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white', textAlign: 'center'}}>
                <h2 style={{color: 'white', marginBottom: '1rem'}}>How to Book Your First Appointment</h2>
                <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '2rem', marginTop: '2rem'}}>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>1️⃣</div>
                        <h4>Browse Doctors</h4>
                        <p>Find the right specialist for your needs</p>
                    </div>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>2️⃣</div>
                        <h4>Select Time Slot</h4>
                        <p>Choose from available appointment times</p>
                    </div>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>3️⃣</div>
                        <h4>Add Notes</h4>
                        <p>Include any specific concerns or symptoms</p>
                    </div>
                    <div>
                        <div style={{fontSize: '2rem', marginBottom: '0.5rem'}}>4️⃣</div>
                        <h4>Get Confirmation</h4>
                        <p>Receive instant booking confirmation</p>
                    </div>
                </div>
                <Link to="/appointments/book" className="btn" style={{background: 'white', color: '#667eea', marginTop: '2rem', fontWeight: '600'}}>
                    Start Booking Now
                </Link>
            </div>
        </div>
    )
}