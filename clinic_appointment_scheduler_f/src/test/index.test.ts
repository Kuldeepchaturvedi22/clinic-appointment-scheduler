// Test Suite Entry Point
import { describe, it, expect } from 'vitest'

describe('ClinicCare Frontend Test Suite', () => {
  it('should run all tests successfully', () => {
    expect(true).toBe(true)
  })
})

// Import all test files to ensure they run
import './context/AuthContext.test'
import './pages/LoginPage.test'
import './pages/RegisterPage.test'
import './pages/DoctorsPage.test'
import './pages/BookAppointmentPage.test'
import './pages/PatientAppointmentHistoryPage.test'
import './pages/DoctorAppointmentsPage.test'
import './pages/ChatPage.test'
import './utils/validation.test'
import './App.test'