import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "pt.tiagoduarte.challenge"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "pt.tiagoduarte.challenge"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true

        unitTests.all { it.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED") }
    }

    @Suppress("UnstableApiUsage")
    testFixtures {
        enable = true
    }

    flavorDimensions += "appMode"
    productFlavors {
        create("listing") {
            dimension = "appMode"
            applicationIdSuffix = ".listing"
            versionNameSuffix = "-listing"
            buildConfigField("String", "BASE_URL", "\"https://dummyjson.com/\"")
        }
        create("form") {
            dimension = "appMode"
            applicationIdSuffix = ".form"
            versionNameSuffix = "-form"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.hilt.android)

    ksp(libs.hilt.android.compiler)

    // Listing app only: networking, database, paging, images and navigation
    "listingImplementation"(libs.androidx.compose.adaptive)
    "listingImplementation"(libs.androidx.navigation.compose)
    "listingImplementation"(libs.androidx.paging.compose)
    "listingImplementation"(libs.androidx.paging.runtime)
    "listingImplementation"(libs.androidx.room.runtime)
    "listingImplementation"(libs.androidx.room.ktx)
    "listingImplementation"(libs.androidx.room.paging)
    "listingImplementation"(libs.coil.compose)
    "listingImplementation"(libs.coil.network.okhttp)
    "listingImplementation"(libs.kotlinx.serialization.json)
    "listingImplementation"(libs.okhttp)
    "listingImplementation"(libs.okhttp.logging.interceptor)
    "listingImplementation"(libs.retrofit.core)
    "listingImplementation"(libs.retrofit.converter.kotlinx.serialization)
    "kspListing"(libs.androidx.room.compiler)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)

    "testListingImplementation"(libs.androidx.navigation.testing)
    "testListingImplementation"(libs.androidx.paging.testing)
    "testListingImplementation"(libs.okhttp.mockwebserver)

    testFixturesImplementation(platform(libs.androidx.compose.bom))
    testFixturesImplementation(libs.androidx.compose.runtime)

    detektPlugins(libs.compose.rules.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

room {
    schemaDirectory("$projectDir/schemas")
}

kover {
    reports {
        filters {
            excludes {
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
                annotatedBy("pt.tiagoduarte.challenge.ui.theme.PreviewDevices")
                annotatedBy("androidx.compose.runtime.Composable")

                classes(
                    // Generated code
                    "*_Factory",
                    "*_Factory\$*",
                    "*_MembersInjector",
                    "*_Impl",
                    "*_Impl\$*",
                    "*Hilt_*",
                    "*_HiltComponents*",
                    "*_HiltModules*",
                    "*Dagger*",
                    "*ComposableSingletons*",
                    "dagger.hilt.internal.aggregatedroot.codegen.*",
                    "hilt_aggregated_deps.*",
                    "pt.tiagoduarte.challenge.BuildConfig",

                    // Application classes
                    "pt.tiagoduarte.challenge.listing.ListingApp",
                    "pt.tiagoduarte.challenge.form.FormApp",

                    // Activities and DI
                    "pt.tiagoduarte.challenge.listing.presentation.ListingActivity",
                    "pt.tiagoduarte.challenge.form.FormActivity",
                    "pt.tiagoduarte.challenge.form.di.*",
                    "pt.tiagoduarte.challenge.listing.di.*",
                    "pt.tiagoduarte.challenge.data.local.db.AppDatabase",

                    // Theme
                    "pt.tiagoduarte.challenge.ui.theme.*",

                    // Retrofit API and DTOs
                    "pt.tiagoduarte.challenge.data.remote.RetrofitClient",
                    "pt.tiagoduarte.challenge.data.remote.model.*",
                )
            }
        }

        verify {
            rule("line-coverage") {
                minBound(
                    minValue = 80,
                    coverageUnits = CoverageUnit.LINE,
                    aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                )
            }
            rule("branch-coverage") {
                minBound(
                    minValue = 80,
                    coverageUnits = CoverageUnit.BRANCH,
                    aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                )
            }
        }
    }
}
