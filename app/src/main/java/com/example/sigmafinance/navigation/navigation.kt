package com.example.sigmafinance.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.example.sigmafinance.main.MainScreen
import com.example.sigmafinance.viewmodel.ViewModel

@Composable
fun NavigationComponent(viewModel: ViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main"){
        composable("main"){ MainScreen(navController, viewModel) }
    }
}