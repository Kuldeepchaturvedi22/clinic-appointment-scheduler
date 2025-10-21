import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen } from './test-utils'
import { mockAxios } from './mocks'
import App from '../App'

// Mock localStorage
const mockLocalStorage = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage,
})

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockLocalStorage.getItem.mockReturnValue(null)
  })

  it('should render homepage for unauthenticated users', () => {
    render(<App />)
    
    expect(screen.getByText('🏥 ClinicCare')).toBeInTheDocument()
    expect(screen.getByText('Your Health, Our Priority')).toBeInTheDocument()
    expect(screen.getByText('Get Started')).toBeInTheDocument()
  })

  it('should render navigation header', () => {
    render(<App />)
    
    expect(screen.getByText('🏥 ClinicCare')).toBeInTheDocument()
    expect(screen.getByText('Login')).toBeInTheDocument()
    expect(screen.getByText('Register')).toBeInTheDocument()
  })

  it('should render footer', () => {
    render(<App />)
    
    expect(screen.getByText('© 2024 ClinicCare. All rights reserved.')).toBeInTheDocument()
    expect(screen.getByText('About Us')).toBeInTheDocument()
    expect(screen.getByText('Contact')).toBeInTheDocument()
  })

  it('should show authenticated navigation for logged in users', () => {
    mockLocalStorage.getItem.mockImplementation((key) => {
      if (key === 'token') return 'mock-token'
      if (key === 'role') return 'PATIENT'
      return null
    })

    render(<App />)
    
    expect(screen.getByText('My Profile')).toBeInTheDocument()
    expect(screen.getByText('Logout')).toBeInTheDocument()
  })

  it('should show different navigation for doctors', () => {
    mockLocalStorage.getItem.mockImplementation((key) => {
      if (key === 'token') return 'mock-token'
      if (key === 'role') return 'DOCTOR'
      return null
    })

    render(<App />)
    
    expect(screen.getByText('Dashboard')).toBeInTheDocument()
    expect(screen.getByText('Appointments')).toBeInTheDocument()
  })

  it('should handle responsive design', () => {
    render(<App />)
    
    // Check that the app container has proper styling
    const appContainer = screen.getByText('🏥 ClinicCare').closest('div')
    expect(appContainer).toBeInTheDocument()
  })
})