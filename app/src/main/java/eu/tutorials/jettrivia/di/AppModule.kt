package eu.tutorials.jettrivia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.tutorials.jettrivia.network.QuestionApi
import eu.tutorials.jettrivia.repository.QuestionRepository
import eu.tutorials.jettrivia.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    //Dagger and Hilt will make behind the scenes as such:
    //when ever QuestionRepository will be needed, it will execute provideQuestionRepository this function
    //automatically and since it needs QuestionApi it will execute provideQuestionApi function as well
    //and will run the code in the background and will do all the jon
    fun provideQuestionRepository(api: QuestionApi) = QuestionRepository(api)


    //want only one instance of that class
    @Singleton
    //will provide something to our dependencies meaning this object can be injected into other classes
    //we will be able to provide that dependency to who ever needs it
    @Provides
    fun provideQuestionApi(): QuestionApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            //the class in which responsible to create that service
            .create(QuestionApi::class.java)
    }
}