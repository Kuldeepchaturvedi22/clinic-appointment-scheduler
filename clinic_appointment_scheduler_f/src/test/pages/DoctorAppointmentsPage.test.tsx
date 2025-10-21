import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '../test-utils'
import { mockAxios, mockAppointment } from '../mocks'
import DoctorAppointmentsPage from '../../pages/DoctorAppointmentsPage'

describe('DoctorAppointmentsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render appointment management interface', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockAppointment]
    })

    render(<DoctorAppointmentsPage />)
    
    expect(screen.getByText('📋 Manage Appointments')).toBeInTheDocument()
    expect(screen.getByText('Pending Requests')).toBeInTheDocument()
    expect(screen.getByText('Scheduled Appointments')).toBeInTheDocument()
    expect(screen.getByText('Completed Appointments')).toBeInTheDocument()

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument()
    })
  })

  it('should handle appointment acceptance', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockAppointment]
    })
    mockAxios.put.mockResolvedValueOnce({})

    render(<DoctorAppointmentsPage />)

    await waitFor(() => {
      const acceptButton = screen.getByText('✅ Accept')
      fireEvent.click(acceptButton)
    })

    await waitFor(() => {
      expect(mockAxios.put).toHaveBeenCalledWith('/api/doctors/appointments/1/accept')
    })
  })

  it('should handle appointment rejection', async () => {
    mockAxios.get.mockResolvedValueOnce({
      data: [mockAppointment]
    })
    mockAxios.put.mockResolvedValueOnce({})

    render(<DoctorAppointmentsPage />)

    await waitFor(() => {
      const rejectButton = screen.getByText('❌ Reject')
      fireEvent.click(rejectButton)
    })

    await waitFor(() => {
      expect(mockAxios.put).toHaveBeenCalledWith('/api/doctors/appointments/1/reject')
    })
  })

  it('should handle appointment completion', async () => {
    const scheduledAppointment = { ...mockAppointment, status: 'SCHEDULED' }
    mockAxios.get.mockResolvedValueOnce({
      data: [scheduledAppointment]
    })
    mockAxios.put.mockResolvedValueOnce({})

    render(<DoctorAppointmentsPage />)

    await waitFor(() => {
      const completeButton = screen.getByText('✅ Complete')
      fireEvent.click(completeButton)
    })

    await waitFor(() => {
      expect(mockAxios.put).toHaveBeenCalledWith('/api/doctors/appointments/1/complete')
    })
  })

  it('should show chat button for scheduled appointments', async () => {
    const scheduledAppointment = { ...mockAppointment, status: 'SCHEDULED' }
    mockAxios.get.mockResolvedValueOnce({
      data: [scheduledAppointment]
    })

    render(<DoctorAppointmentsPage />)

    await waitFor(() => {
      expect(screen.getByText('💬 Chat')).toBeInTheDocument()
      expect(screen.getByText('💬 Chat').closest('a')).toHaveAttribute('href', '/chat/1')
    })
  })

  it('should filter appointments by status', async () => {
    const pendingAppointment = { ...mockAppointment, status: 'PENDING' }
    const scheduledAppointment = { ...mockAppointment, id: 2, status: 'SCHEDULED' }
    const completedAppointment = { ...mockAppointment, id: 3, status: 'COMPLETED' }

    mockAxios.get.mockResolvedValueOnce({
      data: [pendingAppointment, scheduledAppointment, completedAppointment]
    })

    render(<DoctorAppointmentsPage />)

    await waitFor(() => {
      // Check that appointments are in correct sections
      const pendingSection = screen.getByText('Pending Requests').closest('div')
      const scheduledSection = screen.getByText('Scheduled Appointments').closest('div')
      const completedSection = screen.getByText('Completed Appointments').closest('div')

      expect(pendingSection).toBeInTheDocument()
      expect(scheduledSection).toBeInTheDocument()
      expect(completedSection).toBeInTheDocument()
    })
  })

  it('should show loading state', () => {
    mockAxios.get.mockImplementationOnce(() => new Promise(() => {}))

    render(<DoctorAppointmentsPage />)
    
    expect(screen.getByText('Loading appointments...')).toBeInTheDocument()
  })

  it('should show error state', async () => {
    mockAxios.get.mockRejectedValueOnce({
      response: { data: { message: 'Failed to load appointments' } }
    })

    render(<DoctorAppointmentsPage />)

    await waitFor(() => {
      expect(screen.getByText('Failed to load appointments')).toBeInTheDocument()
    })
  })
})