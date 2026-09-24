# Android Interview Challenge

[![CI](https://github.com/tiagodiasduarte/android-interview-challenge/actions/workflows/ci.yml/badge.svg?branch=develop)](https://github.com/tiagodiasduarte/android-interview-challenge/actions/workflows/ci.yml) ![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin&logoColor=white) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white) ![Min SDK](https://img.shields.io/badge/minSdk-26-3DDC84?logo=android&logoColor=white) ![Target SDK](https://img.shields.io/badge/targetSdk-37-3DDC84?logo=android&logoColor=white)

## 📄 Description

A native Android project built with Kotlin and Jetpack Compose, following Clean Architecture and MVVM. It uses Coroutines and Flow for asynchronous operations, Hilt for dependency injection, and unit and Compose UI tests for the main layers and user interactions.

The project builds two independent apps from a single codebase:

- **Listing** (`pt.tiagoduarte.challenge.listing`): Downloads a product catalog once, stores it locally, and provides a paginated, searchable list with a product detail screen.
- **Form** (`pt.tiagoduarte.challenge.form`): Provides a validated order form.

Both apps can be installed side by side on the same device.

## 🧰 Requirements

- JDK **17** or newer
- Minimum SDK  **26**

## 🚀 Build, install and run

The project has two apps, most tasks need the app (flavor) in their name. Run every command from the project root with the Gradle wrapper. In Android Studio, choose the app in **View → Tool Windows → Build Variants** (`listingDebug` or `formDebug`) and press **Run**.

```bash
# Build the debug APKs
./gradlew assembleListingDebug
./gradlew assembleFormDebug

# Build the release APKs
./gradlew assembleListingRelease
./gradlew assembleFormRelease
```

The APKs are written to `app/build/outputs/apk/<app>/debug/`.


## 🧪 Tests and checks

```bash
# Unit tests (including the Compose UI tests, which run on the JVM with Robolectric)
./gradlew testListingDebugUnitTest
./gradlew testFormDebugUnitTest

# Coverage report and the 90% line/branch minimum
./gradlew koverHtmlReport koverVerify

# Static analysis
./gradlew lintListingDebug lintFormDebug
./gradlew detekt

# Everything above, for both apps
./gradlew check
```

CI (`.github/workflows/ci.yml`) runs the build, the unit tests with the coverage check, lint and detekt on every pull request.

## ✨ Features

All four features from the requirements are implemented.
- ✅ **Feature 1: Paginated List:** Product listing with local storage and first-launch download.
- ✅ **Feature 2: Advanced Search:** Real-time search by name and description, with case and accent-insensitive matching.
- ✅ **Feature 3: Product Detail:** Product details with title, price, discount, stock, rating, and image.
- ✅ **Feature 4: Form Validation:** Form with validation for user data, email, phone, promo code, date, and rating.


## 🧠 Technical decisions

### Clean Architecture

Clean Architecture was chosen to keep the app modular, maintainable, and easy to test.

* **Data Layer:** Handles API communication and local storage with Room.
* **Domain Layer:** Contains the Product model and ProductRepository interface, independent of Android and the data layer. No use cases are needed yet, as they would only forward repository calls.
* **Presentation Layer:** Uses MVVM, with ViewModels depending on the domain repository interface.

The code follows a layered structure: **data → domain ← presentation**, with dependency injection through Hilt.

```
app/src/
├── main/            shared by both apps
│   ├── data/        Room (entity, DAO, database), Retrofit (API, DTOs, RetrofitClient), repository implementation
│   ├── domain/      Product model and ProductRepository interface
│   ├── mapper/      conversions between DTOs, entities and domain models
│   └── ui/theme/    Compose theme, colors, typography, spacing
├── listing/         listing app: ListingApp, ListingActivity, navigation, Hilt modules, screens and ViewModels
├── form/            form app: FormApp, FormActivity, validator, screen and ViewModel
├── test/            unit tests shared by both apps, fakes and test rules
├── testListing/     listing-only tests
├── testForm/        form-only tests
└── testFixtures/    Random test data (random products, entities and responses)
```

**Note:** The `data/`, `domain/` and `mapper/` could be moved to the listing flavour because was not used in listing but was kep here because is the core of the app.

### Dependency Injection (Hilt)

Hilt is used for dependency injection, providing dependencies at runtime and validating the dependency graph at compile time.

### MVVM

MVVM separates UI logic from business logic and works well with Jetpack Compose, making UI state and user interactions easier to manage and test.

### Product Flavors

The two required apps are built from a single module using product flavors:

* **listing:** Product catalog, search, and detail (src/listing).
* **form:** Validated order form (src/form).

Each flavor has its own launcher activity, application ID, name, and test source set, allowing both apps to be installed side by side. Shared code is kept in src/main. 

### Room Database

Room stores the product catalog locally, allowing the listing app to work offline after the initial download. The catalog is replaced in a single transaction, so an interrupted download leaves the previous state unchanged and retries on the next launch.

### Paging Library

Paging 3 handles the product list efficiently, loading data from Room while limiting memory usage.

### UI and Theme
The Screens and UI elements are built entirely using Jetpack Compose.

- **Light** — the default light color scheme
- **Dark** — the default dark color scheme
- **System** — follows the device's system-wide light/dark setting(on Android 12 and newer)

The screens also adapt to tablets, foldables and landscape:
- **Product list:** an adaptive grid, so wider screens show more columns.
- **Product detail:** the image and the details are shown side by side on wide windows.
- **Form:** kept at a comfortable width and centered.

On tablets, the list and the detail are still separate screens; showing them side by side is listed in [Future improvements](#-future-improvements).

### Testing

* **Unit Tests:** Cover ViewModels, repository, DAO, API, mappers, and form validation.
* **UI Tests:** Use Jetpack Compose testing APIs with Robolectric to test the list, detail screen, and form without an emulator.
* **Fakes:** Hand-written fakes and a local mock server with JSON responses are used instead of mocks to test success and error scenarios.

### Coverage

Kover requires 90% line and branch coverage, excluding Composables, previews, Activities, Hilt modules, and generated code.


## 📚 Libraries

### 📱 App

| Library                                    | Purpose                                                                     |
|--------------------------------------------|-----------------------------------------------------------------------------|
| Jetpack Compose (BOM), Material 3          | UI toolkit and Material components                                          |
| Material icons extended                    | Rating, search, calendar and navigation icons                               |
| Material 3 adaptive                        | Window size classes, for the detail screen's wide layout                    |
| Navigation Compose                         | Navigation between the list and the detail screen                           |
| Lifecycle (runtime, Compose)               | Lifecycle-aware state collection in Compose (`collectAsStateWithLifecycle`) |
| Hilt (+ Hilt Navigation Compose)           | Dependency injection, `hiltViewModel()`                                     |
| Retrofit + kotlinx.serialization converter | HTTP API client and JSON parsing                                            |
| OkHttp + logging interceptor               | HTTP client and request logging in debug builds                             |
| kotlinx.serialization                      | JSON models for the API                                                     |
| Room (runtime, KTX, paging)                | Local database for the catalog, with Paging support                         |
| Paging 3 (runtime, Compose)                | Loading the product list page by page                                       |
| Coil 3 (Compose, OkHttp)                   | Loading the product image                                                   |

### ✅ Tests

| Library                                   | Purpose                                                    |
|-------------------------------------------|------------------------------------------------------------|
| JUnit 4                                   | Test framework                                             |
| kotlinx-coroutines-test                   | Testing coroutines and flows (`runTest`, test dispatchers) |
| Turbine                                   | Asserting `Flow` emissions in order                        |
| MockWebServer (OkHttp)                    | Serving fake API responses to test the Retrofit API        |
| Robolectric                               | Running Android code (Room, Compose UI) on the JVM         |
| AndroidX Test (JUnit ext, core), Espresso | Android test runner and utilities used by Compose UI tests |
| Compose UI test                           | Finding and interacting with composables in tests          |
| Paging testing                            | Snapshots of paged data and in-memory paging sources       |

### 🔧 Build and tooling

| Tool                                                 | Purpose                                                  |
|------------------------------------------------------|----------------------------------------------------------|
| KSP                                                  | Code generation for Room and Hilt                        |
| Kover                                                | Code coverage reports and the coverage minimum           |
| Detekt + Compose rules                               | Kotlin static analysis, including Compose-specific rules |
| Android Lint                                         | Android static analysis                                  |
| Gradle version catalog (`gradle/libs.versions.toml`) | All dependency versions in one place                     |

## ⚠️ Known issues

- **Fixed page size:** the list always loads pages of 20 products, whatever the screen size. On a large tablet, a page can be smaller than what fits on one screen, so several pages load right away to fill it.

## 🔮 Future improvements

The project covers all the requirements, but there is still room to improve it. These are the main improvements I have identified:

1. **Modularization:** split the project into Gradle modules (for example `:core:data`, `:core:domain`, `:core:designsystem`, `:feature:products`, `:feature:form`) with convention plugins, so layer boundaries are checked by the compiler and builds can run in parallel.

2. **Accessibility:** let the delivery date picker open with TalkBack and a hardware keyboard, not only by touch.

3. **Adaptive layout:** on tablets and foldables, show the product list and the selected product side by side with `ListDetailPaneScaffold`, instead of navigating to a full-screen detail.
