package com.callsblocker

import org.junit.Test
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import java.io.File

class ManifestValidationTest {

    @Test
    fun validateManifest_serviceDeclaration() {
        // Parse AndroidManifest.xml
        val manifestFile = File("src/main/AndroidManifest.xml")
        assert(manifestFile.exists()) { "AndroidManifest.xml not found" }

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(manifestFile)

        // Find CallBlockerService
        val services = doc.getElementsByTagName("service")
        var found = false

        for (i in 0 until services.length) {
            val service = services.item(i) as Element
            val serviceName = service.getAttribute("android:name")

            if (serviceName.contains("CallBlockerService")) {
                found = true

                // Verify required attributes
                val permission = service.getAttribute("android:permission")
                assert(permission == "android.permission.BIND_SCREENING_SERVICE") {
                    "android:permission must be android.permission.BIND_SCREENING_SERVICE, got: $permission"
                }

                val exported = service.getAttribute("android:exported")
                assert(exported == "true") {
                    "android:exported must be true, got: $exported"
                }

                // Verify intent-filter
                val intentFilters = service.getElementsByTagName("intent-filter")
                assert(intentFilters.length > 0) { "No intent-filter found in service" }

                var hasCorrectAction = false
                for (j in 0 until intentFilters.length) {
                    val intentFilter = intentFilters.item(j) as Element
                    val actions = intentFilter.getElementsByTagName("action")

                    for (k in 0 until actions.length) {
                        val action = actions.item(k) as Element
                        val actionName = action.getAttribute("android:name")

                        if (actionName == "android.telecom.CallScreeningService") {
                            hasCorrectAction = true
                            break
                        }
                    }
                }

                assert(hasCorrectAction) {
                    "Intent-filter must contain action android.telecom.CallScreeningService"
                }

                break
            }
        }

        assert(found) { "CallBlockerService not found in manifest" }
    }

    @Test
    fun validateManifest_requiredPermissions() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        assert(manifestFile.exists()) { "AndroidManifest.xml not found" }

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(manifestFile)

        val requiredPermissions = listOf(
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_CALL_LOG",
            "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
        )

        val permissions = doc.getElementsByTagName("uses-permission")
        val foundPermissions = mutableSetOf<String>()

        for (i in 0 until permissions.length) {
            val permission = permissions.item(i) as Element
            val permissionName = permission.getAttribute("android:name")
            foundPermissions.add(permissionName)
        }

        requiredPermissions.forEach { required ->
            assert(foundPermissions.contains(required)) {
                "Missing required permission: $required"
            }
        }

        // BIND_SCREENING_SERVICE must NOT be in uses-permission (system-only permission,
        // declared only on the service element via android:permission)
        assert(!foundPermissions.contains("android.permission.BIND_SCREENING_SERVICE")) {
            "BIND_SCREENING_SERVICE must NOT be in uses-permission (it's a system-only permission)"
        }
    }

    @Test
    fun validateManifest_mainActivity() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        assert(manifestFile.exists()) { "AndroidManifest.xml not found" }

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(manifestFile)

        val activities = doc.getElementsByTagName("activity")
        var mainActivityFound = false

        for (i in 0 until activities.length) {
            val activity = activities.item(i) as Element
            val activityName = activity.getAttribute("android:name")

            if (activityName.contains("MainActivity")) {
                mainActivityFound = true

                val exported = activity.getAttribute("android:exported")
                assert(exported == "true") {
                    "MainActivity must be exported"
                }

                // Verify MAIN intent filter
                val intentFilters = activity.getElementsByTagName("intent-filter")
                assert(intentFilters.length > 0) { "MainActivity must have intent-filter" }

                break
            }
        }

        assert(mainActivityFound) { "MainActivity not found in manifest" }
    }
}
