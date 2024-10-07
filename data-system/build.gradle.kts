plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
}

android {
	namespace = "com.roland.android.data_system"
	compileSdk = 34

	defaultConfig {
		minSdk = 24

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
}

dependencies {

	// android
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)

	// di
	implementation(platform(libs.koin.bom))
	implementation(libs.koin.core)

	// media3
	implementation(libs.media3.player)
	implementation(libs.media3.session)
	implementation(libs.media3.ui)

	// other modules
	implementation(project(path = ":domain"))
	implementation(project(path = ":data-repository"))

	// test
	testImplementation(libs.coroutines)
	testImplementation(libs.junit)
	testImplementation(libs.mockito)

}