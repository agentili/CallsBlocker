package com.callsblocker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callsblocker.util.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 1, // 1-5
    val isCompleted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsManager: PrefsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current < 5) {
            _uiState.value = _uiState.value.copy(currentStep = current + 1)
        } else if (current == 5) {
            completeOnboarding()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefsManager.setOnboardingCompleted(true)
            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }

    fun skipToStep(step: Int) {
        if (step in 1..5) {
            _uiState.value = _uiState.value.copy(currentStep = step)
        }
    }
}
