import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '../test-utils'
import { mockAxios, mockDoctor } from '../mocks'
import BookAppointmentPage from '../../pages/BookAppointmentPage'

describe('BookAppointmentPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render appointment booking form', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [mockDoctor] })

    render(<BookAppointmentPage />)
    
    expect(screen.getByText('Book Appointment')).toBeInTheDocument()
    
    await waitFor(() => {
      expect(screen.getByText('Dr. Smith - Cardiology')).toBeInTheDocument()
    })
  })

  it('should load available slots when doctor is selected', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [mockDoctor] })
    mockAxios.get.mockResolvedValueOnce({ 
      data: ['09:00-11:00', '11:00-13:00', '13:00-15:00'] 
    })

    render(<BookAppointmentPage />)

    await waitFor(() => {
      const doctorSelect = screen.getByDisplayValue('')
      fireEvent.change(doctorSelect, { target: { value: '1' } })
    })

    await waitFor(() => {
      expect(screen.getByText('09:00-11:00')).toBeInTheDocument()
      expect(screen.getByText('11:00-13:00')).toBeInTheDocument()
      expect(screen.getByText('13:00-15:00')).toBeInTheDocument()
    })
  })

  it('should handle slot selection', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [mockDoctor] })
    mockAxios.get.mockResolvedValueOnce({ 
      data: ['09:00-11:00', '11:00-13:00'] 
    })

    render(<BookAppointmentPage />)

    await waitFor(() => {
      const doctorSelect = screen.getByDisplayValue('')
      fireEvent.change(doctorSelect, { target: { value: '1' } })
    })

    await waitFor(() => {
      const slot = screen.getByText('09:00-11:00')
      fireEvent.click(slot)
      expect(slot.closest('div')).toHaveClass('selected')
    })
  })

  it('should handle successful appointment booking', async () => {
    mockAxios.get.mockResolvedValueOnce({ data: [mockDoctor] })
    mockAxios.get.mockResolvedValueOnce({ data: ['09:00-11:00'] })
    mockAxios.post.mockResolvedValueOnce({ 
      data: { id: 1, status: 'PENDING' } 
    })

    render(<BookAppointmentPage />)

    await waitFor(() => {
      const doctorSelect = screen.getByDisplayValue('')
      fireEvent.change(doctorSelect, { target: { value: '1' } })
    })

    await waitFor(() => {
      const slot = screen.getByText('09:00-11:00')
      fireEvent.click(slot)
    })

    const notesTextarea = screen.getByPlaceholderText('Any specific concerns or notes...')
    fireEvent.change(notesTextarea, { target: { value: 'Test notes' } })

    const bookButton = screen.getByText('Book Appointment')
    fireEvent.click(bookButton)

    await waitFor(() => {
      expect(mockAxios.post).toHaveBeenCalledWith('/api/appointments/book', expect.objectContaining({
        doctorId: 1,
        notes: 'Test notes'
      }))
    })
  })

  it('should show validation errors', async () => {
    render(<BookAppointmentPage />)

    const bookButton = screen.getByText('Book Appointment')
    fireEvent.click(bookButton)

    await waitFor(() => {
      expect(screen.getByText('Please select a doctor')).toBeInTheDocument()
      expect(screen.getByText('Please select a time slot')).toBeInTheDocument()
    })
  })
})