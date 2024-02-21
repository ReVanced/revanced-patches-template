rootProject.name = "revanced-patches-template"

buildCache {
    local {
        isEnabled = "CI" !in System.getenv()
    }
}
