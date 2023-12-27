package eu.tutorials.jettrivia.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.tutorials.jettrivia.data.DataOrException
import eu.tutorials.jettrivia.model.QuestionItem
import eu.tutorials.jettrivia.repository.QuestionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(private val repository: QuestionRepository): ViewModel() {
    //variable what will hold a mutable state to the actual UI
    val data:MutableState<DataOrException<ArrayList<QuestionItem>,
            Boolean, Exception>>
    //Instantiating the data class of DataOrException
    = mutableStateOf(
                DataOrException(null,true, Exception("")))


    init {
        getAllQuestions()
    }

    private fun getAllQuestions(){
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()
            if(data.value.data.toString().isNotEmpty()){
                data.value.loading = false
            }
        }
    }
    fun getTotalQuestionCount(): Int {
        return data.value.data?.toMutableList()?.size ?: 0
    }
}


