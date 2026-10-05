# PetCareSys

A desktop pet care management system built with **Java Swing** and **MySQL**, created as school coursework. It lets pet owners, veterinarians, and administrators manage pets, appointments, and treatments from one application, with role-based dashboards and printable reports.

## Features

The app has three user roles, each with its own dashboard:

| Role | What they can do |
| --- | --- |
| **Pet Owner** | Register and log in, add/edit their pets, book appointments with a vet, view treatment and medical history, generate reports |
| **Vet** | View their appointments and patients, add/edit treatments, view medical records, generate reports |
| **Admin** | Manage users (add/edit), manage appointments and pets, view system activity, generate reports |

Other highlights:

- Login and registration screens
- Activity logging of user actions
- PDF-style reports (appointments, pets, treatments, users, medical) using JasperReports
- Modern light UI using FlatLaf
- Structured with MVC, DAO, and Factory patterns

## Prerequisites

Make sure you have the following installed:

- **Java 21 or later**. Check with `java -version`.
- **MySQL Server** (or MariaDB / XAMPP, which the database was exported from), running locally.
- **A MySQL client** to import the database, such as MySQL Workbench, phpMyAdmin, or the `mysql` command line.

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/burntbasic/PetCareSys.git
cd PetCareSys
```

### 2. Set up the database

Import the provided SQL file. It creates the `PetCareSysDB` database and all tables.

**Option A: command line**

```bash
mysql -u your_username -p < database/PetCareSysDB.sql
```

**Option B: phpMyAdmin / MySQL Workbench**

Open the tool, choose *Import*, and select `database/PetCareSysDB.sql`.

### 3. Configure the database connection

The real config file is git-ignored so credentials aren't committed. Create your own from the example:

```bash
cp config/database.properties.example config/database.properties
```

(On Windows, just copy and rename the file in File Explorer.)

Then open `config/database.properties` and fill in your MySQL details:

```properties
db.url=jdbc:mysql://localhost:3306/PetCareSysDB
db.user=your_username
db.password=your_password
```

### 4. Run the application

The app loads `config/` and `reports/` using relative paths, so run it from the **project root folder**, the one containing `PetCareSys.jar`, `config/`, `reports/`, and `lib/`.

```bash
java -jar PetCareSys.jar
```

On most systems you can also double-click `PetCareSys.jar`, as long as it is still inside the project root folder.

The login window should appear.

## First-time use

The database ships **without any user accounts**, so you'll need to create your own:

1. Click **Register** on the login screen to create an account. New accounts are created as **Pet Owners** by default.
2. To get an **Admin** or **Vet** account, register normally, then change that user's `role` to `ADMIN` or `VET` in the `Users` table (for example through phpMyAdmin). Once you have one admin, you can add and edit other users from the admin dashboard.

## Project structure

```
PetCareSys/
├── PetCareSys.jar  Executable JAR (run this)
├── MANIFEST.MF     JAR manifest
├── config/       Database configuration (example file included)
├── database/     SQL script to create the database and tables
├── lib/          Third-party JAR libraries
├── reports/      JasperReports templates (.jrxml) for admin, vet, and pet owner
└── src/com/petcare/
    ├── PetCareSys.java   Application entry point
    ├── model/            Data classes (User, Pet, Appointment, Treatment, Activity)
    ├── dao/              Database access classes
    ├── controller/       Logic connecting the views to the DAOs
    ├── view/             Swing UI (login, register, and per-role dashboards/panels)
    ├── factory/          Creates the right dashboard for each user role
    ├── util/             Report generation and error handling
    └── exception/        Custom exceptions
```

## Troubleshooting

- **"Could not load config/database.properties"**: you haven't created the file in step 3, or you're running the app from the wrong folder. Run it from the project root.
- **Connection or access denied errors**: check that MySQL is running and that the username, password, and URL in `database.properties` are correct.
- **`ClassNotFoundException` / missing class errors**: the `lib/` folder must stay next to `PetCareSys.jar`. Don't move the JAR out of the project folder.
- **Reports don't open**: make sure the database connection works and you're running from the project root so `reports/` can be found.

## Built with

- Java Swing
- [FlatLaf](https://www.formdev.com/flatlaf/) (look and feel)
- [JasperReports](https://community.jaspersoft.com/) (reporting)
- [LGoodDatePicker](https://github.com/LGoodDatePicker/LGoodDatePicker) (date picker)
- MySQL and MySQL Connector/J

## Author

**Author:** Hirunaka Wickramage (GitHub: [burntbasic](https://github.com/burntbasic))  
**Course:** Diploma in Software Engineering  
**Module:** Enterprise Application Development  
**Year:** 2026
