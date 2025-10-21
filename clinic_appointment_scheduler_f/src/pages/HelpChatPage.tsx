import { useEffect, useState } from 'react'
import api from '../api/client'

type HelpMessage = {
    id: number
    senderEmail: string
    senderType: 'PATIENT' | 'DOCTOR' | 'ADMIN'
    message: string
    sentAt: string
}

type UserInfo = {
    email: string
    type: 'PATIENT' | 'DOCTOR' | 'ADMIN'
}

export default function HelpChatPage() {
    const [messages, setMessages] = useState<HelpMessage[]>([])
    const [newMessage, setNewMessage] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)
    const [replyingTo, setReplyingTo] = useState<string | null>(null)
    const [uniqueUsers, setUniqueUsers] = useState<UserInfo[]>([])

    useEffect(() => {
        loadMessages()
        const interval = setInterval(loadMessages, 3000)
        return () => clearInterval(interval)
    }, [])

    const loadMessages = async () => {
        try {
            const res = await api.get('/help-chat')
            setMessages(res.data)
            
            // Extract unique users for admin reply dropdown
            const users = res.data
                .filter((msg: HelpMessage) => msg.senderType !== 'ADMIN')
                .reduce((acc: UserInfo[], msg: HelpMessage) => {
                    if (!acc.find(u => u.email === msg.senderEmail)) {
                        acc.push({ email: msg.senderEmail, type: msg.senderType })
                    }
                    return acc
                }, [])
            setUniqueUsers(users)
            setError(null)
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to load messages')
        } finally {
            setLoading(false)
        }
    }

    const sendMessage = async () => {
        if (!newMessage.trim()) return
        try {
            const payload: any = { message: newMessage }
            if (replyingTo) {
                payload.recipientEmail = replyingTo
            }
            await api.post('/help-chat', payload)
            setNewMessage('')
            setReplyingTo(null)
            loadMessages()
        } catch (err: any) {
            setError(err?.response?.data?.message ?? 'Failed to send message')
        }
    }

    if (loading) return (
        <div className="page-container">
            <div style={{textAlign: 'center', padding: '2rem'}}>Loading chat...</div>
        </div>
    )

    return (
        <div className="page-container">
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>💬 Help & Support</h2>
            
            <div style={{maxWidth: '600px', margin: '0 auto'}}>
                {error && <div className="alert alert-error">{error}</div>}
                
                <div style={{
                    border: '1px solid #e2e8f0',
                    borderRadius: '8px',
                    height: '400px',
                    overflowY: 'auto',
                    padding: '1rem',
                    marginBottom: '1rem',
                    background: '#f9fafb'
                }}>
                    {messages.length === 0 ? (
                        <div style={{textAlign: 'center', color: '#666', padding: '2rem'}}>
                            No messages yet. Ask admin for help!
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
                                     msg.senderType === 'DOCTOR' ? '👨⚕️ Doctor' : '👤 Patient'} ({msg.senderEmail})
                                </div>
                                <div style={{marginBottom: '0.25rem'}}>{msg.message}</div>
                                <div style={{fontSize: '0.75rem', color: '#666'}}>
                                    {new Date(msg.sentAt).toLocaleString()}
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {uniqueUsers.length > 0 && (
                    <div style={{marginBottom: '1rem'}}>
                        <select 
                            className="form-select" 
                            value={replyingTo || ''} 
                            onChange={e => setReplyingTo(e.target.value || null)}
                            style={{width: '100%'}}
                        >
                            <option value="">Reply to all (broadcast)</option>
                            {uniqueUsers.map(user => (
                                <option key={user.email} value={user.email}>
                                    Reply to {user.email} ({user.type})
                                </option>
                            ))}
                        </select>
                    </div>
                )}
                
                <div style={{display: 'flex', gap: '0.5rem'}}>
                    <input
                        className="form-input"
                        type="text"
                        placeholder={replyingTo ? `Reply to ${replyingTo}...` : "Type your message..."}
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
            </div>
        </div>
    )
}