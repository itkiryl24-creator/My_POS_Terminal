package com.example.myposterminal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myposterminal.domain.model.PosResult
import com.example.myposterminal.domain.useCase.TransferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class POSViewModel @Inject constructor(
    private val transferUseCase: TransferUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PosState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PosEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun handleEvent(onEvent: PosEvent) {
        when (onEvent) {
            is PosEvent.OnButtonClicked -> startTransfer()
            is PosEvent.OnCoinsChanged -> _uiState.update { it.copy(coins = onEvent.value) }
        }
    }

    private fun startTransfer() {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.value.coins.isNotEmpty()) {

                val result = transferUseCase(uiState.value.coins.toInt())
                withContext(Dispatchers.Main) {
                    when (result) {
                        is PosResult.Error -> _uiEffect.emit(PosEffect.ShowToast(result.error))
                        is PosResult.Success -> _uiEffect.emit(PosEffect.ShowToast(result.message))
                    }
                }
            } else {
                _uiEffect.emit(PosEffect.ShowToast("пустое поле"))
            }
        }
    }
}