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
    private var spinnerCircles: MutableList<ImageView> = mutableListOf()
    private var isAnimationRunning: Boolean = false

    companion object {
        private const val CIRCLE_ANIMATION_DURATION: Long = 300L
    }

    init {
        inflate(context, R.layout.element_spinner, this)
        spinnerCircles.add(findViewById(R.id.spinner_circle_1))
        spinnerCircles.add(findViewById(R.id.spinner_circle_2))
        spinnerCircles.add(findViewById(R.id.spinner_circle_3))
        visibility = View.GONE
    }

    fun showAnimation(show: Boolean) {
        if (show == true && isAnimationRunning == false) {
            isAnimationRunning = true
            visibility = View.VISIBLE
            animatorSet.duration = spinnerCircles.count() * CIRCLE_ANIMATION_DURATION
            var animatorSetBuilder: AnimatorSet.Builder? = null
            for (i in 0 until spinnerCircles.count()) {
                val scaleAnimator = ValueAnimator.ofFloat(1.0f, 0.1f, 1.0f)
                scaleAnimator.addUpdateListener {
                    val value = it.animatedValue as Float
                    spinnerCircles[i].scaleX = value
                    spinnerCircles[i].scaleY = value
                }
                scaleAnimator.startDelay = i * CIRCLE_ANIMATION_DURATION
                scaleAnimator.repeatCount = Animation.INFINITE
                if (i == 0) {
                    animatorSetBuilder = animatorSet.play(scaleAnimator)
                } else {
                    animatorSetBuilder!!.with(scaleAnimator)
                }
            }

            animatorSet.start()
        } else if (show == false && isAnimationRunning == true) {
            visibility = View.GONE
            animatorSet.cancel()
            isAnimationRunning = false
        }
    }
}