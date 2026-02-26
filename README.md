# NetCorp – Corporate Meeting Management App

NetCorp is an Android application developed in Kotlin that allows companies to manage meetings, internal communication and daily planning in a centralized and structured way.

This project was developed as my Final Degree Project (DAM – Multiplatform Application Development).

---

## Tech Stack

### Mobile
- Kotlin
- Android SDK
- Android Studio

### Backend (Cloud-Based)
- Firebase Authentication
- Cloud Firestore (NoSQL database)
- Firebase Crashlytics

### Architecture
- Client–Server architecture
- Real-time data synchronization
- NoSQL document-based data modeling

---

## Main Features

- User registration and secure login
- Profile management
- Corporate contact system
- Real-time private chat
- Meeting creation with priority levels
- Calendar view (monthly planning)
- Daily agenda view
- Error monitoring with Crashlytics

---

## System Architecture

NetCorp follows a client-server architecture:

- The Android application acts as the client.
- Firebase provides authentication and real-time database services.
- Firestore stores data using a NoSQL document-based model.

The database was initially designed using a relational model and later adapted to a NoSQL structure in Firestore.

---

## Database Structure (Firestore)

Main collections:

- **Users**
- **Chats**
- **Events**

Relationships:
- A user can create multiple meetings.
- Meetings can have multiple participants.
- Users can exchange real-time messages.

---

## Testing

Functional tests were performed for:
- Authentication
- Chat messaging
- Contact management
- Event creation
- Calendar and agenda synchronization
- Profile synchronization

All core features were successfully validated.

---

## Future Improvements

- Push notifications for messages and meetings
- Role and permission management
- Video call integration
- Cloud image storage
- Synchronization optimization
- UI/UX improvements

---

## Author

Paula Boix Vilella  
Junior Backend & Mobile Developer  
