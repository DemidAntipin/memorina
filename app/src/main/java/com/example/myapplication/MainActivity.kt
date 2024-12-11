package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private var firstCard: ImageView? = null
    private var openCardsCount = 0
    private var remainingCards = 16
    private val theMap = mapOf(
        "cat" to R.drawable.cat,
        "ctulhu" to R.drawable.ctulhu,
        "owl" to R.drawable.owl,
        "castle" to R.drawable.castle,
        "moon" to R.drawable.moon,
        "nebula" to R.drawable.nebula,
        "tower" to R.drawable.tower,
        "freedom" to R.drawable.freedom,
        "cardback" to R.drawable.cardback
    )

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startGame()
    }

    private fun startGame() {
        openCardsCount = 0
        remainingCards = 16

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1f

        val colorListener = View.OnClickListener { view ->
            if (openCardsCount < 2) {
                (view as ImageView).setImageResource(theMap[view.tag]!!)
                openCardsCount++

                if (openCardsCount == 1) {
                    firstCard = view
                    view.isClickable = false
                } else {
                    layout.isClickable = false
                    if (view.tag == firstCard?.tag) {
                        coroutineScope.launch {
                            delay(1000)
                            view.visibility = View.INVISIBLE
                            firstCard?.visibility = View.INVISIBLE
                            openCardsCount = 0
                            remainingCards -= 2
                            checkForWin()
                            layout.isClickable = true
                        }
                    } else {
                        coroutineScope.launch {
                            delay(1000)
                            view.setImageResource(theMap["cardback"]!!)
                            firstCard?.setImageResource(theMap["cardback"]!!)
                            view.isClickable = true
                            firstCard?.isClickable = true
                            openCardsCount = 0
                            layout.isClickable = true
                        }
                    }
                }
            } else {
                Log.d("mytag", "two cards are open already")
            }
        }

        val catViews = ArrayList<ImageView>()
        for (i in 0..15) {
            val name: String = theMap.keys.elementAt(i / 2)
            catViews.add(ImageView(this).apply {
                id = i
                setImageResource(theMap["cardback"]!!)
                layoutParams = params
                tag = name
                setOnClickListener(colorListener)
            })
        }

        catViews.shuffle()
        val rows = Array(4) { LinearLayout(this) }

        var count = 0
        for (view in catViews) {
            val row: Int = count / 4
            rows[row].addView(view)
            count++
        }
        for (row in rows) {
            layout.addView(row)
        }
        setContentView(layout)
    }

    private fun checkForWin() {
        if (remainingCards == 0) {
            showWinToast()
        }
    }

    private fun showWinToast() {
        Toast.makeText(this, "Поздравляем! Вы выиграли!", Toast.LENGTH_SHORT).show()
        startGame()
    }
}
