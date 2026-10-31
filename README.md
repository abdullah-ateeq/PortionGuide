# 🥗 PortionGuide - Smart Food Portions & Meal Prep Calculator

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Room DB](https://img.shields.io/badge/Database-Room-4285F4?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

**PortionGuide** is a modern, privacy-first Android application designed to help home cooks, meal preppers, and food professionals divide meals and batch-cooked dishes into exact portions with mathematical precision. By eliminating guesswork and tracking leftover food, PortionGuide streamlines meal planning and reduces food waste.

---

## 🌟 Key Features

- ⚖️ **Dual Calculation Engines**:
  - **Divide by Portions (Equal Servings)**: Split total cooked food (e.g., 1,200g lasagna) evenly among a desired number of portions (e.g., 6 servings = 200g each).
  - **Calculate Portions by Target Size**: Determine how many full servings of a target size (e.g., 180g) can be produced from a total quantity, clearly showing any leftover amount.
- 🥄 **Comprehensive Unit Support & Conversions**:
  - **Weight**: Grams (`g`), Kilograms (`kg`), Milligrams (`mg`), Ounces (`oz`), Pounds (`lb`).
  - **Liquid Volume**: Milliliters (`mL`), Liters (`L`).
  - **Culinary Measurement**: Cups (`cup`), Tablespoons (`tbsp`), Teaspoons (`tsp`).
  - **Discrete Units**: Pieces (`piece`), Slices (`slice`), Servings (`serving`), or Custom defined units.
  - **Unit Converter**: Interactive unit selection dialog and real-time conversion across compatible units.
- 📊 **Visual Portion Breakdown**:
  - Interactive cards displaying individual portion numbers, quantities, and percentage allocation.
  - Real-time adjustment controls (`+` / `-`) on the result screen to recompute portions instantly.
  - Dedicated leftover display highlighting leftover food in weight or volume.
- 📜 **Local Calculation History**:
  - Automatically saves calculation sessions locally in an offline Room SQLite database.
  - Re-open past meal prep sessions, inspect breakdown, or clear history anytime.
- 🎨 **Material 3 & Dark Mode**:
  - Seamless support for Light, Dark, and System Default theme modes.
  - Customizable decimal precision (0 to 4 decimal places) stored via Jetpack DataStore.
- 🔒 **100% Offline & Private**:
  - No network permissions required.
  - Zero tracking, analytics, or external API calls — all data stays on your device.

---

## 📱 Screenshots & User Experience

| Home Screen | Portion Calculator | Results & Breakdown |
|:---:|:---:|:---:|
| Quick calculation modes & recent history | Flexible input controls, unit picker & quick presets | Visual portion cards & leftover tracking |

| History | Settings | Unit Converter |
|:---:|:---:|:---:|
| Saved meal calculations | Theme preference & precision customization | Multi-category measurement unit selector |

---

## 🛠️ Tech Stack & Architecture

PortionGuide is built following **Clean Architecture** principles and recommended Android modern app development practices:

```
app/src/main/java/com/example/portionguide/
├── data/                       # Data Layer
│   ├── database/               # Room Database (Entities, DAO, Database instance)
│   ├── preferences/            # DataStore Preferences (App configuration)
│   └── repository/             # Portion Repository implementation
├── domain/                     # Domain Layer
│   ├── engine/                 # Core math engine (PortionCalculationEngine, QuantityFormatter, UnitConverter)
│   └── model/                  # Domain Models (PortionInput, PortionResult, MeasurementUnit, etc.)
├── ui/                         # Presentation Layer (Jetpack Compose)
│   ├── components/             # Reusable UI components (AppTopBar, ResponsiveContainer, UnitSelectorDialog)
│   ├── navigation/             # Navigation Destinations & Routing
│   ├── screens/                # UI Screens (Home, Calculator, Result, History, Settings, About, Splash)
│   └── theme/                  # Material 3 Color Schemes & Typography
└── viewmodel/                  # PortionViewModel & Factory
```

### Core Technologies
- **Kotlin** (`2.2.10`)
- **Jetpack Compose** with **Material 3**
- **Navigation Compose**
- **AndroidX Lifecycle & ViewModel**
- **Room Database** (v2.7.2 with KSP annotation processing)
- **DataStore Preferences** (v1.1.2)
- **BigDecimal** high-precision calculation engine
- **Gradle Version Catalog** (`libs.versions.toml`) & AGP 9.4+

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug (or newer)
- **JDK 11** or higher
- **Android SDK** API level 37 (Minimum API level 26 / Android 8.0)

### Building & Running

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/PortionGuide.git
   cd PortionGuide
   ```

2. **Open in Android Studio**:
   Open Android Studio, select **Open**, and navigate to the `PortionGuide` directory.

3. **Build the Project**:
   Run the following command in the terminal or use Android Studio's **Build > Make Project**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 🧪 Unit Testing

The project includes unit tests for the core calculation engine (`PortionCalculationEngineTest`), covering:
- Equal portion distribution & rounding precision.
- Target size division and exact remainder/leftover calculation.
- Edge case validations (zero input, custom units, large portion counts).

---

## 📄 License

```text
MIT License

Copyright (c) 2025 Abdullah Ateeq

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<p center="align">Developed with ❤️ for food lovers and meal preppers worldwide.</p>
