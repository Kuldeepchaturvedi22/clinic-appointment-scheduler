import { vi } from 'vitest'

// Mock axios
export const mockAxios = {
  get: vi.fn(() => Promise.resolve({ data: {} })),
  post: vi.fn(() => Promise.resolve({ data: {} })),
  put: vi.fn(() => Promise.resolve({ data: {} })),
  delete: vi.fn(() => Promise.resolve({ data: {} })),
  interceptors: {
    request: {
      use: vi.fn(),
      eject: vi.fn(),
    },
    response: {
      use: vi.fn(),
      eject: vi.fn(),
    },
  },
}

// Mock react-router-dom
export const mockNavigate = vi.fn()
export const mockLocation = {
  pathname: '/',
  search: '',
  hash: '',
  state: null,
  key: 'default',
}

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => mockLocation,
  }
})

// Mock API client
vi.mock('../api/client', () => ({
  default: mockAxios,
}))

// Sample test data
export const mockUser = {
  id: 1,
  email: 'test@example.com',
  role: 'PATIENT',
  fullName: 'John Doe',
}

export const mockDoctor = {
  id: 1,
  fullName: 'Dr. Smith',
  specialization: 'Cardiology',
  email: 'doctor@example.com',
  phone: '1234567890',
}

export const mockAppointment = {
  id: 1,
  startTime: '2024-01-01T09:00:00Z',
  endTime: '2024-01-01T11:00:00Z',
  status: 'PENDING',
  notes: 'Test appointment',
  doctorName: 'Dr. Smith',
  doctorSpecialization: 'Cardiology',
  patientName: 'John Doe',
}