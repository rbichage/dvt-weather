package com.dvt.weatherforecast.ui.cities

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.launchFragmentInHiltContainer
import com.dvt.weatherforecast.sample.SampleResponses
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LocationsListFragmentTests {

    private lateinit var scenario: FragmentScenario<LocationsListFragment>

    val LIST_ITEM = 4
    val ITEM_IN_TEST = SampleResponses.fakeLocations[LIST_ITEM]

//    @Before
//    fun setup() {
//        scenario = launchFragmentInHiltContainer<LocationsListFragment> {
//
//
//        }
//    }

    @Test
    fun test_launchFragmentInHiltContainer() {
        launchFragmentInHiltContainer<LocationsListFragment> {

        }
    }

    @Test
    fun test_isFragmentVisible() {
        onView(withId(R.id.locations_root))
                .check(matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun test_isRecyclerVisible() {
        onView(withId(R.id.cities_recycler))
                .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_recyclerViewSelection() {
        onView(withId(R.id.cities_recycler))
                .perform(actionOnItemAtPosition<LocationsAdapter.LocationViewHolder>(LIST_ITEM, click()))

        onView(withId(R.id.tv_location_name))
                .check(matches(withText(ITEM_IN_TEST.name)))


        pressBack()

    }


}