package com.example.pharmarestock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmarestock.database.AppDatabase
import com.example.pharmarestock.ui.MainScreen
import com.example.pharmarestock.ui.PharmacyViewModel
import com.example.pharmarestock.ui.PharmacyViewModelFactory
import com.example.pharmarestock.ui.theme.PharmaRestockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // This makes the app look modern (draws behind the status bar)
        enableEdgeToEdge()

        // 1. Get the Database and the DAO (Data Access Object)
        val database = AppDatabase.getDatabase(this)
        val dao = database.pharmacyDao()

        setContent {
            PharmaRestockTheme {
                // 2. Create the ViewModel and pass the database DAO to it
                val viewModel: PharmacyViewModel = viewModel(
                    factory = PharmacyViewModelFactory(dao)
                )

                // 3. Show the screen we built in the UI folder!
                MainScreen(viewModel = viewModel)
            }
        }
    }
}