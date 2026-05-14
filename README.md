# 🏥 PharmaRestock (Galle Drugs)

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.9-purple?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Database-Room%20SQLite-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/PDF-Native%20PdfDocument-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Offline-First-success?style=for-the-badge" />
</p>

<p align="center">
  <b>Advanced Pharmacy Inventory & Restock Manager</b><br>
  Built with Kotlin, Jetpack Compose, Room Database, and MVVM Architecture.
</p>

---

# 📖 Overview

**PharmaRestock** is a production-level Android application designed to modernize pharmacy inventory and restock workflows.

The application replaces traditional paper-based stock management with a fast, offline-first digital workflow that allows pharmacists to:

- Search medicines instantly
- Add restock quantities quickly
- Generate professional PDF order sheets
- Share restock orders directly through WhatsApp
- Operate completely offline using Room Database

This project demonstrates real-world Android engineering practices including local persistence, MVVM architecture, Jetpack Compose UI development, native PDF generation, and secure Android file sharing.

---

# 🚀 Features

## 📦 Master Inventory Management

- Add new medicines quickly
- Full CRUD operations (Create, Read, Update, Delete)
- Real-time search filtering
- Offline Room SQLite database storage
- Category and dosage management

---

## 🛒 Daily Restock Workflow

- Tap-to-add restock system
- Quantity selection dialog
- Dynamic cart badge updates
- Review order summary screen
- Swipe-to-delete incorrect entries

---

## 📄 Native PDF Generation

- Built using Android `PdfDocument`
- Professional table-style layouts
- Dynamic headers with:
  - Date
  - Pharmacist Name
  - Branch Name
- Lightweight offline PDF creation

---

## 💬 WhatsApp Integration

- Secure file sharing using `FileProvider`
- Share generated PDFs directly to WhatsApp
- Android `ACTION_SEND` integration
- Automatic daily cart cleanup after successful sharing

---

## ⚙️ Settings & Preferences

- Save pharmacist details
- Save branch information
- Persistent local settings
- Database export support
- Danger Zone for database reset

---

# 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Architecture | MVVM |
| Database | Room Database |
| Async Operations | Kotlin Coroutines + Flow |
| PDF Engine | PdfDocument |
| Navigation | Navigation Compose |
| Storage | SharedPreferences / DataStore |
| File Sharing | FileProvider |
| IDE | Android Studio |

---

# 🏗 Architecture

PharmaRestock follows a modern **Local-First MVVM Architecture**.

```text
UI Layer (Compose Screens)
        ↓
ViewModel Layer
        ↓
Repository Layer
        ↓
Room Database
```

### Architecture Benefits

- Clean separation of concerns
- Easier debugging
- Scalable codebase
- Reactive UI updates
- Better maintainability

---

# 📂 Project Structure

```text
com.gayan.pharmarestock/

│
├── data/
│   ├── database/
│   ├── dao/
│   ├── entities/
│   ├── repository/
│   └── preferences/
│
├── ui/
│   ├── screens/
│   ├── components/
│   ├── navigation/
│   └── theme/
│
├── viewmodel/
│
├── utils/
│   ├── pdf/
│   ├── csv/
│   └── whatsapp/
│
├── workers/
│
└── MainActivity.kt
```

---

# 🗄 Database Structure

## medicines Table

| Field | Type |
|---|---|
| id | Int (Primary Key) |
| name | String |
| category | String |
| dosage | String |
| barcode | String? |

---

## cart_items Table

| Field | Type |
|---|---|
| id | Int (Primary Key) |
| medicineId | Int |
| medicineName | String |
| quantity | Int |

---

# 🔄 App Workflow

```text
Setup User Details
        ↓
Populate Medicine Inventory
        ↓
Search & Add Low Stock Medicines
        ↓
Review Daily Restock Cart
        ↓
Generate PDF
        ↓
Share via WhatsApp
```

---

# 📱 Application Screens

## 🏠 Daily Restock Screen
- Real-time medicine search
- Fast tap-to-add workflow
- Quantity dialog system

---

## 🛒 Review Order Screen
- Cart summary
- Remove incorrect items
- Generate PDF button

---

## 📦 Inventory Management Screen
- Add/Edit/Delete medicines
- Search inventory
- Medicine management tools

---

## ⚙️ Settings Screen
- Pharmacist profile
- Branch details
- Database export/reset tools

---

# 📄 PDF Generation System

The app uses Android’s native:

```kotlin
android.graphics.pdf.PdfDocument
```

Generated PDFs include:

- Pharmacy name
- Date
- Pharmacist details
- Medicine restock tables
- Quantity summaries

---

# 💬 WhatsApp Sharing Flow

```text
Generate PDF
        ↓
Save to Cache Directory
        ↓
Create Secure URI
        ↓
Launch WhatsApp Share Intent
```

---

# 📦 Installation

## Requirements

- Android Studio Flamingo or newer
- Android SDK 26+
- Kotlin 1.9+
- WhatsApp installed on test device

---

## Steps

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/PharmaRestock.git
```

---

### 2. Open Project

Open the project in Android Studio.

---

### 3. Sync Gradle

Allow Gradle to download dependencies.

---

### 4. Run Application

Connect an Android device or emulator and click:

```text
Run ▶
```

---

# 🧪 Future Improvements

## 📷 Barcode Scanner
- CameraX integration
- ML Kit barcode scanning

---

## ☁ Cloud Backup
- Google Drive sync
- Firebase Storage integration

---

## 📊 Analytics Dashboard
- Weekly medicine trends
- Monthly restock reports
- Usage analytics

---

## 📁 CSV Import/Export
- Bulk medicine import
- Excel compatibility
- Backup restoration

---

# 🧠 Learning Objectives

This project demonstrates:

- Modern Android Development
- MVVM Architecture
- Jetpack Compose UI
- Room Database Integration
- Kotlin Coroutines & Flow
- Native Android PDF APIs
- Android FileProvider Security
- Offline-first application design

---

# 📸 Screenshots

> Add your screenshots here after building the UI.

```text
screenshots/
├── home.png
├── cart.png
├── inventory.png
└── settings.png
```

---

# 🤝 Contributing

Contributions are welcome.

If you'd like to improve the project:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a pull request

---

# 📜 License

This project is licensed under the MIT License.

---

# 👨‍💻 Author

## Gayan

Production-grade Android pharmacy inventory & restock management system built using Kotlin and Jetpack Compose.

---

# ⭐ Support

If you found this project useful:

- Star the repository
- Share it with others
- Contribute improvements

---

<p align="center">
  <b>PharmaRestock — Designed for real-world pharmacy workflows.</b>
</p>
