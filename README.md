# Travel Guide Backend

Travel Guide Backend is a RESTful API for a travel blog / magazine application.  
It manages user authentication, role-based access control, posts, destinations, activities, comments, and user favorites, backed by a MySQL database.

The project is implemented in Java using JAX-RS (Jersey), HK2 for dependency injection, and JWT for authentication.

---

## Table of Contents

- Overview
- Architecture
- Domain Model
- Features
- Tech Stack
- Security
  - Authentication
  - Authorization
- API Overview
  - Users & Authentication
  - Posts
  - Destinations
  - Activities
  - Comments
- Running Locally
  - Database Configuration
  - Build & Deploy
- Possible Improvements

---

## Overview

This backend powers a travel-oriented web magazine application with a focus on editorial-style posts, curated destinations, and activity tags.

Key capabilities include:

- User registration and login with password hashing.
- JWT-based authentication and SecurityContext integration.
- Role-based access control (`USER`, `CREATOR`, `ADMIN`).
- CRUD operations for posts, destinations, and activities.
- Commenting system for authenticated users.
- Favorite posts per user.
- Basic analytics (most viewed posts over a period).

The API is exposed under the base path: `/api`.

---

## Architecture

The application follows a layered architecture:

### Resources (REST controllers)

Package: `rs.raf.travel_guide_backend.resources`  
JAX-RS resources exposing HTTP endpoints (Users, Posts, Destinations, Activities, Comments).

### Services (business logic)

Package: `rs.raf.travel_guide_backend.services`  
Implements application logic, role checks, and delegates persistence to repositories.  
Class `AbstractIsAuthorized` provides a base authorization strategy for services.

### Repositories (data access)

Package: `rs.raf.travel_guide_backend.respositories`  
MySQL-based repositories using plain JDBC for data access.  
`MySQLAbstractRepository` centralizes connection handling.

### Entities (domain models)

Package: `rs.raf.travel_guide_backend.entities`  
Domain objects such as `User`, `Post`, `Destination`, `Activity`, `Comment`, and `Role`.

### Security & Filters

- `rs.raf.travel_guide_backend.filter.AuthFilter` – JWT authentication and security context setup.
- `rs.raf.travel_guide_backend.filter.Cors` – CORS configuration.
- `rs.raf.travel_guide_backend.security.UserPrincipal` – authenticated user representation.

### DTOs / Requests

Package: `rs.raf.travel_guide_backend.requests`  
Contains `LoginRequest` and `LoginResult`.

### Application bootstrap

Class: `rs.raf.travel_guide_backend.HelloApplication`  
Extends `ResourceConfig`, configures DI bindings and scans the application package.  
Application is exposed under `/api`.

---

## Domain Model

### User

- Fields: `id`, `name`, `surname`, `email`, `role`, `hashedPassword`, `status`
- Roles: `USER`, `CREATOR`, `ADMIN`
- `status` indicates whether the account is active/suspended.

### Post

- Fields: `id`, `title`, `content`, `author_id`, `created_at`, `destination_id`, `cover`
- Associations:
  - `List<Activity> activities`
  - `List<Comment> comments`
- Aggregated metadata:
  - `author_name`
  - `view_count` (stored in DB)

### Destination

- Fields: `id`, `name`, `description`, `cover`
- One-to-many relationship with `Post`.

### Activity

- Fields: `id`, `name`
- Many-to-many with `Post` via `post_activities`.

### Comment

- Fields: `id`, `author_id`, `content`, `created_at`, `author_name`
- Associated with a `Post`.

### Role

- Enum: `USER`, `ADMIN`, `CREATOR`.

---

## Features

### Authentication & Users

- Email/password login.
- Passwords hashed using SHA-256.
- JWT issued on successful login, containing:
  - `sub` (subject) = user email
  - `role` claim with the user’s role.
- User management endpoints (admin only for sensitive operations):
  - List users
  - Update users
  - Change user status (activate/suspend)

### Role-Based Access Control

Role behaviour:

- `USER`:
  - Browse all public content.
  - View posts, destinations, activities.
  - Comment on posts.
  - Mark posts as favorites and manage favorites.
