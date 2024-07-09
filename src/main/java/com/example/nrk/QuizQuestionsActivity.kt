package com.example.nrk

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var ivImage: ImageView
    private lateinit var tvOptionOne: TextView
    private lateinit var tvOptionTwo: TextView
    private lateinit var tvOptionThree: TextView
    private lateinit var tvOptionFour: TextView
    private lateinit var btnSubmit: TextView
    private lateinit var tvTimer: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val timerDuration: Long = 10000
    private var isTimerRunning = false
    private var isAnswerShown = false

    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mCorrectAnswers: Int = 0
    private var mUserName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        mUserName = intent.getStringExtra(Constants.USER_NAME)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)
        tvOptionOne = findViewById(R.id.tv_option_one)
        tvOptionTwo = findViewById(R.id.tv_option_two)
        tvOptionThree = findViewById(R.id.tv_option_three)
        tvOptionFour = findViewById(R.id.tv_option_four)
        btnSubmit = findViewById(R.id.btn_submit)
        tvTimer = findViewById(R.id.tv_timer)

        mQuestionsList = Constants.getQuestions()

        setQuestion()

        tvOptionOne.setOnClickListener(this)
        tvOptionTwo.setOnClickListener(this)
        tvOptionThree.setOnClickListener(this)
        tvOptionFour.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_option_one -> {
                if (isTimerRunning) selectedOptionView(tvOptionOne, 1)
            }
            R.id.tv_option_two -> {
                if (isTimerRunning) selectedOptionView(tvOptionTwo, 2)
            }
            R.id.tv_option_three -> {
                if (isTimerRunning) selectedOptionView(tvOptionThree, 3)
            }
            R.id.tv_option_four -> {
                if (isTimerRunning) selectedOptionView(tvOptionFour, 4)
            }
            R.id.btn_submit -> {
                if (isTimerRunning) {
                    countDownTimer.cancel()
                    isTimerRunning = false

                    if (mSelectedOptionPosition == 0) {
                        showCorrectAnswer()
                    } else {
                        val question = mQuestionsList?.get(mCurrentPosition - 1)
                        if (question!!.correctAnswer != mSelectedOptionPosition) {
                            answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                        } else {
                            mCorrectAnswers++
                        }

                        answerView(question.correctAnswer, R.drawable.correct_option_border_bg)
                    }

                    isAnswerShown = true

                    if (mCurrentPosition == mQuestionsList!!.size) {
                        btnSubmit.text = "FINISH"
                    } else {
                        btnSubmit.text = "NEXT QUESTION"
                    }
                    mSelectedOptionPosition = 0
                } else {
                    mCurrentPosition++
                    if (mCurrentPosition <= mQuestionsList!!.size) {
                        isAnswerShown = false
                        setQuestion()
                    } else {
                        val intent = Intent(this@QuizQuestionsActivity, ResultActivity::class.java)
                        intent.putExtra(Constants.USER_NAME, mUserName)
                        intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                        intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setQuestion() {
        val question = mQuestionsList!!.get(mCurrentPosition - 1)
        startTimer()

        defaultOptionsView()

        if (!isAnswerShown) {
            progressBar.progress = mCurrentPosition
            tvProgress.text = "$mCurrentPosition/${progressBar.max}"
            tvQuestion.text = question.question
            ivImage.setImageResource(question.image)
            tvOptionOne.text = question.optionOne
            tvOptionTwo.text = question.optionTwo
            tvOptionThree.text = question.optionThree
            tvOptionFour.text = question.optionFour

            btnSubmit.text = "SUBMIT"
        } else {
            showCorrectAnswer()
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        countDownTimer = object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                tvTimer.text = "Seconds left: $secondsRemaining"
            }

            override fun onFinish() {
                isTimerRunning = false
                showCorrectAnswer()
                isAnswerShown = true
                if (mCurrentPosition == mQuestionsList!!.size) {
                    btnSubmit.text = "FINISH"
                } else {
                    btnSubmit.text = "NEXT QUESTION"
                }
            }
        }.start()
    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        options.add(0, tvOptionOne)
        options.add(1, tvOptionTwo)
        options.add(2, tvOptionThree)
        options.add(3, tvOptionFour)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this@QuizQuestionsActivity,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum

        tv.setTextColor(
            Color.parseColor("#363A43")
        )
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this@QuizQuestionsActivity,
            R.drawable.selected_option_border_bg
        )
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> {
                tvOptionOne.background = ContextCompat.getDrawable(
                    this@QuizQuestionsActivity,
                    drawableView
                )
            }
            2 -> {
                tvOptionTwo.background = ContextCompat.getDrawable(
                    this@QuizQuestionsActivity,
                    drawableView
                )
            }
            3 -> {
                tvOptionThree.background = ContextCompat.getDrawable(
                    this@QuizQuestionsActivity,
                    drawableView
                )
            }
            4 -> {
                tvOptionFour.background = ContextCompat.getDrawable(
                    this@QuizQuestionsActivity,
                    drawableView
                )
            }
        }
    }

    private fun showCorrectAnswer() {
        val question = mQuestionsList!![mCurrentPosition - 1]
        answerView(question.correctAnswer, R.drawable.correct_option_border_bg)
    }
}