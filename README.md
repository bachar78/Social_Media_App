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
   ### 1. for loging 
    - POST: "/api/v1/login" for the user authentication
   ### 2. for user:
    - POST:"/api/v1/users" For creating a user account 
    - GET: "/api/v1/users" For fetching all the users
    - GET: "/api/v1/users/username" for fetching a specific user
    - PUT: "/api/v1/users/id" for updating a user profile
    - DELETE: "/api/v1/users/id" for deleting a user account
   ### 3. for post:
    - POST:"/api/v1/posts" for creating a post
    - GET: "/api/v1/posts" for fetching all the posts
    - GET: "/api/v1/username/posts" for fetching the posts of a specific user
    - DELETE: "/api/v1/posts/id" for deleting the user account



## 4. Further Improvements
- Enhancement of README
- Add JUnit Test 
- Add Images to the posts and user entitis
