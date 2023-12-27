package eu.tutorials.jettrivia.repository

import eu.tutorials.jettrivia.data.DataOrException
import eu.tutorials.jettrivia.model.QuestionItem
import eu.tutorials.jettrivia.network.QuestionApi
import javax.inject.Inject

//@Inject is part of Hilt and we are injecting the dependency of QuestionApi into here
class QuestionRepository @Inject constructor(private val api:QuestionApi) {
    private val dataOrException
    = DataOrException<ArrayList<QuestionItem>,
            Boolean,
            Exception>()

    suspend fun getAllQuestions():DataOrException<ArrayList<QuestionItem>,Boolean,Exception>{
        try{
            //we are trying to load something now
            dataOrException.loading = true
            //Getting all the questions using our Dependency Injection - to get the json file
            dataOrException.data = api.getAllQuestions()
            //IF WE FINISHED FETCHING THE DATA AND WE HAVE SOME DATA
            if(dataOrException.data.toString().isNotEmpty()){
                dataOrException.loading = false
            }
        }
        catch (exception:Exception){
            dataOrException.e = exception
        }
        return dataOrException
    }

}