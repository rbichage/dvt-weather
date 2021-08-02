package com.dvt.weatherforecast.ui.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.dvt.weatherforecast.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class HomeActivityTests {

    @get: Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)


    @Test
    fun test_IsActivityInView() {
        onView(withId(R.id.home_root))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_selectLocation() {
        Thread.sleep(2000)

        onView(withId(R.id.tv_location_name))
                .perform(click())

    }

    @Test
    fun test_navigateToLocations() {
        onView(withId(R.id.fab_cities))
                .perform(click())
        Thread.sleep(1000)
        pressBack()
    }
}