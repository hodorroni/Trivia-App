package eu.tutorials.jettrivia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//will give Hilt the permissions to all of our project files to bind all dependencies
@HiltAndroidApp
class TriviaApplication:Application() {
}