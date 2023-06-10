package com.lacolinares.jetpackqrbarcodescanner.model

sealed class BarcodeState<out T>{
    object Idle: BarcodeState<Nothing>()
    data class Success<out T>(val data: Map<Int, T>): BarcodeState<T>()
    object Cancelled: BarcodeState<Nothing>()
    object Error: BarcodeState<Nothing>()
}