package app.revanced.patches.shared.settings.preference

import app.revanced.patcher.arsc.Resource

/**
 * Resource
 */
internal interface IResource {
    /**
     * Name of the resource.
     */
    val name: String


    /**
     * Type of the resource
     */
    val type: String

    /**
     * Value of the resource
     */
    val patcherValue: Resource
}