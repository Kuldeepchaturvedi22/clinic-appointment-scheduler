import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '../test-utils'
import { mockAxios } from '../mocks'
import RegisterPage from '../../pages/RegisterPage'

describe('RegisterPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render registration form', () => {
    render(<RegisterPage />)
    
    expect(screen.getByText('Create Account')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Full Name')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Email')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Phone')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument()
  })

  it('should handle successful patient registration', async () => {
    const mockResponse = {
      data: { token: 'mock-token', role: 'PATIENT' }
    }
    mockAxios.post.mockResolvedValueOnce(mockResponse)

    render(<RegisterPage />)
    
    fireEvent.change(screen.getByPlaceholderText('Full Name'), {
      target: { value: 'John Doe' }
    })
    fireEvent.change(screen.getByPlaceholderText('Email'), {
      target: { value: 'john@example.com' }
    })
    fireEvent.change(screen.getByPlaceholderText('Password'), {
      target: { value: 'password123' }
    })
    fireEvent.change(screen.getByPlaceholderText('Phone'), {
      target: { value: '1234567890' }
    })
    fireEvent.change(screen.getByDisplayValue(''), {
      target: { value: '1990-01-01' }
    })
    
    fireEvent.click(screen.getByRole('button', { name: /register/i }))

    await waitFor(() => {
      expect(mockAxios.post).toHaveBeenCalledWith('/api/auth/register', expect.objectContaining({
        fullName: 'John Doe',
        email: 'john@example.com',
        password: 'password123',
        phone: '1234567890',
        dateOfBirth: '1990-01-01',
        role: 'PATIENT'
      }))
    })
  })

  it('should handle doctor registration with specialization', async () => {
    const mockResponse = {
      data: { token: 'mock-token', role: 'DOCTOR' }
    }
    mockAxios.post.mockResolvedValueOnce(mockResponse)

    render(<RegisterPage />)
    
    // Select doctor role
    fireEvent.change(screen.getByDisplayValue('PATIENT'), {
      target: { value: 'DOCTOR' }
    })

    await waitFor(() => {
      expect(screen.getByPlaceholderText('Specialization')).toBeInTheDocument()
    })

    fireEvent.change(screen.getByPlaceholderText('Full Name'), {
      target: { value: 'Dr. Smith' }
    })
    fireEvent.change(screen.getByPlaceholderText('Specialization'), {
      target: { value: 'Cardiology' }
    })
    
    fireEvent.click(screen.getByRole('button', { name: /register/i }))

    await waitFor(() => {
      expect(mockAxios.post).toHaveBeenCalledWith('/api/auth/register', expect.objectContaining({
        role: 'DOCTOR',
        specialization: 'Cardiology'
      }))
    })
  })

  it('should show validation errors', async () => {
    render(<RegisterPage />)
    
    fireEvent.click(screen.getByRole('button', { name: /register/i }))

    await waitFor(() => {
      expect(screen.getByText('Name must be at least 2 characters')).toBeInTheDocument()
      expect(screen.getByText('Please enter a valid email')).toBeInTheDocument()
      expect(screen.getByText('Password must be at least 6 characters')).toBeInTheDocument()
    })
  })
})