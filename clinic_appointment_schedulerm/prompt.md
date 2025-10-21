# ClinicCare - Appointment Scheduler Project Prompts

This document contains the sequential prompts used to build the ClinicCare appointment scheduling system. Follow these prompts in order to recreate the entire project.

## Backend Development (Spring Boot)

### 1. Project Setup and Basic Structure

**Prompt:**
```
Create a Spring Boot application for a clinic appointment scheduling system called "ClinicCare". The system should have:

1. User authentication with JWT tokens
2. Two types of users: PATIENT and DOCTOR
3. Appointment booking and management system
4. RESTful APIs for all operations

Set up the basic project structure with:
- Spring Boot 3.x
- Spring Security with JWT
- JPA/Hibernate for database
- MySQL database
- Proper package structure (controller, service, repository, entity, dto)
- Basic security configuration
- CORS configuration for frontend integration

Include User entity with roles, Patient and Doctor entities, and basic authentication endpoints (login, register).
```

**Expected Response:**
A complete Spring Boot project setup with user authentication, basic entities (User, Patient, Doctor), JWT security configuration, and authentication controllers. The project should have proper package structure and be ready for appointment functionality.

### 2. Appointment System Implementation

**Prompt:**
```
Implement the appointment booking system with the following requirements:

1. Appointment entity with fields: id, doctorId, patientId, startTime, endTime, status (PENDING, SCHEDULED, COMPLETED, CANCELLED), notes
2. Time slot system: 2-hour slots from 9 AM to 8 PM, bookable for today and tomorrow
3. Appointment workflow: PENDING → SCHEDULED (doctor accepts) → COMPLETED
4. APIs for:
   - Patients: book appointments, view history
   - Doctors: view pending/scheduled appointments, accept/reject/complete appointments
   - Get available time slots for doctors
   - Dashboard data for doctors

Include proper validation, error handling, and DTO classes for clean API responses. Use eager fetching to avoid lazy loading issues.
```

**Expected Response:**
Complete appointment system with Appointment entity, AppointmentService with business logic, AppointmentController with all necessary endpoints, proper DTOs, and time slot generation logic. Should handle the complete appointment lifecycle.

### 3. Doctor Management and Search

**Prompt:**
```
Implement doctor management features:

1. Doctor profile management (update profile, view details)
2. Doctor search functionality by name and specialization
3. Doctor listing with contact information
4. Available slots endpoint for each doctor
5. Doctor dashboard with statistics (today's appointments, pending, completed)

Include proper error handling and ensure all endpoints return consistent DTO responses instead of raw entities.
```

**Expected Response:**
Doctor management system with profile CRUD operations, search functionality, dashboard statistics, and available slots calculation. All endpoints should use DTOs and handle errors gracefully.

### 4. Chat System Implementation

**Prompt:**
```
Implement a real-time chat system for scheduled appointments:

1. Chat entity to store messages between patients and doctors
2. Messages should be linked to specific appointments
3. Only participants of scheduled appointments can chat
4. Chat endpoints:
   - Get messages for an appointment
   - Send message to an appointment chat
5. Include sender type (PATIENT/DOCTOR), message content, and timestamp
6. Proper access control to ensure only appointment participants can access chats

Use DTOs for responses and include sender name for better UX.
```

**Expected Response:**
Complete chat system with Chat entity, ChatService with access control, ChatController with REST endpoints, and proper security to ensure only appointment participants can communicate.

### 5. Data Serialization and Error Handling

**Prompt:**
```
Fix common Spring Boot issues:

1. Configure Jackson to handle Hibernate lazy loading and Java time serialization
2. Implement global exception handler for consistent error responses
3. Add proper validation and user-friendly error messages
4. Configure Jackson modules for OffsetDateTime and Hibernate proxies
5. Ensure all API responses use DTOs to avoid serialization issues

Focus on production-ready error handling and data serialization.
```

**Expected Response:**
Robust error handling with GlobalExceptionHandler, Jackson configuration for proper serialization, validation annotations, and consistent API response format across all endpoints.

---

## Frontend Development (React + TypeScript)

### 1. Project Setup and Authentication

