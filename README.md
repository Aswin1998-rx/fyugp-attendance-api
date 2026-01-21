

# 🎓 FYUGP Attendance System (Backend)

A robust and scalable backend service built to manage **college classroom attendance** under the **FYUGP (Four Year Undergraduate Programme)** framework.  
This system handles student, faculty, subject, and attendance management with secure authentication and data integrity.

---

## 🚀 Tech Stack
- **Java 17**
- **Spring Boot 3+**
- **Spring Data JPA**
- **MySQL**
- **Flyway Migration**
- **OpenAPI (Swagger UI)**
- **Docker (Optional)**

---

## 🧩 Features
- ✅ Secure authentication & role-based authorization
- 📚 Manage students, faculty, subjects, and classes
- 🕒 Mark and track daily attendance
- 📊 Generate attendance reports
- 🔄 RESTful API design with OpenAPI documentation
- 🧠 Clean architecture with service-layer separation

---

## 🧱 Project Structure
## Prerequisite
For building and running the application you would need the following.
- java >= 17
- <details><summary>SQL server</summary>

  To boot an sql server run the following script on the project directory.
  ```bash
  docker compose up db-dev -d
  ```
  To login to the sql shell run the following command
  ```bash
  docker compose exec -it db-dev sh -c '/opt/mssql-tools/bin/sqlcmd -U SA -P ${MSSQL_SA_PASSWORD} -d ${MSSQL_DATABASE} -y 30 -Y 30'
  ```
  or use the official installation procedure as mentioned [here](https://www.microsoft.com/en-in/sql-server/sql-server-downloads).
  </details>
- env variables specified in [.env.example](./.env.example) template


## Dev Environment
To setup the dev environment follow the steps bellow.
1. run the required dependencies
   ```bash
   docker compose --profile infra up -d
   ```

## Running application
Make sure the prerequisites are met.
- ### Locally
    1. build the application
         ```bash
         ./mvnw clean package -DskipTests
         ```
    2. Run your application to work with the [dev environment](#dev-environment) setup earlier
       ```bash
       ./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev
       ```
       or configure your env variables as per [.env.example](./.env.example) template. <br>
       And run the application via your IDE's runtime or via maven
        ```bash
        ./mvnw clean spring-boot:run
        ```
- ### Development
    - run the following command to boot up a dev server
      ```bash
      docker compose --profile dev up --build -d
      ```
- ### Production
    - configure your env variables as per [.env.example](./.env.example) template.
    - run the following
      ```bash
      docker compose --profile prod up --build -d
      ```
