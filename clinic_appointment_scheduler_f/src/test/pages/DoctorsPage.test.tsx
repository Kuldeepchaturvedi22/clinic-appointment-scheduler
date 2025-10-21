import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '../test-utils'
import { mockAxios, mockDoctor } from '../mocks'
import DoctorsPage from '../../pages/DoctorsPage'

describe('DoctorsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render doctors list', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockDoctor]
    })

    render(<DoctorsPage />)
    
    expect(screen.getByText('Find Your Doctor')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Search doctors by name or specialization...')).toBeInTheDocument()

    await waitFor(() => {
      expect(screen.getByText('Dr. Smith')).toBeInTheDocument()
      expect(screen.getByText('Cardiology')).toBeInTheDocument()
    })
  })

  it('should handle search functionality', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockDoctor]
    })

    render(<DoctorsPage />)
    
    const searchInput = screen.getByPlaceholderText('Search doctors by name or specialization...')
    fireEvent.change(searchInput, { target: { value: 'cardio' } })

    await waitFor(() => {
      expect(mockAxios.get).toHaveBeenCalledWith('/api/doctors?search=cardio')
    })
  })

  it('should show loading state', () => {
    mockAxios.get.mockImplementationOnce(() => new Promise(() => {}))

    render(<DoctorsPage />)
    
    expect(screen.getByText('Loading doctors...')).toBeInTheDocument()
  })

  it('should show error state', async () => {
    mockAxios.get.mockRejectedValueOnce({
      response: { data: { message: 'Failed to load doctors' } }
    })

    render(<DoctorsPage />)

    await waitFor(() => {
      expect(screen.getByText('Failed to load doctors')).toBeInTheDocument()
    })
  })

  it('should show empty state when no doctors found', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [] })

    render(<DoctorsPage />)

    await waitFor(() => {
      expect(screen.getByText('No doctors found')).toBeInTheDocument()
    })
  })

  it('should navigate to book appointment', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockDoctor]
    })

    render(<DoctorsPage />)

    await waitFor(() => {
      const bookButton = screen.getByText('Book Appointment')
      expect(bookButton).toBeInTheDocument()
      expect(bookButton.closest('a')).toHaveAttribute('href', '/appointments/book?doctorId=1')
    })
  })
})