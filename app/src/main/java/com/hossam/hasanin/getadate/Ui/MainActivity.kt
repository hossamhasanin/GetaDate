package com.hossam.hasanin.getadate.Ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.getadate.Externals.getGender
import com.hossam.hasanin.getadate.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.asking_for_more_questions_popup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    var currentPage = MainPages.CARDS
    val themeListen = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        CoroutineScope(Dispatchers.Main).launch {
            val mAuth = FirebaseAuth.getInstance()
            if (mAuth.currentUser!!.getGender() == 1){
                setTheme(R.style.MaleMode)
                toolbar.setBackgroundResource(R.drawable.upper_tool_box2)
                setStatusBarColor(R.color.purpule)
            } else {
                setTheme(R.style.FeMaleMode)
                toolbar.setBackgroundResource(R.drawable.upper_tool_box2)
                setStatusBarColor(R.color.dark_bink)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


//
//        right_icon.setOnClickListener {
//            if (currentPage == MainPages.CARDS){
//                currentPage = MainPages.MATCHES
//                this.findNavController(R.id.nav_host_fragment).navigate(R.id.go_to_match_list)
//            }
//        }
//
//        left_icon.setOnClickListener {
//            if (currentPage == MainPages.CARDS){
//                currentPage = MainPages.PROFILE
//                this.findNavController(R.id.nav_host_fragment).navigate(R.id.go_to_profile)
//            } else {
////                if (currentPage == MainPages.SHOW_USER){
////                    currentPage = MainPages.MATCHES
////                } else if (currentPage == MainPages.RESERVE_RESTURANT || currentPage == MainPages.PICK_TIME) {
////                    currentPage = MainPages.SHOW_USER
////                } else if (currentPage == MainPages.PROFILE) {
////                    currentPage = MainPages.CARDS
////                } else if (currentPage == MainPages.MATCHES){
////                    currentPage = MainPages.CARDS
////                }
//                this.findNavController(R.id.nav_host_fragment).navigateUp()
//            }
//        }

        nav_bottom.selectedItemId = R.id.home
        nav_bottom.setOnNavigationItemSelectedListener {

            when {
                it.itemId == R.id.home -> this.findNavController(R.id.nav_host_fragment).navigate(R.id.cardsFragment)
                it.itemId == R.id.profile -> this.findNavController(R.id.nav_host_fragment).navigate(R.id.profileFragment)
                it.itemId == R.id.matches -> this.findNavController(R.id.nav_host_fragment).navigate(R.id.matchesFragment)
                it.itemId == R.id.settings -> {

                }
            }

            return@setOnNavigationItemSelectedListener true
        }

    }

    override fun onResume() {
        super.onResume()
        val new = intent.extras?.getBoolean("new")
        if (new != null && new){
            buildWelcomeMessage()
        }

    }

    private fun setStatusBarColor(color : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this@MainActivity , color)
        }
    }

    private fun buildWelcomeMessage(){
        val dialog = AlertDialog.Builder(this)
        val layout = layoutInflater.inflate(R.layout.asking_for_more_questions_popup , null)
        dialog.setView(layout)
        val ad = dialog.create()
        layout.answer_questions.setOnClickListener {
            ad.dismiss()
            this.findNavController(R.id.nav_host_fragment).navigate(R.id.answerQustionsFragment)
        }
        layout.later.setOnClickListener {
            ad.dismiss()
        }
        val btm = screenShot(main_activity_cont)
        val fastBlur = fastblur(btm , 10)
        val blurImage = BitmapDrawable(resources , fastBlur)
        ad.window?.setBackgroundDrawable(blurImage)
        ad.show()
    }

    fun screenShot(view: View): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(
            50,
            50, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun fastblur(sentBitmap: Bitmap, radius: Int): Bitmap? {
        val bitmap: Bitmap = sentBitmap.copy(sentBitmap.config, true)
        if (radius < 1) {
            return null
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        Log.e("pix", w.toString() + " " + h.toString() + " " + pix.size)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            rinsum = 0
            ginsum = 0
            binsum = 0
            routsum = 0
            goutsum = 0
            boutsum = 0
            rsum = 0
            gsum = 0
            bsum = 0

            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            rinsum = 0
            ginsum = 0
            binsum = 0
            routsum = 0
            goutsum = 0
            boutsum = 0
            rsum = 0
            gsum = 0
            bsum = 0

            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        Log.e("pix", w.toString() + " " + h.toString() + " " + pix.size)
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }


}

enum class MainPages{
    CARDS , PROFILE , MATCHES , SHOW_USER , RESERVE_RESTURANT
    , PICK_TIME , DETAILS , ADVICES , EDIT_CHARACTERISTICS , ENHANCEMENT_PERSONALITY
    , ADD_MORE_QUESTIONS , ANSWER_QUESTIIONS
}
