package app.revanced.patches.youtube.layout.theme

enum class Themes(val applier: (String) -> String?) {
    Amoled({ attr ->
        when (attr) {
            "yt_black1", "yt_black1_opacity95", "yt_black2", "yt_black3", "yt_black4", "yt_status_bar_background_dark" -> "@android:color/black"
            "yt_selected_nav_label_dark" -> "#ffdf0000"
            else -> null
        }
    });

    companion object {
        val names = values().map { it.name }
    }
}