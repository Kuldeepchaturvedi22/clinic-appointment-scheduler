import { useEffect, useState } from 'react'
import api from '../api/client'

type HelpMessage = {
    id: number
    senderEmail: string
    senderType: 'PATIENT' | 'DOCTOR' | 'ADMIN'
    message: string
    sentAt: string
}

export default function AdminHelpChatPage() {
    const [users, setUsers] = useState<string[]>([])
    const [selectedUser, setSelectedUser] = useState<string | null>(null)
    const [messages, setMessages] = useState<HelpMessage[]>([])
    const [newMessage, setNewMessage] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        loadUsers()
    }, [])

    useEffect(() => {
        if (selectedUser) {
            loadConversation(selectedUser)
            const interval = setInterval(() => loadConversation(selectedUser), 3000)
            return () => clearInterval(interval)
        }
    }, [selectedUser])

    const loadUsers = async () => {
        try {
            const res = await api.get('/help-chat/users')
            setUsers(res.data)
            setError(null)
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to load users')
        } finally {
            setLoading(false)
        }
    }

    const loadConversation = async (userEmail: string) => {
        try {
            const res = await api.get(`/help-chat/conversation/${encodeURIComponent(userEmail)}`)
            setMessages(res.data)
            setError(null)
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to load conversation')
        }
    }

    const sendMessage = async () => {
        if (!newMessage.trim() || !selectedUser) return
        try {
            await api.post('/help-chat', { 
                message: newMessage, 
                recipientEmail: selectedUser 
            })
            setNewMessage('')
            loadConversation(selectedUser)
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to send message')
        }
    }

    if (loading) return (
        <div className="page-container">
            <div style={{textAlign: 'center', padding: '2rem'}}>Loading users...</div>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>💬 Admin Help Chat</h2>
            
            <div style={{display: 'grid', gridTemplateColumns: '300px 1fr', gap: '1rem', height: '600px'}}>
                {/* Users List */}
                <div className="card" style={{padding: '1rem'}}>
                    <h3 style={{margin: '0 0 1rem 0'}}>Users</h3>
                    {error && <div className="alert alert-error" style={{marginBottom: '1rem'}}>{error}</div>}
                    <div style={{maxHeight: '500px', overflowY: 'auto'}}>
                        {users.map(user => (
                            <div 
                                key={user}
                                onClick={() => setSelectedUser(user)}
                                style={{
                                    padding: '0.75rem',
                                    borderRadius: '6px',
                                    cursor: 'pointer',
                                    marginBottom: '0.5rem',
                                    background: selectedUser === user ? '#667eea' : '#f7fafc',
                                    color: selectedUser === user ? 'white' : '#333',
                                    border: '1px solid #e2e8f0'
                                }}
                            >
                                {user}
                            </div>
                        ))}
                        {users.length === 0 && (
                            <div style={{textAlign: 'center', color: '#666', padding: '2rem'}}>
                                No users found
                            </div>
                        )}
                    </div>
                </div>

                {/* Chat Area */}
                <div className="card" style={{padding: '1rem', display: 'flex', flexDirection: 'column'}}>
                    {selectedUser ? (
                        <>
                            <h3 style={{margin: '0 0 1rem 0'}}>Chat with {selectedUser}</h3>
                            
                            <div style={{
                                flex: 1,
                                border: '1px solid #e2e8f0',
                                borderRadius: '8px',
                                padding: '1rem',
                                marginBottom: '1rem',
                                overflowY: 'auto',
                                background: '#f9fafb'
                            }}>
                                {messages.length === 0 ? (
                                    <div style={{textAlign: 'center', color: '#666', padding: '2rem'}}>
                                        No messages yet
                                    </div>
                                ) : (
                                    messages.map(msg => (
                                        <div key={msg.id} style={{
                                            marginBottom: '1rem',
                                            padding: '0.75rem',
                                            borderRadius: '8px',
                                            background: msg.senderType === 'ADMIN' ? '#e6fffa' : '#f0f4ff',
                                            border: `1px solid ${msg.senderType === 'ADMIN' ? '#81e6d9' : '#c6d2fd'}`
                                        }}>
                                            <div style={{
                                                fontSize: '0.875rem',
                                                fontWeight: '600',
                                                color: msg.senderType === 'ADMIN' ? '#2c7a7b' : '#553c9a',
                                                marginBottom: '0.25rem'
                                            }}>
                                                {msg.senderType === 'ADMIN' ? '🛠️ Admin' : 
                                                 msg.senderType === 'DOCTOR' ? '👨⚕️ Doctor' : '👤 Patient'}
                                            </div>
                                            <div style={{marginBottom: '0.25rem'}}>{msg.message}</div>
                                            <div style={{fontSize: '0.75rem', color: '#666'}}>
                                                {new Date(msg.sentAt).toLocaleString()}
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>

                            <div style={{display: 'flex', gap: '0.5rem'}}>
                                <input
                                    className="form-input"
                                    type="text"
                                    placeholder={`Reply to ${selectedUser}...`}
                                    value={newMessage}
                                    onChange={e => setNewMessage(e.target.value)}
                                    onKeyPress={e => e.key === 'Enter' && sendMessage()}
                                    style={{flex: 1}}
                                />
                                <button
                                    className="btn btn-primary"
                                    onClick={sendMessage}
                                    disabled={!newMessage.trim()}
                                >
                                    Send
                                </button>
                            </div>
                        </>
                    ) : (
                        <div style={{
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            height: '100%',
                            color: '#666'
                        }}>
                            Select a user to start chatting
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}