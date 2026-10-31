package com.example.portionguide.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.portionguide.data.preferences.AppPreferences
import com.example.portionguide.data.preferences.AppPreferencesRepository
import com.example.portionguide.data.preferences.ThemeMode
import com.example.portionguide.data.repository.PortionRepository
import com.example.portionguide.domain.engine.PortionCalculationEngine
import com.example.portionguide.domain.engine.QuantityFormatter
import com.example.portionguide.domain.model.CalculationMode
import com.example.portionguide.domain.model.MeasurementUnit
import com.example.portionguide.domain.model.PortionInput
import com.example.portionguide.domain.model.PortionResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PortionViewModel(
    private val repository: PortionRepository,
    private val preferencesRepository: AppPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<AppPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppPreferences()
        )

    private val _inputState = MutableStateFlow(PortionInput())
    val inputState: StateFlow<PortionInput> = _inputState.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val _currentResult = MutableStateFlow<PortionResult?>(null)
    val currentResult: StateFlow<PortionResult?> = _currentResult.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    // History flows dynamically re-evaluate formatting when precision preference changes
    val historyList: StateFlow<List<PortionResult>> = preferences.flatMapLatest { prefs ->
        repository.getAllCalculations(prefs.decimalPrecision)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentCalculations: StateFlow<List<PortionResult>> = preferences.flatMapLatest { prefs ->
        repository.getRecentCalculations(5, prefs.decimalPrecision)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Apply default unit from preferences when preferences load first time
        viewModelScope.launch {
            preferences.collect { prefs ->
                if (_inputState.value.totalQuantityText.isBlank()) {
                    _inputState.value = _inputState.value.copy(unit = prefs.defaultUnit)
                }
            }
        }
    }

    fun updateFoodName(name: String) {
        _inputState.value = _inputState.value.copy(foodName = name)
    }

    fun updateTotalQuantity(quantity: String) {
        _validationError.value = null
        _inputState.value = _inputState.value.copy(totalQuantityText = quantity)
    }

    fun updateUnit(unit: MeasurementUnit) {
        _inputState.value = _inputState.value.copy(unit = unit)
    }

    fun updateCustomUnitName(name: String) {
        _inputState.value = _inputState.value.copy(customUnitName = name)
    }

    fun updateNumberOfPortions(portions: String) {
        _validationError.value = null
        _inputState.value = _inputState.value.copy(numberOfPortionsText = portions)
    }

    fun updatePortionSize(size: String) {
        _validationError.value = null
        _inputState.value = _inputState.value.copy(portionSizeText = size)
    }

    fun updateNumberOfPeople(people: Int) {
        _inputState.value = _inputState.value.copy(numberOfPeople = people.coerceAtLeast(1))
    }

    fun setMode(mode: CalculationMode) {
        _validationError.value = null
        _inputState.value = _inputState.value.copy(mode = mode)
    }

    fun swapMode() {
        val current = _inputState.value
        val newMode = if (current.mode == CalculationMode.BY_PORTIONS) {
            CalculationMode.BY_SIZE
        } else {
            CalculationMode.BY_PORTIONS
        }
        setMode(newMode)
    }

    fun resetInput() {
        _validationError.value = null
        _inputState.value = PortionInput(
            unit = preferences.value.defaultUnit
        )
    }

    fun calculate(): Boolean {
        val currentInput = _inputState.value
        when (val validation = PortionCalculationEngine.validateInput(currentInput)) {
            is PortionCalculationEngine.ValidationResult.Error -> {
                _validationError.value = validation.message
                return false
            }
            is PortionCalculationEngine.ValidationResult.Valid -> {
                _validationError.value = null
                val precision = preferences.value.decimalPrecision
                val result = PortionCalculationEngine.calculate(currentInput, precision)
                _currentResult.value = result
                _isSaved.value = false
                return true
            }
        }
    }

    fun adjustPortions(delta: Int) {
        val current = _currentResult.value ?: return
        val newCount = (current.portionCount + delta).coerceAtLeast(1)
        if (newCount == current.portionCount) return

        val precision = preferences.value.decimalPrecision
        val updated = PortionCalculationEngine.recalculateWithPortionCount(current, newCount, precision)
        _currentResult.value = updated
        _isSaved.value = false
    }

    fun saveCurrentResult(onSaved: () -> Unit = {}) {
        val result = _currentResult.value ?: return
        viewModelScope.launch {
            val insertedId = repository.saveCalculation(result)
            _currentResult.value = result.copy(id = insertedId)
            _isSaved.value = true
            onSaved()
        }
    }

    fun selectResult(result: PortionResult) {
        _currentResult.value = result
        _isSaved.value = result.id > 0L
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
            if (_currentResult.value?.id == id) {
                _isSaved.value = false
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllCalculations()
            _isSaved.value = false
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(mode)
        }
    }

    fun setDefaultUnit(unit: MeasurementUnit) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultUnit(unit)
        }
    }

    fun setDecimalPrecision(precision: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDecimalPrecision(precision)
            // Recompute current result if visible
            _currentResult.value?.let { curr ->
                _currentResult.value = PortionCalculationEngine.recalculateWithPortionCount(
                    curr,
                    curr.portionCount,
                    precision
                )
            }
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHapticFeedback(enabled)
        }
    }

    fun generateShareText(result: PortionResult): String {
        return buildString {
            append("🍽️ PortionGuide Calculation\n")
            if (result.foodName.isNotBlank()) {
                append("Food: ${result.foodName}\n")
            }
            append("Total Quantity: ${result.formattedTotal} ${result.displayUnit}\n")
            append("Mode: ${result.mode.title}\n")
            append("Portions: ${result.portionCount}\n")
            append("Per Portion: ${result.formattedPerPortion} ${result.displayUnit}\n")
            if (result.hasLeftover) {
                append("Leftovers: ${result.formattedRemaining} ${result.displayUnit}\n")
            } else {
                append("Leftovers: 0 (Exact division)\n")
            }
            append("Total Distributed: ${result.formattedDistributed} ${result.displayUnit}\n")
            if (result.numberOfPeople > 1) {
                append("Servings for ${result.numberOfPeople} people\n")
            }
            append("\nCalculated accurately with PortionGuide App")
        }
    }

    fun generateCopyText(result: PortionResult): String {
        return buildString {
            if (result.foodName.isNotBlank()) {
                append("${result.foodName} | ")
            }
            append("${result.portionCount} portions of ${result.formattedPerPortion} ${result.displayUnit} each")
            if (result.hasLeftover) {
                append(" (${result.formattedRemaining} ${result.displayUnit} leftover)")
            }
        }
    }
}

class PortionViewModelFactory(
    private val repository: PortionRepository,
    private val preferencesRepository: AppPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortionViewModel::class.java)) {
            return PortionViewModel(repository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
