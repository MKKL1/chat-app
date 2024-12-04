plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.szampchat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.szampchat"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        pickFirst ("META-INF/INDEX.LIST")
        pickFirst ("META-INF/io.netty.versions.properties")
    }
//    configurations {
//        all {
//            exclude(group = "io.netty")
//        }
//    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-testing:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")


    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0-alpha06")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.8.4")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // https://mvnrepository.com/artifact/com.github.davidliu/audioswitch
//    implementation("com.github.davidliu:audioswitch:89582c47c9a04c62f90aa5e57251af4800a6")
//    implementation("io.livekit:livekit-android:1.1.8")
//    implementation ("io.livekit:livekit-android-compose-components:1.3.0")

    implementation ("io.rsocket:rsocket-core:1.1.4")
    implementation ("io.rsocket:rsocket-transport-netty:1.1.1")
    implementation ("io.projectreactor:reactor-core:3.5.0")

    implementation("com.auth0.android:jwtdecode:2.0.1")
    implementation("com.google.android.gms:play-services-auth:20.0.1")


}