import { describe, it, expect } from 'vitest'
import { validateEmail, validatePhone, validateName, validateDate } from '../../utils/validation'

describe('Validation Utils', () => {
  describe('validateEmail', () => {
    it('should return null for valid emails', () => {
      expect(validateEmail('test@example.com')).toBeNull()
      expect(validateEmail('user.name@domain.co.uk')).toBeNull()
      expect(validateEmail('user+tag@example.org')).toBeNull()
    })

    it('should return error for invalid emails', () => {
      expect(validateEmail('')).toBe('Email is required')
      expect(validateEmail('invalid-email')).toBe('Please enter a valid email')
      expect(validateEmail('test@')).toBe('Please enter a valid email')
      expect(validateEmail('@example.com')).toBe('Please enter a valid email')
      expect(validateEmail('test.example.com')).toBe('Please enter a valid email')
    })
  })

  describe('validatePhone', () => {
    it('should return null for valid phone numbers', () => {
      expect(validatePhone('1234567890')).toBeNull()
      expect(validatePhone('123-456-7890')).toBeNull()
      expect(validatePhone('(123) 456-7890')).toBeNull()
      expect(validatePhone('+1 123 456 7890')).toBeNull()
    })

    it('should return error for invalid phone numbers', () => {
      expect(validatePhone('')).toBe('Phone number is required')
      expect(validatePhone('123')).toBe('Please enter a valid phone number')
      expect(validatePhone('abcdefghij')).toBe('Please enter a valid phone number')
      expect(validatePhone('12345')).toBe('Please enter a valid phone number')
    })
  })

  describe('validateName', () => {
    it('should return null for valid names', () => {
      expect(validateName('John Doe')).toBeNull()
      expect(validateName('Dr. Smith')).toBeNull()
      expect(validateName('Mary Jane Watson')).toBeNull()
      expect(validateName('Al')).toBeNull()
    })

    it('should return error for invalid names', () => {
      expect(validateName('')).toBe('Name is required')
      expect(validateName('A')).toBe('Name must be at least 2 characters')
      expect(validateName('   ')).toBe('Name is required')
    })
  })

  describe('validateDate', () => {
    it('should return null for valid dates', () => {
      expect(validateDate('1990-01-01')).toBeNull()
      expect(validateDate('2000-12-31')).toBeNull()
      expect(validateDate('1985-06-15')).toBeNull()
    })

    it('should return error for invalid dates', () => {
      expect(validateDate('')).toBe('Date is required')
      expect(validateDate('invalid-date')).toBe('Please enter a valid date')
      expect(validateDate('2025-01-01')).toBe('Date cannot be in the future')
      
      // Test date that's too far in the past
      expect(validateDate('1900-01-01')).toBe('Please enter a valid birth date')
    })

    it('should return error for future dates', () => {
      const futureDate = new Date()
      futureDate.setFullYear(futureDate.getFullYear() + 1)
      const futureDateString = futureDate.toISOString().split('T')[0]
      
      expect(validateDate(futureDateString)).toBe('Date cannot be in the future')
    })
  })
})