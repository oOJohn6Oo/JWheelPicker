package io.john6.base.compose.picker.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.motion.MaterialBottomContainerBackHelper

/**
 * 可预测返回动画辅助类，拓展自 [MaterialBottomContainerBackHelper]
 *
 * Compose BottomSheetDialog 实现自全屏的 ModalBottomSheet
 * 动画过程中 pivotY 坐标与 View 不一致，重写 [updateBackProgress] 及 [cancelBackProgress] 确保体验一致
 */
@SuppressLint("RestrictedApi", "PrivateResource")
class JBottomContainerBackHelper(view: View) : MaterialBottomContainerBackHelper(view) {

    override fun updateBackProgress(progress: Float) {
        val desireProgress = interpolateProgress(progress)

        val width = view.width.toFloat()
        val height = view.height.toFloat()
        if (width <= 0f || height <= 0f) {
            return
        }

        val desireScaleDelta = 144 / width
        val finalScaleDelta = AnimationUtils.lerp(0f, desireScaleDelta, desireProgress)
        val finalScale = 1 - finalScaleDelta
        view.scaleX = finalScale
        view.pivotY = height
        view.scaleY = finalScale

    }

    override fun cancelBackProgress() {

        if (super.onCancelBackProgress() == null) {
            return
        }

        val animator = createResetScaleAnimator()
        animator.setDuration(cancelDuration.toLong())
        animator.start()
    }


    private fun createResetScaleAnimator(): Animator {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(view, View.SCALE_X, 1f),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f)
        )
        animatorSet.interpolator = FastOutSlowInInterpolator()
        return animatorSet
    }

}