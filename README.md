# AllRoute – Public Transport Ticketing System

**AllRoute** is a web-based ticketing and travel card management system for multi-modal public transport (bus, train, metro).  
It allows users to register, top-up travel cards using various payment methods, and purchase or view tickets — all in one app.

---

##  Features

###  User Portal
- Registration & login
- View dashboard & travel history
- Recharge travel card via:
  - Apple Pay
  - Google Pay
  - Credit/Debit Card
- Purchase & manage tickets
- Route search and ticket confirmation

###  Admin Portal
- Admin dashboard
- Manage transport routes
- View system notifications

---

##  Tech Stack

| Layer        | Technology                    |
|--------------|-------------------------------|
| Backend      | Spring Boot 3 (Java 21)       |
| Frontend     | Thymeleaf (HTML, CSS)         |
| Security     | Spring Security               |
| ORM          | Spring Data JPA + Hibernate   |
| Database     | MariaDB                       |
| Build Tool   | Maven                         |
| View Engine  | Thymeleaf                     |

---

##  Getting Started

###  Prerequisites

- Java 21+
- Maven 3.8+
- MariaDB or MySQL

###  Database Setup

Create a database:

```sql
CREATE DATABASE transport_db;
