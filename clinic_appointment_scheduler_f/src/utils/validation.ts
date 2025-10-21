export const validateEmail = (email: string): string | null => {
  if (!email) return 'Email is required'
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) return 'Please enter a valid email address'
  return null
}

export const validatePassword = (password: string): string | null => {
  if (!password) return 'Password is required'
  if (password.length < 6) return 'Password must be at least 6 characters'
  return null
}

export const validatePhone = (phone: string): string | null => {
  if (!phone) return 'Phone number is required'
  const phoneRegex = /^\+?[\d\s-()]{10,}$/
  if (!phoneRegex.test(phone)) return 'Please enter a valid phone number'
  return null
}

export const validateName = (name: string): string | null => {
  if (!name) return 'Full name is required'
  if (name.trim().length < 2) return 'Name must be at least 2 characters'
  return null
}

export const validateDate = (date: string): string | null => {
  if (!date) return 'Date is required'
  const selectedDate = new Date(date)
  const today = new Date()
  if (selectedDate >= today) return 'Please enter a valid birth date'
  return null
}