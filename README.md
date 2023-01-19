# Social-Media-App-with-Srping-Security
- Social Media App is a backend code for a social media application where the user can create acount and share posts with every one else has account in the same application. It it coded with Test Driven Development (TDD).
- It provides API's for creating, updating, fetching and deleting user's account. And API's for creating fetching and deleting Posts.
- If the user delete his account, then his posts will be deleted automatically. That it is not logical to have posts without their authors. 

## 1.Used Technologis
- Java 17
- Spring Boot 3
- Spring Security
- Spring Boot Data Jpa with h2 Database Memory 

## 2. Code structure

```
Social Media App
├── .mvn/wrapper
└── src
|   └── main
|   |  └── Java
|   |  |      └── Post
|   |  |      └── configuration
|   |  |      └── error
|   |  |      └── shared
|   |  |      └── user
|   |  |      └── DemoApplication.java
|   |  └── resources
|   └── Test
|   |   └── postIntegrationTest
|   |   |           └── postControllerTest.java
|   |   └── userIntegrationtest
|   |   |           └── LoginControllerTest.java
|   |   |           └── TestPage.java
|   |   |           └── UserControllerTest.java
|   |   |           └── UserRepositoryTest.java
|   |   └── TestUtil.java
```

<p align="right">(<a href="#top">back to top</a>)</p>

## 3. Provided API's:
### 1. for loging in and regestration 
    - POST: "/api/v1/login" 
### 2. for user:
    - POST:"/api/v1/users"
    - GET: "/api/v1/users"
    - GET: "/api/v1/users/username"
    - PUT: "/api/v1/users/id"
    - DELETE: "/api/v1/users/id"



## 4. Further Improvements
- Enhancement of README
- Add JUnit Test 
- Add Images to the posts and user entitis
