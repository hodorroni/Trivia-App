package eu.tutorials.jettrivia.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.jettrivia.model.QuestionItem
import eu.tutorials.jettrivia.screens.QuestionsViewModel
import eu.tutorials.jettrivia.util.AppColors

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val context = LocalContext.current

    val questions = viewModel.data.value.data?.toMutableList()

    val questionIndex = remember{
        mutableStateOf(0)
    }

    if(viewModel.data.value.loading == true){
        CircularProgressIndicator(modifier = Modifier.fillMaxHeight(0.5f))
    }
    //will be false whenever we fetched the entire data
    else {
        //trying to get the next question inside of our questions mutable list
        val question = try{
            questions?.get(questionIndex.value)
        }
        catch (ex:Exception){
            null
        }

        if(questions != null && question != null){
            QuestionDisplay(question = question!!,questionIndex = questionIndex,
                totalQuestions = viewModel.getTotalQuestionCount() ,
                viewModel = viewModel)
            //the onNextClicked lambda function
            {
                questionIndex.value = questionIndex.value + 1
            }
        }
        else {
            Toast.makeText(context, "No more questions to see", Toast.LENGTH_SHORT).show()
        }
    }

}



//@Preview
@Composable
fun QuestionDisplay(
    question:QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    totalQuestions:Int,
    onNextClicked: (Int) -> Unit = {}

    ){
    //will hold the question state, if question changes then choicesState will too
    val choicesState = remember(question){
        question.choices.toMutableList()
    }

    //will hold the state for the answer index inside of the choices answers

    val answerState = remember(question){
        mutableStateOf<Int?>(null)
    }

    //will hold the value if true for correct answer or false to wrong answer

    val correctAnswerState = remember(question){
        mutableStateOf<Boolean?>(null)
    }

    //inside we will get a boolean in which will be set to updateAnswer afterwards
    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            //will be the index that's passed when clicking the radio button
            answerState.value = it
            //checking if the item inside choicesState at the same index
            //is equal to the real answer, if so that's correct !
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }

    //10 pixels on 10 pixels off 10 pixels on 10 pexels off to the draw a dashed line
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f), 0f)
    Surface(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        color = AppColors.mDarkPurple) {
        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start)
        {
            if(questionIndex.value >=3) showProgress(score = questionIndex.value,
                totalQuestions = totalQuestions)
            QuestionTracker(counter = questionIndex.value, outOf = totalQuestions)
            DrawDottedLine(pathEffect =pathEffect )
            Column {
                Text(text = "${question.question}",
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        //the height will be 30% of the all height it can use
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp,
                    color = AppColors.mOffWhite,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp)
                //choices array list for each question
                choicesState.forEachIndexed { index, answerText ->
                    Row(modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(
                            width = 4.dp, brush = Brush.linearGradient(
                                colors = listOf(
                                    AppColors.mOffDarkPurple,
                                    AppColors.mOffDarkPurple
                                )
                            ), shape = RoundedCornerShape(15.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 50
                            )
                        )
                        .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically){
                        RadioButton(selected =(answerState.value == index) ,
                            onClick = {
                                updateAnswer(index)
                            },
                            modifier = Modifier.padding(start = 16.dp),
                            colors = RadioButtonDefaults
                                .colors(
                                    selectedColor =
                                        if(correctAnswerState.value == true &&
                                            index==answerState.value){
                                            Color.Green.copy(alpha=0.2f)
                                        }
                                    else {
                                        Color.Red.copy(alpha = 0.2f)
                                        }
                                )) // end of radio button
                        //will show the possible choices per question

                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light,
                                color =
                                if(correctAnswerState.value == true &&
                                    index==answerState.value){
                                    Color.Green
                                }
                                else if(correctAnswerState.value == false
                                    && index == answerState.value) {
                                    Color.Red.copy(alpha = 0.2f)
                                }
                                else {
                                    AppColors.mOffWhite
                                }, fontSize = 17.sp)){
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                    }
                }
                Button(onClick = {
                    //a function in which we are passing the next questionIndex
                    onNextClicked(questionIndex.value)

                }, modifier = Modifier
                    .padding(3.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.mLightBlue
                    )
                ) {
                    Text(text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite,
                        fontSize = 17.sp)
                }
            }
        }
    }
}


@Composable
fun DrawDottedLine(pathEffect: PathEffect){
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp))
    {
        drawLine(color = AppColors.mLightGray,
            start = Offset(0f,0f),
            end = Offset(size.width, y= 0f),
            pathEffect = pathEffect
        )

    }
}

@Preview
@Composable
fun showProgress(score:Int = 12, totalQuestions:Int=4798){
    val gradient = Brush.linearGradient(listOf(
        Color(0XFFF95075),
        Color(0XFFBE6BE5)))

//    val progressFactor = remember(score,totalQuestions) {
//        mutableStateOf(score*0.005f)
//    }

    //for the progress bar converted to float
    val progressFactor = remember(score, totalQuestions) {
        mutableStateOf(calculateProgressFactor(score, totalQuestions))
    }

    val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()
    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(
            width = 4.dp, brush = Brush.linearGradient(
                colors = listOf(
                    AppColors.mLightPurple, AppColors.mLightPurple
                )
            ),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(
            RoundedCornerShape(
                topStartPercent = 50,
                topEndPercent = 50,
                bottomStartPercent = 50,
                bottomEndPercent = 50
            )
        )
        .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically)
    {
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = {  },
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            //disabling the button colors build in
            colors = buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )) {
            Text(text = percentage.toString(),
                modifier = Modifier.clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center)

        }

    }
}

// Function to calculate the progress factor based on current and total questions
private fun calculateProgressFactor(currentQuestion: Int, totalQuestions: Int): Float {
    return (currentQuestion.toFloat() / totalQuestions.toFloat())
}

//@Preview
@Composable
fun QuestionTracker(counter:Int = 10,
                    outOf:Int = 100)
{
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)){
            withStyle(style = SpanStyle(color = AppColors.mLightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp)){
                append("Question $counter/")
                withStyle(style = SpanStyle(color = AppColors.mLightGray,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp)
                ){
                    append("$outOf")
                }
            }
        }


    },
        modifier = Modifier.padding(20.dp))
}