- `CREATOR`:
  - All `USER` capabilities.
  - Create, update, and delete posts, destinations, and activities.
- `ADMIN`:
  - All `CREATOR` capabilities.
  - Manage users (e.g., toggle status).

Service classes typically extend `AbstractIsAuthorized`, which by default allows `ADMIN` and `CREATOR` for protected operations, while resources can additionally enforce their own constraints.

### Content Management

#### Posts

- CRUD operations for posts.
- Each post:
  - Has a single destination.
  - Can have multiple activities (via join table).
  - Tracks view counts.
- Filtering:
  - By destination
  - By activity
- “Most viewed”:
  - Top posts over the last 365 days based on `view_count`.

#### Destinations

- CRUD operations for destinations.
- Cannot be deleted if there are posts associated with it (integrity check in repository).

#### Activities

- CRUD operations for activities.
- Linked to posts; used for filtering and tagging.

### Comments

- Authenticated users can comment on posts.
- Each comment stores author, content, timestamp, and `author_name`.

### Favorites

- Users can manage a list of favorite posts:
  - Add post to favorites.
  - Remove from favorites.
  - List favorite posts for the authenticated user.

---

## Tech Stack

- Language: Java
- Framework: JAX-RS (Jersey)
- DI Container: HK2
- Database: MySQL
- Persistence: Plain JDBC (via `MySQLAbstractRepository`)
- Auth: JWT (`java-jwt` by Auth0)
- JSON: Jackson
- Validation: Bean Validation (`javax.validation`)
- Application Server: Any JAX-RS-compatible (Tomcat, Payara, WildFly, etc.)

---

## Security

### Authentication

Login endpoint:

- `POST /api/users/login`
  - Request body: JSON with `email` and `password` fields.
  - On success: returns a JSON object containing a `jwt` token.

Passwords are never stored in plaintext; they are hashed using SHA-256 before being persisted.

JWT generation (simplified):

- Secret: HMAC256 (for example `"secret"` in development)
- Claims:
  - subject (`sub`) = user email
  - `role` = string representation of the user’s role

In production, the JWT secret should be stored in configuration or environment variables, not hard-coded in the source code.

### Authorization

- `AuthFilter` is registered as a `ContainerRequestFilter` with `@Priority(Priorities.AUTHENTICATION)`.
- For each request:
  1. Checks if the path requires authentication (`isAuthRequired`).
  2. If yes:
    - Reads the `Authorization` header (`Bearer <token>`).
    - Validates the JWT.
    - Extracts the email from the token.
    - Loads the user from the database.
    - Checks that `status = true`.
    - Wraps the user into a `UserPrincipal` and sets it into `SecurityContext`.
  3. Delegates fine-grained checks to services via `isAuthorized`.

Access to the authenticated user in resources:

- Injected field: `@Context SecurityContext securityContext`
- Principal: `(UserPrincipal) securityContext.getUserPrincipal()`

---

## API Overview

All endpoints are served under the `/api` base path.

### Users & Authentication

- `POST /users/login`  
  Authenticate user, return JWT token.  
  Request: `LoginRequest { email, password }`  
  Response: `LoginResult { status, jwt }`

- `POST /users/register`  
  Register a new user.  
  Request: `User` payload (email, name, surname, password, etc.)  
  Access: Public.

- `GET /users`  
  List all users.  
  Access: Admin only.

- `GET /users/{id}`  
  Get user by ID.  
  Access: Admin only (or as defined by service logic).

- `PUT /users/{id}`  
  Update user details.  
  Access: Admin only.

- `PUT /users/changeStatus/{id}`  
  Toggle user status (active/suspended).  
  Access: Admin only.

### Posts

- `GET /posts`  
  Get all posts with associated activities, comments, and author info.

- `GET /posts/{id}`  
  Get a single post by ID.  
  Also increments view count on each access.

- `POST /posts/{destinationId}`  
  Create a new post for a given destination.  
  Access: `CREATOR` or `ADMIN`.  
  Request body: `Post` (title, content, cover, activities, etc.)  
  Author is derived from the authenticated user (JWT).

