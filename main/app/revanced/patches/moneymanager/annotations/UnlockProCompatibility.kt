package app.revanced.patches.moneymanager.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.ithebk.expensemanager")])
@Target(AnnotationTarget.CLASS)
internal annotation class UnlockProCompatibility