package app.revanced.patches.grindr.microg.patch.bytecode

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch

import app.revanced.patches.shared.fingerprints.WatchWhileActivityFingerprint

import app.revanced.patches.grindr.microg.annotations.MicroGPatchCompatibility
import app.revanced.patches.grindr.microg.fingerprints.*

import app.revanced.patches.grindr.microg.patch.resource.MicroGResourcePatch
import app.revanced.patches.grindr.microg.patch.resource.GooglePlayServicesManifestResourcePatch
import app.revanced.patches.grindr.microg.patch.bytecode.lyImgPatch
import app.revanced.patches.grindr.microg.patch.bytecode.FirebaseGetCertPatch
import app.revanced.patches.grindr.microg.patch.bytecode.GooglePlayServicesManifestValueExceptionPatch

import app.revanced.patches.grindr.microg.Constants.REVANCED_APP_NAME
import app.revanced.patches.grindr.microg.Constants.PACKAGE_NAME
import app.revanced.patches.grindr.microg.Constants.REVANCED_PACKAGE_NAME
import app.revanced.patches.grindr.ConfigFingerprint
import app.revanced.util.microg.MicroGBytecodeHelper

@Patch
//@DependsOn([MicroGResourcePatch::class, lyImgPatch::class, GooglePlayServicesManifestResourcePatch::class, GooglePlayServicesManifestValueExceptionPatch::class, FirebaseGetCertPatch::class, GetPackageNamePatch::class, OpenHttpURLConnectionPatch::class])
//@DependsOn([FirebaseGetCertPatch::class, GetPackageNamePatch::class, OpenHttpURLConnectionPatch::class])
@DependsOn([FirebaseGetCertPatch::class])
@Name("Firebase patch")
@Description("Allows Grindr ReVanced to run without root and under a different package name with Vanced MicroG.")
@MicroGPatchCompatibility