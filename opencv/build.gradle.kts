// This file is part of OpenCV project.
// It is subject to the license terms in the LICENSE file found in the top-level directory
// of this distribution and at http://opencv.org/license.html.

//
// Notes about integration OpenCV into existed Android Studio application project are below (application 'app' module should exist).
//
// This file is located in <OpenCV-android-sdk>/sdk directory (near 'etc', 'java', 'native' subdirectories)
//
// Add module into Android Studio application project:
//
// - Android Studio way:
//   (will copy almost all OpenCV Android SDK into your project, ~200Mb)
//
//   Import module: Menu -> "File" -> "New" -> "Module" -> "Import Gradle project":
//   Source directory: select this "sdk" directory
//   Module name: ":opencv"
//
// - or attach library module from OpenCV Android SDK
//   (without copying into application project directory, allow to share the same module between projects)
//
//   Edit "settings.gradle" and add these lines:
//
//   def opencvsdk='<path_to_opencv_android_sdk_rootdir>'
//   // You can put declaration above into gradle.properties file instead (including file in HOME directory),
//   // but without 'def' and apostrophe symbols ('): opencvsdk=<path_to_opencv_android_sdk_rootdir>
//   include ':opencv'
//   project(':opencv').projectDir = new File(opencvsdk + '/sdk')
//
//
//
// Add dependency into application module:
//
// - Android Studio way:
//   "Open Module Settings" (F4) -> "Dependencies" tab
//
// - or add "project(':opencv')" dependency into app/build.gradle:
//
//   dependencies {
//       implementation fileTree(dir: 'libs', include: ['*.jar'])
//       ...
//       implementation project(':opencv')
//   }
//
//
//
// Load OpenCV native library before using:
//
// - avoid using of "OpenCVLoader.initAsync()" approach - it is deprecated
//   It may load library with different version (from OpenCV Android Manager, which is installed separatelly on device)
//
// - use "System.loadLibrary("opencv_java4")" or "OpenCVLoader.initDebug()"
//   TODO: Add accurate API to load OpenCV native library
//
//
//
// Native C++ support (necessary to use OpenCV in native code of application only):
//
// - Use find_package() in app/CMakeLists.txt:
//
//   find_package(OpenCV 4.8 REQUIRED java)
//   ...
//   target_link_libraries(native-lib ${OpenCV_LIBRARIES})
//
// - Add "OpenCV_DIR" and enable C++ exceptions/RTTI support via app/build.gradle
//   Documentation about CMake options: https://developer.android.com/ndk/guides/cmake.html
//
//   defaultConfig {
//       ...
//       externalNativeBuild {
//           cmake {
//               cppFlags "-std=c++11 -frtti -fexceptions"
//               arguments "-DOpenCV_DIR=" + opencvsdk + "/sdk/native/jni" // , "-DANDROID_ARM_NEON=TRUE"
//           }
//       }
//   }
//
// - (optional) Limit/filter ABIs to build ('android' scope of 'app/build.gradle'):
//   Useful information: https://developer.android.com/studio/build/gradle-tips.html (Configure separate APKs per ABI)
//
//   splits {
//       abi {
//           enable true
//           universalApk false
//           reset()
//           include 'armeabi-v7a' // , 'x86', 'x86_64', 'arm64-v8a'
//       }
//   }
//
plugins {
    id("com.android.library")
    id("kotlin-android")
}



android {
    compileSdk = 33
    namespace = "org.opencv"

    defaultConfig {


        minSdk = 24
        targetSdk = 33
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
                targets("opencv_jni_shared")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildTypes {
        getByName("debug") {
            packagingOptions {
                doNotStrip("**/*.so")  // controlled by OpenCV CMake scripts
            }
        }
        getByName("release") {
            packagingOptions {
                doNotStrip("**/*.so")  // controlled by OpenCV CMake scripts
            }
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("native/libs")
            java.srcDirs("java/src")
            aidl.srcDirs("java/src")
            res.srcDirs("java/res")
            manifest.srcFile("java/AndroidManifest.xml")
        }
    }

    externalNativeBuild {
        cmake {
            path("$projectDir/libcxx_helper/CMakeLists.txt")
        }
    }

}

dependencies {
    // Add your dependencies here
}
