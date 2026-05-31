package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.WaterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Water Logger", appName)
  }

  @Test
  fun `viewmodel and database initialization works`() = runTest {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = WaterViewModel(app)
    assertNotNull(viewModel)
    
    // Test goal change
    viewModel.changeGoal(3000)
    assertEquals(3000, viewModel.hydrationGoal.value)

    // Test adding water logs
    viewModel.addWaterLog(500)
    viewModel.addWaterLog(250)

    // Verify logs map is correctly loaded
    val logs = viewModel.logsForSelectedDay.first()
    assertNotNull(logs)

    // Verify drawable calm can be decoded
    val bitmap = android.graphics.BitmapFactory.decodeResource(app.resources, R.drawable.calm)
    assertNotNull("calm bitmap should not be null", bitmap)
    println("calm bitmap info: width=${bitmap?.width}, height=${bitmap?.height}")
  }
}
