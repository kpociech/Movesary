<h1 align="center">
  <a href="https://movesary.herokuapp.com"><img src="https://lh3.googleusercontent.com/pw/AM-JKLWKNMibOzQBehcW-3PoADXSBevX1XuHInx-nEWZiUPkcLA9eT6CfP-9xIhuLqxr5xDwIhXDvNhATOBBtiVBkwMCFOvb08mPM635EzaXXr6FYRVYNzao20kD_O8PBBLkDOUrmBhHfkZroRsIMZvchohG=w1126-h281-no?authuser=0" alt="Movesary" width="600"></a> 
</h1>
<h4 align="center">Movesary - Library for your moves</h4>
<p align="center">
Open App here: <a href="https://movesary.herokuapp.com">https://movesary.herokuapp.com</a>
</p>


# Overview
[Movesary](https://movesary.herokuapp.com) project was created in order to help me develop skills to become Java Backend Developer.
# Tech stack
- `SpringBoot` application
  - CRUD REST API that validates bearer token implemented with Keycloak
  - Spring AOP aspect that updates changes in keycloak's user data
  - Database entities are mapped to DTO with use of ModelMapper
  - Database queries are supported with JPQL and Native queries
  - Junit5 and Mockito Tests for every layer of the application
- `Heroku` used as host for applications and `PostgreSQL` database
- `Keycloak` used as identity and access management solution allowing for registration with use of your **email** or **Google Account**
- Application uses git submodule to connect with external frontend application.

# API
[Swagger-UI](https://movesary.herokuapp.com/swagger-ui.html) and [Postman Collection](https://github.com/kpociech/Movesary/tree/main/postmanCollection)
