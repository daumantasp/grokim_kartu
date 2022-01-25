package com.dauma.grokimkartu.ui.viewelements

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import com.dauma.grokimkartu.R

//https://medium.com/@douglas.iacovelli/the-beauty-of-custom-views-and-how-to-do-it-79c7d78e2088
class SpinnerViewElement(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val animatorSet: AnimatorSet = AnimatorSet()
    private val spinnerContainerLinearLayout: LinearLayout
    private var spinnerCircles: MutableList<ImageView> = mutableListOf()
    private var isAnimationRunning: Boolean = false

    companion object {
        private const val CIRCLE_ANIMATION_DURATION: Long = 300L
    }

    init {
        inflate(context, R.layout.element_spinner, this)
        spinnerContainerLinearLayout = findViewById(R.id.spinnerContainerLinearLayout)
        spinnerCircles.add(findViewById(R.id.spinner_circle_1))
        spinnerCircles.add(findViewById(R.id.spinner_circle_2))
        spinnerCircles.add(findViewById(R.id.spinner_circle_3))
        visibility = View.INVISIBLE

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SpinnerViewElement)
        val unitDimensions = attributes.getDimension(R.styleable.SpinnerViewElement_unitDimensions, 18f)
        spinnerContainerLinearLayout.clipChildren = false
        spinnerContainerLinearLayout.clipToPadding = false
        spinnerContainerLinearLayout.setPadding(unitDimensions.toInt(), unitDimensions.toInt(), unitDimensions.toInt(), unitDimensions.toInt())
        for (i in 0 until spinnerCircles.count()) {
            spinnerCircles[i].layoutParams.width = unitDimensions.toInt()
            spinnerCircles[i].layoutParams.height = unitDimensions.toInt()
        }
    }

    fun showAnimation(show: Boolean) {
        if (show == true && isAnimationRunning == false) {
            isAnimationRunning = true
            visibility = View.VISIBLE
            animatorSet.duration = spinnerCircles.count() * CIRCLE_ANIMATION_DURATION
            var animatorSetBuilder: AnimatorSet.Builder? = null
            for (i in 0 until spinnerCircles.count()) {
                val tY = (spinnerCircles[i].layoutParams.height / -2.0).toFloat()
                val tYAnimator = ValueAnimator.ofFloat(0f, tY, 0f)
                tYAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    spinnerCircles[i].translationY = value
                }
                val scaleAnimator = ValueAnimator.ofFloat(1.0f, 0.3f, 1.0f)
                scaleAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    spinnerCircles[i].scaleX = value
                    spinnerCircles[i].scaleY = value
                }
                tYAnimator.startDelay = i * CIRCLE_ANIMATION_DURATION
                tYAnimator.repeatCount = Animation.INFINITE
                scaleAnimator.startDelay = i * CIRCLE_ANIMATION_DURATION
                scaleAnimator.repeatCount = Animation.INFINITE
                if (i == 0) {
                    animatorSetBuilder = animatorSet.play(tYAnimator).with(scaleAnimator)
                } else {
                    animatorSetBuilder!!.with(tYAnimator).with(scaleAnimator)
                }
            }

            animatorSet.start()
        } else if (show == false && isAnimationRunning == true) {
            visibility = View.INVISIBLE
            animatorSet.cancel()
            isAnimationRunning = false
        }
    }
    // https://www.raywenderlich.com/2785491-android-animation-tutorial-with-kotlin
}