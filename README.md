# Campus/Ledger CGPA Calculator

A server-rendered student results portal built with Java, Spring Boot, Spring Security, Thymeleaf, Hibernate/JPA, and MySQL. Administrators maintain student records and semester marks. Students register against an institution-created record and can view only their own transcript.

## Features

- Admin dashboard with student counts, search by enrollment ID or name, and student record creation.
- Course mark entry by semester, including course code, course name, credits, and marks out of 100.
- Re-entering a course code for the same student and semester updates the existing result.
- Automatic letter grade, grade point, semester GPA (SGPA), and cumulative GPA (CGPA).
- Student self-registration using an existing enrollment ID and matching institution email.
- Role-based access: admins manage records; students see their own transcript only.
- BCrypt password hashing and CSRF protection for form submissions.
- Responsive Thymeleaf pages for login, registration, admin records, and student transcripts.
- H2-backed tests for grading, CGPA, page rendering, and access rules.

## Technology

- Java 21+
- Spring Boot 3.5.7
- Spring MVC and Thymeleaf
- Spring Security 6
- Spring Data JPA / Hibernate
- MySQL 8.4
- Maven
- H2 for automated tests

## Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer
- Docker with the Compose plugin, or a separately installed MySQL 8-compatible server

Check the installed tools:

```bash
java -version
mvn -version
docker compose version
```

## Run Locally

1. Start the MySQL database from the repository root:

	```bash
	docker compose up -d mysql
	```

	The compose file creates a local database named `cgpa_db` and a database user named `cgpa_user`. The default application connection uses the same local-only values. Do not reuse these sample credentials outside development.

2. Configure the first admin account in the same terminal that will run the app:

	```bash
	export ADMIN_USERNAME=admin
	export ADMIN_PASSWORD='ChangeThisLocalPassword123!'
	```

	The account is created on application startup if that username does not already exist. The password is stored as a BCrypt hash. Changing these variables later does not change an existing account's password.

3. Start the Spring Boot application:

	```bash
	mvn spring-boot:run
	```

