package app.revanced.patches.remini.misc.premium.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
	Package(
		"com.bigwinepot.nwdn.international", arrayOf("3.7.46.202160002")
		)
	])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class TimelineAdsCompatibility
