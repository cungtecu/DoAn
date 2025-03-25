plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.doan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.doan"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
dependencies {
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Hoặc phiên bản mới nhất
}
dependencies {
    implementation("com.google.android.material:material:1.12.0") // Thay <version> bằng phiên bản mới nhất, ví dụ: 1.6.0, 1.7.0, 1.8.0, 1.9.0
}
dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0")
}