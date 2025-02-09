package com.example.sigmafinance.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sigmafinance.main.MainScreen
import com.example.sigmafinance.main.ProjectionScreen
import com.example.sigmafinance.ui.theme.richBlack
import com.example.sigmafinance.viewmodel.ViewModel

@Composable
fun NavigationComponent(viewModel: ViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main", modifier = Modifier.background(
        richBlack)){
        composable("main", enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) }){ MainScreen(navController, viewModel) }
        composable(
            route = "MoneyProjection/{baseAmount}",
            arguments = listOf(
                navArgument("baseAmount") { type = NavType.FloatType },

            ),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) }
        ) { backStackEntry ->
            val baseAmount = backStackEntry.arguments?.getFloat("baseAmount")
            ProjectionScreen(viewModel = viewModel, navController = navController, baseAmount = baseAmount?: 0f)
        }
    }
}