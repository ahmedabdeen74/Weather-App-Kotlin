plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 35
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/INDEX.LIST"
        }
    }

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("io.coil-kt:coil-compose:2.5.0")
//Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    //Glide
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    //LiveData & Compose
    val compose_version = "1.0.0"
    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")



// Navigation
    implementation("androidx.navigation:navigation-compose:2.8.8")

// Location
    implementation("com.google.android.gms:play-services-location:21.1.0")

// Animations
    implementation("com.airbnb.android:lottie-compose:6.0.0")

// Maps
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    implementation ("androidx.compose.material:material-icons-extended:1.5.4") // Use your Compose version
    implementation ("com.google.android.material:material:1.11.0")


    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")



    // إصدارات الـ Dependencies (يتم تعريفها في build.gradle على مستوى المشروع أو هنا مباشرة)
    val junitVersion = "4.13.2"
    val hamcrestVersion = "2.2"
    val robolectricVersion = "4.13" // أحدث إصدار متوافق حتى أبريل 2025
    val androidXTestCoreVersion = "1.6.1" // أحدث إصدار لـ androidx.test:core
    val androidXTestExtKotlinRunnerVersion = "1.2.0" // أحدث إصدار لـ androidx.test.ext:junit-ktx
    val espressoVersion = "3.6.1" // أحدث إصدار لـ Espresso
    val timberVersion = "5.0.1" // إصدار Timber (ثابت ولا يحتاج تحديث عادةً)
    val archTestingVersion = "2.2.0" // أحدث إصدار لـ androidx.arch.core:core-testing
    val coroutinesVersion = "1.8.1" // أحدث إصدار لـ Kotlin Coroutines حتى أبريل 2025
    val mockkVersion = "1.13.12" // أحدث إصدار مستقر لـ MockK (تم تعديله لتجنب مشاكل التوافق)

// Dependencies for local unit tests (اختبارات JVM)
    testImplementation ("junit:junit:$junitVersion")
    testImplementation ("org.hamcrest:hamcrest:$hamcrestVersion")
    testImplementation ("org.hamcrest:hamcrest-library:$hamcrestVersion")
    testImplementation ("androidx.arch.core:core-testing:$archTestingVersion")
    testImplementation ("org.robolectric:robolectric:$robolectricVersion")

// AndroidX Test - JVM testing (اختبارات JVM باستخدام AndroidX)
    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
    testImplementation ("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")

// AndroidX Test - Instrumented testing (اختبارات على الجهاز/المحاكي)
    androidTestImplementation ("androidx.test:core:$androidXTestCoreVersion")
    androidTestImplementation ("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
    androidTestImplementation ("androidx.test.espresso:espresso-core:$espressoVersion")

// Timber (للتسجيل/Logging)
    implementation ("com.jakewharton.timber:timber:$timberVersion")

// Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

// MockK (للمحاكاة في الاختبارات)
    testImplementation ("io.mockk:mockk:$mockkVersion")
    testImplementation ("io.mockk:mockk-agent:$mockkVersion")
    androidTestImplementation ("io.mockk:mockk-android:$mockkVersion")

}

