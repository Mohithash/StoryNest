@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.storynest.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohithash.storynest.ui.screens.LibraryScreen
import com.mohithash.storynest.ui.screens.NewStoryScreen
import com.mohithash.storynest.ui.screens.OnboardingScreen
import com.mohithash.storynest.ui.screens.ReadScreen
import com.mohithash.storynest.ui.screens.SettingsScreen

@Composable
fun Nav(vm: AppViewModel) {
    val s by vm.settings.collectAsState()
    if (!s.onboarded) { OnboardingScreen(vm); return }
    val nav = rememberNavController()
    NavHost(nav, "library") {
        composable("library") { LibraryScreen(vm, onNew = { nav.navigate("new") }, onRead = { nav.navigate("read") }, onSettings = { nav.navigate("settings") }) }
        composable("new") { NewStoryScreen(vm, onBack = { nav.popBackStack() }, onDone = { nav.popBackStack(); nav.navigate("read") }) }
        composable("read") { ReadScreen(vm, onBack = { nav.popBackStack() }, onSequel = { nav.navigate("new") }) }
        composable("settings") { SettingsScreen(vm, onBack = { nav.popBackStack() }) }
    }
}
