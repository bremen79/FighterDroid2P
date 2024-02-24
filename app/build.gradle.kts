plugins {
    id("com.android.application")
}

android {
    namespace = "com.android.fighterdroid2p"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    lint {
        //checkReleaseBuilds = false
        // Or, if you prefer, you can continue to check for errors in release builds,
        // but continue the build even when errors are found:
        abortOnError = false
    }

    defaultConfig {
        applicationId = "com.android.fighterdroid2p"
        minSdk = 24
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 24
        versionCode = 8
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    flavorDimensions += listOf("keys")
    productFlavors {
        create("numeric") {
            dimension = "keys"

            // We map the joystick and buttons to numeric keypad keys.
            // The reason is that we don't want to map to any key already used by retroarch and mame.
            // See the following for the retroarch accepted keyboard keys:
            // https://gist.github.com/Monroe88/0f7aa02156af6ae2a0e728852dcbfc90

            buildConfigField("int", "UP_KEY", "152")
            buildConfigField("int", "DOWN_KEY", "146")
            buildConfigField("int", "LEFT_KEY", "148")
            buildConfigField("int", "RIGHT_KEY", "150")
            buildConfigField("int", "P1_KEY", "151")
            buildConfigField("int", "P2_KEY", "153")
            buildConfigField("int", "P3_KEY", "149")
            buildConfigField("int", "P4_KEY", "145")
            buildConfigField("int", "P5_KEY", "147")
            buildConfigField("int", "P6_KEY", "161")
            buildConfigField("int", "START_KEY", "158")
        }
        create("gamepad") {
            dimension = "keys"

            // We map the joystick and buttons to gamepad keys.

            buildConfigField("int", "UP_KEY", "19")
            buildConfigField("int", "DOWN_KEY", "20")
            buildConfigField("int", "LEFT_KEY", "21")
            buildConfigField("int", "RIGHT_KEY", "22")
            buildConfigField("int", "P1_KEY", "96")
            buildConfigField("int", "P2_KEY", "97")
            buildConfigField("int", "P3_KEY", "99")
            buildConfigField("int", "P4_KEY", "98")
            buildConfigField("int", "P5_KEY", "100")
            buildConfigField("int", "P6_KEY", "101")
            buildConfigField("int", "START_KEY", "108")
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}