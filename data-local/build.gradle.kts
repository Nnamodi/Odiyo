plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.ksp)
}

android {
	namespace = "com.roland.android.data_local"
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

	// other modules
	implementation(project(path = ":domain"))
	implementation(project(path = ":data-repository"))

	// persistence
	implementation(libs.datastore)
	implementation(libs.room.ktx)
	implementation(libs.room.runtime)
	ksp(libs.room.compiler)

	// test
	testImplementation(libs.coroutines)
	testImplementation(libs.junit)
	testImplementation(libs.mockito)

}