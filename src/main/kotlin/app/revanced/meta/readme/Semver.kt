data class Semver(val major: Int, val minor: Int, val patch: Int) {
    companion object {
        fun fromString(version: String): Semver {
            var parts = version.split(".")

            if (parts.count() != 3) throw IllegalArgumentException("semver must have 3 parts")

            val intParts = parts.map { it.toInt() }
            return Semver(intParts[0], intParts[1], intParts[2])
        }
    }

    override fun toString(): String = "$major.$minor.$patch"
}

class SemverComparator {
    companion object : Comparator<Semver> {
        override fun compare(a: Semver, b: Semver): Int = when {
            a.major != b.major -> a.major - b.major
            a.minor != b.minor -> a.minor - b.minor
            else -> a.patch - b.patch
        }
    }
}
