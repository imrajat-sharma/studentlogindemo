package com.example

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun verifyAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("StudentLoginDemo", appName)
    }

    @Test
    fun verifyCourseOptionsExist() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val courses = context.resources.getStringArray(R.array.course_options)
        assertTrue(courses.size >= 3)
        assertTrue(courses.any { it.contains("MCA") })
        assertTrue(courses.any { it.contains("MBA") })
        assertTrue(courses.any { it.contains("BCA") })
    }

    @Test
    fun verifyMainActivityLaunchAndLifecycle() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        assertNotNull(scenario)
        scenario.close()
    }

    @Test
    fun verifyDisplayDetailsActivityReceivesIntentData() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, DisplayDetailsActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_REG_ID, "STU-2026-TEST")
            putExtra(MainActivity.EXTRA_NAME, "Test Student")
            putExtra(MainActivity.EXTRA_EMAIL, "test@example.com")
            putExtra(MainActivity.EXTRA_GENDER, "Male")
            putExtra(MainActivity.EXTRA_DOB, "15/08/2001")
            putExtra(MainActivity.EXTRA_COURSE, "MCA")
            putExtra(MainActivity.EXTRA_TERMS_ACCEPTED, "Agreed (Confirmed)")
            putExtra(MainActivity.EXTRA_TIMESTAMP, "2026-09-29 08:30:00")
        }
        val scenario = ActivityScenario.launch<DisplayDetailsActivity>(intent)
        assertNotNull(scenario)
        scenario.close()
    }
}
