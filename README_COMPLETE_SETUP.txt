EMPLOYEE MANAGEMENT SYSTEM - COMPLETE PROJECT
================================================

1. Open THIS folder directly in IntelliJ IDEA:
   EmployeeManagementSystem_Improved_Professional_COMPLETE

2. Make sure the Project SDK is JDK 21 (or another compatible JDK).

3. The Java source root is:
   src/

4. Main class:
   GUI.Main

5. MySQL:
   - Start MySQL 8.x.
   - Run database_setup.sql for a new database.
   - If you already have an older employee_db database, run:
       migration_fix_existing_database.sql
   - Database connection settings are in:
       src/database/DatabaseConnection.java

6. MySQL Connector/J:
   Add your installed MySQL Connector/J JAR to IntelliJ:
   File -> Project Structure -> Libraries -> + -> Java
   Select your mysql-connector-j-*.jar.

7. Input fields:
   All project text input fields use black typed text on the light-blue
   field background. This is centralized in GUI.Theme and also applied
   to Swing text-field defaults.

8. The project source was checked with javac. All 35 Java source files
   compile successfully without Java syntax/type errors.
