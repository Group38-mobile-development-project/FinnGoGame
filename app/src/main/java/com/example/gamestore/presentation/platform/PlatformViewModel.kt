package com.example.gamestore.presentation.platform


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.data.model.Platform
import com.example.gamestore.data.repository.PlatformRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlatformViewModel : ViewModel() {
    private val _platforms = MutableStateFlow<List<Platform>>(emptyList())
    val platforms: StateFlow<List<Platform>> = _platforms

    init {
        viewModelScope.launch {
            _platforms.value = PlatformRepository().getPlatforms()
        }
    }
}
