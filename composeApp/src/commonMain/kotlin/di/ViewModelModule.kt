package di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import ui.screens.detail.DetailViewModel
import ui.screens.home.HomeViewModel

fun viewModelModule(): Module = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
}