**Prompt:**
```
Create a React TypeScript application for the ClinicCare frontend with:

1. Vite as build tool
2. React Router for navigation
3. Axios for API calls with interceptors
4. Authentication context with JWT token management
5. Login and registration pages for both patients and doctors
6. Protected routes that require authentication
7. Responsive design with modern CSS
8. Form validation utilities

Set up the basic app structure with header navigation, routing, and authentication flow.
```

**Expected Response:**
Complete React TypeScript setup with authentication system, routing, API client configuration, login/register forms, protected routes, and responsive design foundation.

### 2. Doctor and Patient Features

**Prompt:**
```
Implement the main user interfaces:

1. Doctor features:
   - Dashboard with appointment statistics
   - Appointment management (pending, scheduled, completed)
   - Profile management
   - Accept/reject/complete appointment actions

2. Patient features:
   - Browse and search doctors
   - Book appointments with time slot selection
   - View appointment history
   - Profile management

3. Common features:
   - Responsive design for mobile and desktop
   - Loading states and error handling
   - Form validation with user feedback

Use modern React patterns with hooks and TypeScript for type safety.
```

**Expected Response:**
Complete user interfaces for both doctors and patients with all core functionality, responsive design, proper state management, and TypeScript integration.

### 3. Appointment Booking System

**Prompt:**
```
Create an advanced appointment booking system:

1. Visual time slot selection interface
2. Display available 2-hour slots from 9 AM to 8 PM
3. Show slot availability status (available/booked)
4. Allow booking for today and tomorrow
5. Integration with doctor selection
6. Appointment confirmation and feedback
7. Real-time slot updates

Make the booking process intuitive and user-friendly with clear visual feedback.
```

**Expected Response:**
Interactive appointment booking interface with visual slot selection, real-time availability, doctor integration, and smooth user experience with proper validation and feedback.

### 4. Chat System Integration

**Prompt:**
```
Implement the frontend chat system:

1. Chat interface for scheduled appointments
2. Real-time message display with polling
3. Message history with sender identification
4. Chat access from appointment lists
5. Responsive chat UI for mobile and desktop
6. Integration with appointment status
7. Access control (only for scheduled appointments)

Create an intuitive chat interface that works seamlessly with the appointment system.
```

**Expected Response:**
Complete chat interface with message history, real-time updates, responsive design, proper access control, and integration with the appointment system.

### 5. Features Pages and Navigation

**Prompt:**
```
Create comprehensive features pages and navigation:

1. Patient features page showcasing all capabilities
2. Doctor features page highlighting practice management tools
3. Homepage with role-based navigation
4. Professional footer with company information
5. Responsive navigation that adapts to user roles
6. Feature highlights with clear call-to-action buttons
7. Consistent branding and design throughout

Focus on user experience and clear feature presentation.
```

**Expected Response:**
Professional features pages, responsive navigation system, informative homepage, and consistent design that clearly communicates the platform's value to both patients and doctors.

---

## Additional Configuration Prompts

### Database Configuration

**Prompt:**
```
Configure the application for production deployment:

1. Database configuration for MySQL with proper connection pooling
2. Environment-specific properties (dev, prod)
3. Proper CORS configuration for frontend integration
4. Security headers and JWT configuration
5. Logging configuration
6. Error handling for database operations

Ensure the application is production-ready with proper configuration management.
```

### Deployment Preparation

**Prompt:**
```
Prepare the application for deployment:

1. Frontend build configuration with proper proxy setup
2. Backend packaging with embedded server
3. Environment variable configuration
4. Database migration scripts
5. Docker configuration (optional)
6. Production security settings
7. Performance optimizations

Create deployment-ready configurations for both frontend and backend.
```

---

## Project Structure Overview

```
clinic-appointment-scheduler/
├── backend/ (Spring Boot)
│   ├── src/main/java/com/example/clinic/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic
│   │   ├── repository/     # Data Access
│   │   ├── entity/         # JPA Entities
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── config/        # Configuration Classes
│   │   └── security/      # Security Configuration
│   └── src/main/resources/
│       └── application.properties
└── frontend/ (React + TypeScript)
    ├── src/
    │   ├── components/    # Reusable Components
    │   ├── pages/         # Page Components
    │   ├── context/       # React Context
    │   ├── api/          # API Client
    │   ├── utils/        # Utility Functions
    │   └── types/        # TypeScript Types
    ├── public/
    └── package.json
```

