package com.example.adminvirtudecor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.adminvirtudecor.screen.AddFurnitureScreen
import com.example.adminvirtudecor.screen.CompletedOrderScreen
import com.example.adminvirtudecor.screen.LoginScreen

import com.example.adminvirtudecor.screen.ViewFurnitureScreen
import com.example.adminvirtudecor.screens.PendingOrderScreen


@Composable
fun FurnitureAdminApp() {
    val navController = rememberNavController()
    NavHost (navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController) }

        composable("add_furniture") { AddFurnitureScreen(navController) }

        composable("view_furniture") { ViewFurnitureScreen(navController) }
        composable("pending_orders") {
            PendingOrderScreen(navController)
        }

        composable("completedOrder") {
            CompletedOrderScreen(navController)
        }
    }



    }





