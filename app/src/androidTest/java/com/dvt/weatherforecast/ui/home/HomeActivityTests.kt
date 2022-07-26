package com.dvt.weatherforecast.ui.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.ui.root.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTests {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun test_IsActivityInView() {
        onView(withId(R.id.main_root))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}