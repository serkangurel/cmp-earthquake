package com.sgmobile.earthquake.core.navigation

interface NavigationProvider {
    operator fun invoke(): NavigationModule
}