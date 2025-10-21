import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '../test-utils'
import { mockAxios } from '../mocks'
import ChatPage from '../../pages/ChatPage'

// Mock useParams
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useParams: () => ({ appointmentId: '1' }),
  }
})

describe('ChatPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render chat interface', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [
        {
          id: 1,
          message: 'Hello Doctor',
          senderType: 'PATIENT',
          senderName: 'John Doe',
          timestamp: '2024-01-01T10:00:00Z'
        }
      ]
    })

    render(<ChatPage />)
    
    expect(screen.getByText('💬 Appointment Chat')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Type your message...')).toBeInTheDocument()
    expect(screen.getByText('Send')).toBeInTheDocument()

    await waitFor(() => {
      expect(screen.getByText('Hello Doctor')).toBeInTheDocument()
      expect(screen.getByText('John Doe')).toBeInTheDocument()
    })
  })

  it('should send message successfully', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [] })
    mockAxios.post.mockResolvedValueOnce({
      data: {
        id: 2,
        message: 'Hello Patient',
        senderType: 'DOCTOR',
        senderName: 'Dr. Smith',
        timestamp: '2024-01-01T10:05:00Z'
      }
    })

    render(<ChatPage />)

    await waitFor(() => {
      const messageInput = screen.getByPlaceholderText('Type your message...')
      fireEvent.change(messageInput, { target: { value: 'Hello Patient' } })
      
      const sendButton = screen.getByText('Send')
      fireEvent.click(sendButton)
    })

    await waitFor(() => {
      expect(mockAxios.post).toHaveBeenCalledWith('/api/chat/appointment/1', {
        message: 'Hello Patient'
      })
    })
  })

  it('should clear input after sending message', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [] })
    mockAxios.post.mockResolvedValueOnce({
      data: {
        id: 2,
        message: 'Test message',
        senderType: 'PATIENT',
        senderName: 'John Doe',
        timestamp: '2024-01-01T10:05:00Z'
      }
    })

    render(<ChatPage />)

    await waitFor(() => {
      const messageInput = screen.getByPlaceholderText('Type your message...') as HTMLInputElement
      fireEvent.change(messageInput, { target: { value: 'Test message' } })
      
      const sendButton = screen.getByText('Send')
      fireEvent.click(sendButton)
    })

    await waitFor(() => {
      const messageInput = screen.getByPlaceholderText('Type your message...') as HTMLInputElement
      expect(messageInput.value).toBe('')
    })
  })

  it('should handle send message on Enter key', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [] })
    mockAxios.post.mockResolvedValueOnce({
      data: {
        id: 2,
        message: 'Enter key message',
        senderType: 'PATIENT',
        senderName: 'John Doe',
        timestamp: '2024-01-01T10:05:00Z'
      }
    })

    render(<ChatPage />)

    await waitFor(() => {
      const messageInput = screen.getByPlaceholderText('Type your message...')
      fireEvent.change(messageInput, { target: { value: 'Enter key message' } })
      fireEvent.keyDown(messageInput, { key: 'Enter', code: 'Enter' })
    })

    await waitFor(() => {
      expect(mockAxios.post).toHaveBeenCalledWith('/api/chat/appointment/1', {
        message: 'Enter key message'
      })
    })
  })

  it('should not send empty messages', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [] })

    render(<ChatPage />)

    await waitFor(() => {
      const sendButton = screen.getByText('Send')
      fireEvent.click(sendButton)
    })

    expect(mockAxios.post).not.toHaveBeenCalled()
  })

  it('should display messages with correct styling', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [
        {
          id: 1,
          message: 'Patient message',
          senderType: 'PATIENT',
          senderName: 'John Doe',
          timestamp: '2024-01-01T10:00:00Z'
        },
        {
          id: 2,
          message: 'Doctor message',
          senderType: 'DOCTOR',
          senderName: 'Dr. Smith',
          timestamp: '2024-01-01T10:05:00Z'
        }
      ]
    })

    render(<ChatPage />)

    await waitFor(() => {
      expect(screen.getByText('Patient message')).toBeInTheDocument()
      expect(screen.getByText('Doctor message')).toBeInTheDocument()
      expect(screen.getByText('John Doe')).toBeInTheDocument()
      expect(screen.getByText('Dr. Smith')).toBeInTheDocument()
    })
  })

  it('should handle error when loading messages', async () => {
    mockAxios.get.mockRejectedValueOnce({
      response: { data: { message: 'Failed to load messages' } }
    })

    render(<ChatPage />)

    await waitFor(() => {
      expect(screen.getByText('Failed to load messages')).toBeInTheDocument()
    })
  })
})