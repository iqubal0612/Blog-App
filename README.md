# Blog Application

## Project Overview

The Blog Application is a web app for managing blog posts and comments. It supports CRUD operations, along with pagination, search, filtering, and sorting features. Initially built using Spring Boot, Spring Data JPA, and Spring Security, it also integrates Thymeleaf for server-side templating.

## Technologies Used

- **Spring Boot**: Core framework for building the application.
- **Spring Data JPA**: For database connectivity and ORM.
- **Spring Security**: For handling authentication and authorization.
- **Thymeleaf**: For server-side templating.

## Project Structure
```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── imam
│   │   │            ├── BlogProjectApplication.java
│   │   │            ├── config
│   │   │            │   └── SecurityConfig.java
│   │   │            ├── controller
│   │   │            │   ├── CommentController.java
│   │   │            │   ├── PostController.java
│   │   │            │   └── UserController.java
│   │   │            ├── model
│   │   │            │   ├── Comment.java
│   │   │            │   ├── Post.java
│   │   │            │   ├── Tag.java
│   │   │            │   └── User.java
│   │   │            ├── repository
│   │   │            │   ├── CommentRepository.java
│   │   │            │   ├── PostRepository.java
│   │   │            │   ├── TagRepository.java
│   │   │            │   └── UserRepository.java
│   │   │            └── service
│   │   │                ├── CommentService.java
│   │   │                ├── PostService.java
│   │   │                ├── TagService.java
│   │   │                └── UserService.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── templates
│   │           ├── create.html
│   │           ├── editComment.html
│   │           ├── editPost.html
│   │           ├── error.html
│   │           ├── list.html
│   │           ├── login.html
│   │           ├── register.html
│   │           └── view.html
│   └── test
│       └── java
│           └── com
│               └── imam
│                   └── BlogProjectApplicationTests.java
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Features

### Part 1: CRUD Operations

- **Read Blog Posts**:
    - Users can browse a list of blog posts displaying the title, excerpt, author, publication date, and tags.
- **View Full Post**:
    - Users can view the full details of a blog post, including the title, content, author, publication date, and tags.
- **Create Post**:
    - Users can create a new blog post by providing the title, content, author, publication date, and tags.
- **Update Post**:
    - Users can update an existing blog post.
- **Delete Post**:
    - Users can delete a blog post.
- **Filter Posts**:
    - Users can filter blog posts by author, published date, and tags.
- **Sort Posts**:
    - Users can sort blog posts by published date.
- **Search Posts**:
    - Users can search blog posts using a full-text search on title, content, author, and tags.
- **Pagination**:
    - Users can navigate through pages of blog posts, with each page displaying a maximum of 6 blog posts.

### Part 2: Authentication & Authorization

- **Authentication**:
    - Implement user login and logout functionality.
- **Authorization**:
    - Different access levels for users, including:
        - **Authors**: Can create, update, and delete their own posts and comment on posts.
        - **Admins**: Can manage all posts and comments, regardless of the author.
        - **Non Logged-In Users**: Can view posts and comments but cannot modify them.

### Part 3: Deployment

- **Deployment on Render**:
    - Instructions and configuration for deploying the application to Render.

## Database Schema

- **User**
    - `id`
    - `name`
    - `email`
    - `password`

- **Posts**
    - `id`
    - `title`
    - `excerpt`
    - `content`
    - `author`
    - `published_at`
    - `is_published`
    - `created_at`
    - `updated_at`

- **Tags**
    - `id`
    - `name`
    - `created_at`
    - `updated_at`

- **Post_Tags**
    - `post_id`
    - `tag_id`
    - `created_at`
    - `updated_at`

- **Comments**
    - `id`
    - `name`
    - `email`
    - `comment`
    - `post_id`
    - `created_at`
    - `updated_at`

## Steps to Develop

1. **Initial Setup**
    - Design the HTML and CSS for the application.
    - Create and configure the database schema.

2. **Implementation**
    - Replace static HTML & CSS with Thymeleaf templates.
    - Implement CRUD operations using Spring Data JPA.
    - Integrate pagination, filtering, sorting, and search functionalities.

3. **Authentication and Authorization (Part 2)**
    - Implement user authentication and authorization.
    - Restrict and grant access based on user roles.

4. **Deployment (Part 3)**
    - Deploy the application to Railway and Render.

## Running the Application

- Ensure the database is set up and configured.
- Build and run the Spring Boot application.
- Access the application through the endpoints.
---
Thank you for checking out the Blog Application. We hope it provides a strong base for your own projects and assists you in creating an effective blogging platform.
