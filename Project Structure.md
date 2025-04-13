src/
├── config/                  # Spring Security & JWT setup
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   └── SecurityConfig.java
├── exception/               # Global error handling
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── CustomValidationException.java
│   └── ErrorResponse.java
├── auth/                   # Auth logic
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── AuthRequest.java
│   ├── RegisterRequest.java
│   └── AuthResponse.java
├── user/                   # User domain
│   ├── User.java
│   ├── Role.java
│   ├── UserRepository.java
│   ├── UserService.java
├── chat/                   # Chat domain
│   ├── Message.java
│   ├── MessageRepository.java
│   ├── ChatController.java
│   └── ChatService.java
└── Application.java
