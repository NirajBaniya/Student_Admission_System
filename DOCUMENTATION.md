# EduPortal - Student Admission System Documentation

## 1. Project Overview
**EduPortal** is a modern, centralized web-based application designed to streamline the college admission process. It serves as a bridge between prospective students and college administrators, providing a paperless environment for submitting, tracking, and managing admission applications.

The system is currently configured for a **Single-College Architecture**, allowing one institution to manage its entire admission lifecycle through a robust administrative interface.

---

## 2. Technology Stack
The project is built using a modern Java-based stack focused on security, scalability, and clean code principles:

*   **Backend:** Java 21, Spring Boot 3.x
*   **Database:** PostgreSQL (with Spring Data JPA / Hibernate)
*   **Frontend Templating:** Thymeleaf
*   **Styling:** Bootstrap 5.x, Vanilla CSS (main.css), Bootstrap Icons
*   **Authentication:** Custom Spring-based Authentication (Role-based access)
*   **Build Tool:** Gradle

---

## 3. Core Features

### 3.1 Global / Guest Features
*   **Dynamic Landing Page:** A welcoming home page with clear calls to action.
*   **Detailed About Page:** Information about the admission process, including "Easy Application," "Document Upload," and "Real-time Tracking."
*   **Consistent Navigation:** A unified header and footer across all public and authenticated pages.

### 3.2 Student Features
*   **Registration & Authentication:** Secure account creation and login.
*   **Profile Management:**
    *   View and edit personal details (Address, Profile Picture).
    *   **Delete Account:** Permanent removal of profile, profile picture, and all associated applications (Full Cascade).
*   **Admission Dashboard:**
    *   Apply for specific courses offered by the college.
    *   **Document Upload:** Securely upload transcripts and certificates during application.
    *   **Unique Application Check:** Prevents applying to the *same course* twice, but allows applying to different courses within the same college.
*   **Application Tracking:** View a list of submitted applications with live status updates (Pending, Accepted, Rejected) and **Admin Feedback** (comments).

### 3.3 Admin Features
*   **Administrative Dashboard:**
    *   **Live Statistics:** Real-time counters for Pending, Accepted, and Rejected applications.
    *   **Application Review:** Detailed view of student information, GPA scores, and submitted documents.
    *   **Status Management:** One-click buttons to Accept, Reject, or set applications back to Pending.
    *   **Feedback Loop:** Ability to leave specific comments/reasons for students to see on their dashboard.
*   **Course Management:**
    *   Dynamically **Add** new courses to the college offering.
    *   **Remove** courses that are no longer available.
    *   These changes reflect immediately in the Student's application form.

---

## 4. Architecture & Data Model

### 4.1 Key Entities
*   **User:** Stores credentials, profile data, and roles (`STUDENT`, `ADMIN`).
*   **College:** Stores institution details (Name, Location, Founded Year) and a list of available `Courses` and `Features`.
*   **Application:** The bridge entity connecting `User` to `College`. Stores:
    *   Applied Course
    *   GPA Score
    *   Documents Path (File System Reference)
    *   Status (Enum: `PENDING`, `ACCEPTED`, `REJECTED`)
    *   Admin Comment
    *   Applied Date (Timestamp)

### 4.2 File Storage
*   The system uses a `StorageService` to handle file uploads.
*   Files are saved to a configurable directory on the server (e.g., `uploads/`).
*   The database stores only the unique filename to ensure portability and speed.

---

## 5. Security & Validation
*   **Role-Based Access Control (RBAC):** Dashboards are protected; students cannot access administrative panels, and unauthenticated users are redirected to the login page.
*   **Transactional Integrity:** Sensitive operations like "Delete Profile" are wrapped in `@Transactional` to ensure that if one part of the deletion fails (e.g., file removal), the database transaction rolls back to prevent orphan records.
*   **Data Integrity:** The system performs checks at both the UI and Repository levels (e.g., ensuring a student doesn't apply for the same course multiple times).

---

## 6. User Interface Design
The design philosophy focuses on **Aesthetics and Usability**:
*   **Stat Cards:** Clean, color-coded summaries for the Admin (Yellow for Pending, Green for Accepted, Red for Rejected).
*   **Split-Pane Dashboards:** The Admin Dashboard uses a modern sidebar-list and detail-view layout for efficient processing.
*   **Glassmorphism & Micro-animations:** Subtle hover effects on buttons and list items for a premium feel.
*   **Mobile Responsive:** All pages use Bootstrap’s grid system to ensure compatibility across desktops, tablets, and phones.

---

## 7. Setup & Build Instructions

### Prerequisites
*   JDK 21 or higher
*   PostgreSQL Database
*   Gradle

### Running the Project
1.  Configure the database connection in `src/main/resources/application.properties`.
2.  Run the command:
    ```bash
    ./gradlew bootRun
    ```
3.  The system will automatically initialize a default college and admin user (if configured in `CollegeDataInitializer`).

---
*Documentation generated on January 2026 for EduPortal.*
