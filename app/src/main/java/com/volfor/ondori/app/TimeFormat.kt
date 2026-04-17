package com.volfor.ondori.app

import androidx.compose.runtime.staticCompositionLocalOf

val LocalIs24HourFormat = staticCompositionLocalOf<Boolean> {
    error("LocalIs24HourFormat not provided - wrap root composable with CompositionLocalProvider")
}