# XYZ Auth Service

This is the **Auth Service** for a microservice-based system. It handles user registration, authentication, OTP verification, password reset, and email notification flows.

## Tech Stack

- Java 21
- Spring Boot 3.5.3
- Maven
- JUnit + MockMvc for unit testing
- Maria DB 11.2

## Features

- Register using email and password
- Email OTP verification
- Resend OTP after 1 minute
- Expired OTP after 5 minutes
- Login with email and password
- Forgot password and reset password with token

## API Endpoints

All endpoints are prefixed with `/api/v1/auth`

| Method | Endpoint                     | Description                     |
|--------|------------------------------|---------------------------------|
| POST   | /signup                      | Register with email/password    |
| POST   | /verify-otp                  | Submit OTP for verification     |
| POST   | /resend-otp                  | Resend OTP                      |
| POST   | /login                       | Login with email/password       |
| POST   | /forgot-password             | Send reset link to email        |
| POST   | /reset-password              | Submit new password using token |

## Running the Tests

```bash
mvn clean test
