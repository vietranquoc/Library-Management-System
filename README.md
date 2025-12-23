# Library Management System - Angular + Spring Boot

A full-stack **Library Management System** built as a pet project to deepen my knowledge in **Angular (frontend)** and **Spring Boot (backend)** with a real-world use case.

### Core Features

- **Books & Copies Management**  
  - Each book has a unique ID  
  - Support multiple authors per book  
  - Library can own multiple physical copies of the same book  
  - Track individual copy status (available, borrowed, reserved)

- **Powerful Search**  
  Members can search books by title, author(s), genre, or publication year.

- **Borrowing & Reservation System**  
  - Members can borrow available copies (records borrow date)  
  - If all copies are borrowed → members can **reserve** the book (queue system)  
  - Automatic fine calculation if book is not returned within **7 days**  
  - Members can view and pay accumulated fines

- **Admin / Librarian Dashboard**  
  - View who currently borrows a specific copy  
  - Full borrowing history of any book or member  
  - Member management (join date, status, outstanding fines, etc.)

### Tech Stack

- **Frontend**: Angular 17+, TypeScript, Angular Material (or Tailwind/Bootstrap – tùy bạn chọn)  
- **Backend**: Spring Boot 3+, Spring Data JPA, Spring Security  
- **Database**: MySQL 
- **Authentication**: JWT or Spring Security Session
- **API**: RESTful API with clear endpoints & OpenAPI/Swagger documentation

### Purpose
This is a **learning-oriented pet project** aimed at mastering:
- Clean Architecture & layered design in Spring Boot
- State management & reactive forms in Angular
- Full-stack integration (HTTP client, interceptors, guards, lazy loading…)
- Database design for many-to-many relationships (Book ↔ Author, Book ↔ Copies ↔ Borrowing)

Feel free to fork, star ⭐, or contribute! Suggestions and pull requests are very welcome.

Happy coding! 📚