- `PUT /posts/{id}`  
  Update an existing post.  
  Access: `CREATOR` or `ADMIN`.

- `DELETE /posts/{id}`  
  Delete a post.  
  Access: `CREATOR` or `ADMIN`.

- `GET /posts/destFilter/{id}`  
  Get posts filtered by destination ID.

- `GET /posts/aktFilter/{id}`  
  Get posts filtered by activity ID.

- `GET /posts/theMostViewed`  
  Get up to 10 most viewed posts in the last 365 days.

#### Favorites

- `GET /posts/favorites`  
  Get favorite posts for the authenticated user.  
  Access: Authenticated.

- `POST /posts/favorites/{id}`  
  Add a post to favorites for the authenticated user.  
  Access: Authenticated.

- `DELETE /posts/favorites/{id}`  
  Remove a post from favorites for the authenticated user.  
  Access: Authenticated.

### Destinations

- `GET /destinations`  
  List all destinations.

- `GET /destinations/{id}`  
  Get destination by ID.

- `POST /destinations`  
  Create a new destination.  
  Access: `CREATOR` or `ADMIN`.

- `PUT /destinations/{id}`  
  Update an existing destination.  
  Access: `CREATOR` or `ADMIN`.

- `DELETE /destinations/{id}`  
  Delete a destination only if no posts are associated with it.  
  Access: `CREATOR` or `ADMIN`.

### Activities

- `GET /activities`  
  List all activities.

- `GET /activities/{id}`  
  Get activity by ID.

- `POST /activities`  
  Create a new activity.  
  Access: `CREATOR` or `ADMIN`.

- `DELETE /activities/{id}`  
  Delete an activity.  
  Access: `CREATOR` or `ADMIN`.

### Comments

- `POST /comments/{postId}`  
  Add a comment to the given post.  
  Access: Authenticated user.  
  Author is derived from the JWT principal.

- `DELETE /comments/post/{postId}/comment/{commentId}`  
  Delete a specific comment for a given post.  
  Access: Depends on service rules (e.g. admin, author, etc.).

---

## Running Locally

### Database Configuration

Default MySQL configuration in `MySQLAbstractRepository`:

- Host: `localhost`
- Port: `3306`
- Database name: `travel_guide`
- Username: `root`
- Password: (empty by default)

You can change these values in `MySQLAbstractRepository` to match your local environment or move them to external configuration.

Create the database (example):

- `CREATE DATABASE travel_guide CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

Set up the required tables (`users`, `posts`, `destinations`, `activities`, `post_activities`, `comments`, `user_favorites`, etc.) based on your schema.

### Build & Deploy

Using Maven:

- `mvn clean package`

This produces an artifact (for example, a WAR file) that can be deployed to a JAX-RS-compatible application server (Tomcat, Payara, WildFly, etc.).

Once deployed, the API is available at:

- `http://localhost:<port>/<context-path>/api`

## Demo accounts (dev only)

Use these **demo accounts** for local testing or to seed your MySQL database.  
**Do NOT use in production.**

### Seed script (example)
You can populate your database with sample users by running:

```sql
INSERT INTO users (name, surname, email, role, hashedPassword, status)
VALUES
('Admin',   'User', 'admin@example.com',   'ADMIN',   SHA2('Admin123!', 256),   TRUE),
('Creator', 'User', 'creator@example.com', 'CREATOR', SHA2('Creator123!', 256), TRUE),
('Regular', 'User', 'user@example.com',    'USER',    SHA2('User123!', 256),    TRUE);



---

## Possible Improvements

- Externalize database and JWT configuration (secrets, URLs) to environment variables or configuration files.
- Add OpenAPI/Swagger documentation for easier API discovery.
- Implement pagination for list endpoints (posts, comments, etc.).
- Add integration and unit tests for repositories and services.
- Introduce a standardized error response format across all endpoints.
- Provide Docker configuration (for both the application and MySQL) to simplify local setup.



> 🔗 Backend repo: https://github.com/nadjaradojicic/travel-guide-backend  
> 🔗 Frontend repo: https://github.com/nadjaradojicic/travel-guide-frontend

