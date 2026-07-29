dependencies {
    compileOnly(project(":extensions:shared:library"))

    implementation(libs.hiddenapibypass)
}

android {
    defaultConfig {
        minSdk = 26
    }
}
