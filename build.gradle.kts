// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra { "1.3.72" }
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0-alpha15")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        val navVersion = "2.3.0-beta01"
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://raw.githubusercontent.com/Zelgius0880/AndroidLibraries/master/releases")
    }
}

tasks.register("clean", Delete::class) {
    delete  (rootProject.buildDir)
}



val getProps by extra {
    fun(propName: String): String {
        val propsFile = rootProject.file("local.properties")
        return if (propsFile.exists()) {
            val props = java.util.Properties()
            props.load(java.io.FileInputStream(propsFile))
            props[propName] as String
        } else {
            ""
        }
    }
}