## Key Technologies Used

**Backend:**
- Spring Boot 3.x
- Spring Security with JWT
- JPA/Hibernate
- MySQL Database
- Maven

**Frontend:**
- React 18+ with TypeScript
- Vite (Build Tool)
- React Router
- Axios
- Modern CSS with Flexbox/Grid

## Recent Updates and Additional Features

### 6. Admin Role and Management System

**Prompt:**
```
Implement admin functionality for system management:

1. Add ADMIN role to UserRole enum with hardcoded credentials (admin@gmail.com/admin123)
2. Admin dashboard with system overview
3. User management: view, edit, delete patients and doctors
4. Appointment management: view all appointments across the system
5. Admin-only access controls and navigation
6. Proper authentication bypass for hardcoded admin credentials

Ensure admin has full system access while maintaining security for regular users.
```

### 7. Rating and Review System

**Prompt:**
```
Implement a 5-star rating system:

1. Rating entity with patient, doctor, appointment, stars (1-5), and comment fields
2. Patients can rate doctors only for their scheduled/completed appointments
3. Allow rating updates - most recent rating reflects for same appointment
4. Display average ratings on doctor profiles
5. Show individual reviews with patient names
6. Admin can manage all ratings (edit/delete)
7. Rating validation and proper error handling

Focus on user experience and preventing rating abuse.
```

### 8. Secure Help Chat System

**Prompt:**
```
Implement a secure help chat system for user support:

1. Help chat entity with sender, recipient, message, and timestamp
2. Patients and doctors can send messages to admin for support
3. Admin interface with separate conversations for each user
4. Message privacy: users only see their own conversation with admin
5. Admin replies are targeted to specific users (not broadcast)
6. Real-time message updates with polling
7. Proper access controls and message filtering

Ensure complete privacy where users cannot see other users' conversations.
```

### 9. Comprehensive Testing Suite

**Prompt:**
```
Add comprehensive backend testing with 80%+ code coverage:

1. Unit tests for all controllers with security testing
2. Service layer tests with mocking and validation scenarios
3. Repository tests for data access operations
4. Integration tests for complete workflows
5. JaCoCo plugin for coverage reporting
6. Test all error scenarios and edge cases
7. Mock authentication and security contexts

Focus on critical business logic, security, and error handling paths.
```

## Features Implemented

1. **Authentication System** - JWT-based auth for patients, doctors, and admin
2. **Appointment Management** - Complete booking and management workflow
3. **Time Slot System** - 2-hour slots with availability checking
4. **Doctor Search** - Search by name and specialization
5. **Chat System** - Real-time communication for scheduled appointments
6. **Responsive Design** - Mobile-first responsive interface
7. **Role-based Access** - Different interfaces for patients, doctors, and admin
8. **Dashboard Analytics** - Statistics and appointment tracking
9. **Profile Management** - User profile CRUD operations
10. **Error Handling** - Comprehensive error handling and validation
11. **Admin Management** - Complete admin panel for system management
12. **Rating System** - 5-star rating and review system with updates
13. **Help Chat System** - Secure support chat with admin
14. **Testing Suite** - Comprehensive backend testing with high coverage

## Security Features

- **JWT Authentication** - Secure token-based authentication
- **Role-based Access Control** - Different permissions for each user type
- **Admin Hardcoded Access** - Secure admin authentication bypass
- **Chat Privacy** - Users can only see their own conversations
- **Appointment Access Control** - Users can only access their own appointments
- **Rating Validation** - Prevent rating abuse and unauthorized access

## Testing Coverage

- **Controller Tests** - All REST endpoints with security scenarios
- **Service Tests** - Business logic with mocking and validation
- **Integration Tests** - Complete workflow testing
- **Security Tests** - Authentication and authorization scenarios
- **Error Handling Tests** - Exception scenarios and edge cases
- **JaCoCo Coverage** - 80%+ code coverage reporting

This prompt sequence will help recreate the complete ClinicCare appointment scheduling system with all its features, security measures, and comprehensive testing suite.