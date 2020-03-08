package com.hossam.hasanin.getadate.Ui.Fragments.MainPage

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hossam.hasanin.getadate.R
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.user_card_expandable_header.*


class ExpandableHeader(private val headerTitle: String , private val card: ViewGroup , private val likeBtn: ImageView , private val disLikeBtn: ImageView) : Item() , ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.header_title.text = headerTitle
        viewHolder.item_expandable_header_icon.setImageResource(getRotatedIconResId())

        viewHolder.header_title.setOnClickListener{
            expandableGroup.onToggleExpanded()
            viewHolder.item_expandable_header_icon.setImageResource(getRotatedIconResId())
            if (expandableGroup.isExpanded){
                //likeBtn.visibility = View.GONE
                //disLikeBtn.visibility = View.GONE
                expand(card)
            } else {
                //likeBtn.visibility = View.VISIBLE
                //disLikeBtn.visibility = View.VISIBLE
                collapse(card)
            }
        }
    }

    override fun getLayout() = R.layout.user_card_expandable_header

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    private fun getRotatedIconResId() =
        if (expandableGroup.isExpanded)
            R.drawable.ic_keyboard_arrow_up_black_24dp
        else
            R.drawable.ic_keyboard_arrow_down_black_24dp


    var originalHeight = 0
    private fun expand(v : View){
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            (v.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight
        originalHeight = v.height
        Log.v("koko" , "d $targetHeight")

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.

        //v.layoutParams.height = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.MATCH_PARENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms


        // Expansion speed of 1dp/ms
        // Expansion speed of 1dp/ms
        a.setDuration((targetHeight / v.context.resources.displayMetrics.density).toLong() * 6)
        v.startAnimation(a)
    }

    private fun collapse(v: View){
        val initialHeight = (v.parent as ConstraintLayout).measuredHeight

        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                v.layoutParams.height = initialHeight - ((initialHeight-originalHeight) * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms


        // Collapse speed of 1dp/ms
        // Collapse speed of 1dp/ms
        a.setDuration((initialHeight / v.context.resources.displayMetrics.density).toLong())
        v.startAnimation(a)
    }

    private fun dp2px(context:Context , dp:Int) : Int{
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay

        val displaymetrics = DisplayMetrics()
        display.getMetrics(displaymetrics)

        return (dp * displaymetrics.density).toInt()
    }

}