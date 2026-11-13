# My Task API

A Spring Boot REST API for My Task, my first full-stack portfolio project.

[Live demo](https://task-web-pva7.onrender.com) · [Angular frontend](https://github.com/rowel-v/task-web)

## Features

- Create, view, update, and delete tasks (CRUD).
- Set priorities and due dates, and archive completed tasks.
- Register, sign in, and manage your profile.

## Security

- Spring Security authenticates users with JWTs stored in HttpOnly cookies.
- BCrypt hashes passwords, and CSRF protection covers requests that change data.

## Deployment

The API runs in a Docker container on Render. Neon hosts the PostgreSQL database.