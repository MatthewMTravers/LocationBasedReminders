package com.example.locationbasedreminders
import androidx.test.core.app.ActivityScenario
import org.junit.Test

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.example.locationbasedreminders.activity.LoginActivity

class LoginTest {
    // Instrumented UI tests for login activity and checking that a few text fields and buttons are present.
    @Test
    fun testLoginButtonDisplayed() {
        ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.login_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testUsernameFieldDisplayed() {
        ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.username_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPasswordFieldDisplayed() {
        ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.password_text))
            .check(matches(isDisplayed()))
    }
}