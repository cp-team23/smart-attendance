# smart-attendance-system

Spring Boot backend for the Smart Attendance System — a capstone project that lets students mark attendance by scanning a session QR code and verifying their identity through face recognition.

This repo contains the core backend API along with a Thymeleaf-based web interface used by admins and teachers. It's one of three repositories that make up the full system:

- **[smart-attendance](https://github.com/cp-team23/smart-attendance)** ← you are here
- **[SmartAttendanceApp](https://github.com/cp-team23/SmartAttendanceApp)** — Kotlin Android app for students
- **[smart-attendance-face-match-service](https://github.com/cp-team23/smart-attendance-face-match-service)** — Python microservice for liveness detection and face comparison

**Live deployments:**
- Web app: https://smart-attendance-k7n4.onrender.com/
- Face match service health: https://smart-attendance-face-match-service.onrender.com/health
- Android APK download: https://smart-attendance-k7n4.onrender.com/app

---

## What it does

Auth is handled via JWT. Issued tokens are stored in Redis, which also makes it straightforward to invalidate them — when a user logs in from a different device, all existing sessions for that account are expired immediately, so only the latest login stays active.

**Admin** (web app) manages the overall college structure — teachers, students, departments, subjects, and class assignments. They can bulk upload student face photos and approve student image change requests by running a face scan directly against the submitted photo. Bulk image upload results and approval records are generated as PDFs. When adding multiple students at once, the upload report comes out as an Excel file.

**Teacher** (web app) creates attendance sessions for their classes, generates a dynamic QR code tied to that session, and can also take or edit attendance manually. Attendance records can be exported as Excel reports.

**Student** (Android app) opens the app, scans the QR code shown by the teacher, takes a selfie, and that's it. The app handles everything else. Students can also check their attendance history and profile, and request an image update if needed.

---

## How attendance marking works

1. The teacher starts a session and generates a QR code. The code includes an expiry timestamp and an integrity token, so replaying an old code or tampering with it will get rejected.
2. The student scans the QR with the Android app. The token is sent to the backend for validation.
3. The backend checks the QR signature and expiry. If it's invalid or expired, the app prompts the student to scan again — the QR step loops until a valid code is scanned.
4. Once the QR is confirmed valid, the app moves to the face verification step and prompts the student to take a selfie.
5. The selfie is sent to the backend, which calls the Python face-match service (via Spring WebFlux, non-blocking).
6. The face-match service runs a liveness check and compares the face embedding against the student's stored photo. If it fails — whether liveness or a mismatch — the app prompts the student to try again. The face step also loops until it passes.
7. Once the face is verified, attendance is recorded in MySQL and the student gets a confirmation.

Each step is a retry loop — an invalid QR keeps the student on the QR scanner, and a failed face check keeps them on the camera. Attendance only gets marked once both steps pass cleanly.

---

## Tech stack

Built with Java 17 and Spring Boot 3.5.6, packaged with Maven.

| Area | Tech |
|---|---|
| Auth | Spring Security + JWT (jjwt 0.11.5) |
| Database | Spring Data JPA + MySQL |
| Redis | JWT token and OTP storage |
| Face service integration | Spring WebFlux (reactive HTTP client) |
| Email (OTP, notifications) | Spring Mail over SMTP |
| Photo storage | Cloudinary |
| Web UI | Thymeleaf |
| Excel export | Apache POI (attendance reports, student bulk upload reports) |
| PDF generation | iText 7 (bulk image upload results, image approval records) |
| DTO mapping | ModelMapper |
| Containerization | Docker (eclipse-temurin:17-jdk) |
| Hosting | Render.com |

---

## Running locally

### Prerequisites

- Java 17
- Maven
- MySQL (running locally or a remote instance)
- Redis
- A Cloudinary account (for photo upload)
- The face-match service running separately (or pointing to the deployed one)

### Setup

Clone the repo and configure your environment. The app expects the following in `application.properties` (or as environment variables):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_attendance
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

spring.data.redis.host=localhost
spring.data.redis.port=6379

jwt.secret=your_jwt_secret

cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

face.service.url=http://localhost:8000

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email
spring.mail.password=your_app_password
```

### Build and run

```bash
mvn clean install
mvn spring-boot:run
```

Or with Docker:

```bash
docker build -t smart-attendance .
docker run -p 8080:8080 --env-file .env smart-attendance
```

The web UI will be at `http://localhost:8080`.

---

## Related repositories

- Android app: https://github.com/cp-team23/SmartAttendanceApp
- Face recognition service: https://github.com/cp-team23/smart-attendance-face-match-service
