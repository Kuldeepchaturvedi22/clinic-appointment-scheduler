import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, waitFor } from '../test-utils'
import { mockAxios, mockAppointment } from '../mocks'
import PatientAppointmentHistoryPage from '../../pages/PatientAppointmentHistoryPage'

describe('PatientAppointmentHistoryPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render appointment history', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockAppointment]
    })

    render(<PatientAppointmentHistoryPage />)
    
    expect(screen.getByText('📊 My Appointment History')).toBeInTheDocument()

    await waitFor(() => {
      expect(screen.getByText('Dr. Smith')).toBeInTheDocument()
      expect(screen.getByText('Cardiology')).toBeInTheDocument()
      expect(screen.getByText('PENDING')).toBeInTheDocument()
    })
  })

  it('should show loading state', () => {
    mockAxios.get.mockImplementationOnce(() => new Promise(() => {}))

    render(<PatientAppointmentHistoryPage />)
    
    expect(screen.getByText('Loading appointment history...')).toBeInTheDocument()
  })

  it('should show error state', async () => {
    mockAxios.get.mockRejectedValueOnce({
      response: { data: { message: 'Failed to load appointments' } }
    })

    render(<PatientAppointmentHistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Failed to load appointments')).toBeInTheDocument()
    })
  })

  it('should show empty state', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [] })

    render(<PatientAppointmentHistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('No appointments found')).toBeInTheDocument()
      expect(screen.getByText("You haven't booked any appointments yet.")).toBeInTheDocument()
    })
  })

  it('should show chat button for scheduled appointments', async () => {
    const scheduledAppointment = { ...mockAppointment, status: 'SCHEDULED' }
    mockAxios.get.mockResolvedValueOnce({
      data: [scheduledAppointment]
    })

    render(<PatientAppointmentHistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('💬 Chat with Doctor')).toBeInTheDocument()
      expect(screen.getByText('💬 Chat with Doctor').closest('a')).toHaveAttribute('href', '/chat/1')
    })
  })

  it('should not show chat button for non-scheduled appointments', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockAppointment] // PENDING status
    })

    render(<PatientAppointmentHistoryPage />)

    await waitFor(() => {
      expect(screen.queryByText('💬 Chat with Doctor')).not.toBeInTheDocument()
    })
  })

  it('should display appointment details correctly', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockAppointment]
    })

    render(<PatientAppointmentHistoryPage />)

    await waitFor(() => {
      expect(screen.getByText('Dr. Smith')).toBeInTheDocument()
      expect(screen.getByText('Cardiology')).toBeInTheDocument()
      expect(screen.getByText('#1')).toBeInTheDocument()
      expect(screen.getByText('Test appointment')).toBeInTheDocument()
    })
  })
})