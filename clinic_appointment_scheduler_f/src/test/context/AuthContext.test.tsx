import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, waitFor } from '../test-utils'
import { useAuth, AuthProvider } from '../../context/AuthContext'
import { mockAxios } from '../mocks'

const TestComponent = () => {
  const { user, token, role, login, logout } = useAuth()
  
  return (
    <div>
      <div data-testid="user">{user || 'No user'}</div>
      <div data-testid="token">{token || 'No token'}</div>
      <div data-testid="role">{role || 'No role'}</div>
      <button onClick={() => login('test@example.com', 'password')}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  )
}

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('should provide initial auth state', () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    expect(screen.getByTestId('user')).toHaveTextContent('No user')
    expect(screen.getByTestId('token')).toHaveTextContent('No token')
    expect(screen.getByTestId('role')).toHaveTextContent('No role')
  })

  it('should handle successful login', async () => {
    const mockResponse = {
      data: {
        token: 'mock-token',
        role: 'PATIENT'
      }
    }
    mockAxios.post.mockResolvedValueOnce(mockResponse)

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    const loginButton = screen.getByText('Login')
    loginButton.click()

    await waitFor(() => {
      expect(screen.getByTestId('token')).toHaveTextContent('mock-token')
      expect(screen.getByTestId('role')).toHaveTextContent('PATIENT')
    })
  })

  it('should handle logout', async () => {
    localStorage.setItem('token', 'existing-token')
    localStorage.setItem('role', 'PATIENT')

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    const logoutButton = screen.getByText('Logout')
    logoutButton.click()

    await waitFor(() => {
      expect(screen.getByTestId('token')).toHaveTextContent('No token')
      expect(screen.getByTestId('role')).toHaveTextContent('No role')
    })
  })
})