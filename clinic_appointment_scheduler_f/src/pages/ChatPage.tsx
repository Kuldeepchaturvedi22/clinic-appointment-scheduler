import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../api/client'

type ChatMessage = {
    id: number
    senderType: 'PATIENT' | 'DOCTOR'
    senderName: string
    message: string
    sentAt: string
}

export default function ChatPage() {
    const { appointmentId } = useParams()
    const [messages, setMessages] = useState<ChatMessage[]>([])
    const [newMessage, setNewMessage] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (!appointmentId) return
        loadMessages()
        const interval = setInterval(loadMessages, 3000) // Poll every 3 seconds
        return () => clearInterval(interval)
    }, [appointmentId])

    const loadMessages = async () => {
        try {
            const res = await api.get(`/chat/appointment/${appointmentId}`)
            setMessages(res.data)
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
            await api.post(`/chat/appointment/${appointmentId}`, { message: newMessage })
            setNewMessage('')
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
            <h2 style={{textAlign: 'center', marginBottom: '2rem'}}>💬 Appointment Chat</h2>
            
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
                            No messages yet. Start the conversation!
                        </div>
                    ) : (
                        messages.map(msg => (
                            <div key={msg.id} style={{
                                marginBottom: '1rem',
                                padding: '0.75rem',
                                borderRadius: '8px',
                                background: msg.senderType === 'DOCTOR' ? '#e6fffa' : '#f0f4ff',
                                border: `1px solid ${msg.senderType === 'DOCTOR' ? '#81e6d9' : '#c6d2fd'}`
                            }}>
                                <div style={{
                                    fontSize: '0.875rem',
                                    fontWeight: '600',
                                    color: msg.senderType === 'DOCTOR' ? '#2c7a7b' : '#553c9a',
                                    marginBottom: '0.25rem'
                                }}>
                                    {msg.senderType === 'DOCTOR' ? '👨⚕️' : '👤'} {msg.senderName}
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
                        placeholder="Type your message..."
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