package com.example.myposterminal.presentation

data class PosState(
    val coins: String = ""
)

sealed interface PosEvent{
    data object OnButtonClicked: PosEvent
    data class OnCoinsChanged(val value: String): PosEvent
}

sealed interface PosEffect{
    data class ShowToast(val message: String) : PosEffect
}
