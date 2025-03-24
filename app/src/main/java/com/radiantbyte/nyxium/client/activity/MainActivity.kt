package com.radiantbyte.nyxium.client.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import com.radiantbyte.nyxium.client.navigation.Navigation
import com.radiantbyte.nyxium.client.ui.theme.MuCuteClientTheme


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuCuteClientTheme {
                Navigation()
            }
        }
    }

}