4. Open [http://localhost:8080](http://localhost:8080) and sign in with the admin username and password configured above.

5. In **Students**, choose **Add student**. Enter the student's name, unique enrollment ID, institution email, and program.

6. Open the student record and enter course results. A course code identifies a course within one semester for that student. Saving that combination again updates its name, credits, and marks.

7. Give the student their enrollment ID and the email entered on their record. They can choose **Create a student account** on the sign-in page, set a password, and then sign in using their enrollment ID.

8. The student transcript displays course results, semester summaries, total credits, SGPA, and CGPA. Student accounts cannot open admin routes or request another student's transcript.

Stop the database when finished:

```bash
docker compose down
```

The named Docker volume retains database contents. To remove the database and all local records, run `docker compose down -v`.

## Configure a Separate MySQL Server

The default connection is `jdbc:mysql://localhost:3306/cgpa_db`. Override the defaults with environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/cgpa_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | JDBC URL |
| `DB_USERNAME` | `cgpa_user` | MySQL username |
| `DB_PASSWORD` | `local_cgpa_password` | MySQL password |
| `ADMIN_USERNAME` | empty | Username for the initial admin account |
| `ADMIN_PASSWORD` | empty | Password for the initial admin account |

For example, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to match a database created by your MySQL administrator. The application cannot create a missing server or grant database privileges. Keep production credentials in a secret manager or deployment environment, never in source control.

## Grading and CGPA Calculation

Marks are converted to grade points using the following default 10-point scale. Change `GradeScale` if an institution uses a different policy.

| Marks | Letter grade | Grade points |
| --- | --- | ---: |
| 90–100 | O | 10 |
| 80–89.99 | A+ | 9 |
| 70–79.99 | A | 8 |
| 60–69.99 | B+ | 7 |
| 50–59.99 | B | 6 |
| 45–49.99 | C | 5 |
| 40–44.99 | P | 4 |
| Below 40 | F | 0 |

Each course contributes its grade point multiplied by its credits. The GPA is the sum of those weighted points divided by the sum of the credits, rounded to two decimal places:

```text
SGPA = sum(course grade points * course credits in a semester)
		 / sum(course credits in that semester)

CGPA = sum(course grade points * course credits across all semesters)
		 / sum(course credits across all semesters)
```

For example, a 4-credit course with 95 marks (10 points) and a 2-credit course with 75 marks (8 points) produce an SGPA of `(4*10 + 2*8) / (4+2) = 9.33`.

## Application Walkthrough

1. **Start-up and schema:** Spring Boot reads the database and admin settings from `application.properties`. Hibernate creates or updates the local tables for students, user accounts, and course marks.
2. **Admin bootstrap:** `AdminAccountInitializer` creates an `ADMIN` account from `ADMIN_USERNAME` and `ADMIN_PASSWORD` when that username does not already exist. No default administrator password is hard-coded.
3. **Sign-in:** Spring Security authenticates both roles using the database-backed `UserDetailsService`. Passwords are checked with BCrypt. After sign-in, admins land on the student directory and students land on their own transcript.
4. **Student setup:** An administrator creates the student record first. Enrollment IDs and email addresses must be unique. The record's email is the registration check; no student account is created automatically.
5. **Student registration:** The registration form matches the submitted enrollment ID and email against a student record, checks the password confirmation, then creates a `STUDENT` account whose username is the enrollment ID.
6. **Mark entry:** An admin searches for a student, opens the result page, and records each course, semester, credits, and mark. Input validation limits semesters to 1–12, credits to 1–10, and marks to 0–100.
7. **Grade calculation:** `GradeScale` maps a mark to a letter and grade point. `AcademicSummaryService` computes credit-weighted SGPA and CGPA. Results are calculated from stored marks instead of duplicated in the database.
8. **Transcript access:** The student controller gets the enrollment ID from the authenticated principal, loads the linked student record, and renders the transcript. A student cannot select another enrollment ID in the URL or search other students.
9. **Persistence:** `Student`, `AppUser`, and `CourseMark` are Hibernate entities. Spring Data repositories handle persistence and lookup. Course code is unique per student and semester.
10. **Sign-out:** The shared navigation submits Spring Security's logout form. CSRF protection remains enabled for state-changing requests.

## Routes

| Route | Access | Purpose |
| --- | --- | --- |
| `/login` | Public | Sign in |
| `/register` | Public | Create a student account for an existing record |
| `/dashboard` | Authenticated | Redirect to the role-specific dashboard |
| `/admin` | Admin | Search and browse students |
| `/admin/students/new` | Admin | Create a student record |
| `/admin/students/{id}` | Admin | View results and enter marks |
| `/student/dashboard` | Student | View personal transcript and CGPA |

## Run Tests

Tests use an in-memory H2 database; they do not require Docker or MySQL.

```bash
mvn test
```

The suite covers grade boundaries, credit-weighted calculations, Thymeleaf view rendering, form validation, and role access. Build a runnable jar with:

```bash
mvn package
java -jar target/cgpa-calculator-0.0.1-SNAPSHOT.jar
```

## Project Structure

```text
src/main/java/com/example/cgpa/
  config/       Initial admin account setup
  model/        JPA entities and user roles
  repository/   Spring Data repositories
  security/     Authentication and route authorization
  service/      Grade scale and GPA calculations
  web/          MVC controllers and validated forms
src/main/resources/
  static/css/   Responsive application styles
  templates/    Thymeleaf login, registration, admin, and student pages
src/test/java/  Unit and Spring MVC integration tests
docker-compose.yml
pom.xml
```

## Notes for Production

- Replace `ddl-auto=update` with a reviewed schema migration workflow such as Flyway or Liquibase.
- Set strong database and admin secrets outside the repository and use HTTPS.
- Review the sample grade scale, student identity verification process, retention requirements, and access policies with the institution.
- The initial admin bootstrap creates an account only once; it is not an admin password reset mechanism.