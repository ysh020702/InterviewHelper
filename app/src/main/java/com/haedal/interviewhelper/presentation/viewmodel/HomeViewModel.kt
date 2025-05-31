package com.haedal.interviewhelper.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(

) : ViewModel() {
    var selectedQuestion by mutableStateOf<String?>(null)
        private set

    fun toggleSelection(question: String) {
        selectedQuestion = if (selectedQuestion == question) null else question
    }

    fun isSelected(question: String): Boolean = selectedQuestion == question
}