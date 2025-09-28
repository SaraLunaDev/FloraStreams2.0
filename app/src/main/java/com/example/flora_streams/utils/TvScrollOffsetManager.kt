package com.example.flora_streams.utils

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class TvScrollOffsetManager(
    private val context: Context,
    private val recyclerView: RecyclerView
) {
    
    companion object {
        private const val SCROLL_OFFSET_TOP_DP = 20
        private const val SCROLL_OFFSET_BOTTOM_DP = 20
    }
    
    private val scrollOffsetTopPx: Int
    private val scrollOffsetBottomPx: Int
    private var isSetup = false
    
    init {
        val density = context.resources.displayMetrics.density
        scrollOffsetTopPx = (SCROLL_OFFSET_TOP_DP * density).toInt()
        scrollOffsetBottomPx = (SCROLL_OFFSET_BOTTOM_DP * density).toInt()
    }

    fun isTvDevice(): Boolean {
        val uiMode = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK
        return uiMode == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION ||
               context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    fun setupTvScrollOffset() {
        if (!isTvDevice() || isSetup) return

        recyclerView.setPadding(
            recyclerView.paddingLeft,
            scrollOffsetTopPx,
            recyclerView.paddingRight,
            scrollOffsetBottomPx
        )

        recyclerView.clipToPadding = false

        setupFocusHandling()
        
        isSetup = true
    }

    private fun setupFocusHandling() {
        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                view.onFocusChangeListener = View.OnFocusChangeListener { focusedView, hasFocus ->
                    if (hasFocus) {
                        smoothScrollToFocusedItem(focusedView)
                    }
                }
            }
            
            override fun onChildViewDetachedFromWindow(view: View) {
                view.onFocusChangeListener = null
            }
        })

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            child.onFocusChangeListener = View.OnFocusChangeListener { focusedView, hasFocus ->
                if (hasFocus) {
                    smoothScrollToFocusedItem(focusedView)
                }
            }
        }
    }

    private fun smoothScrollToFocusedItem(focusedView: View) {
        val position = recyclerView.getChildAdapterPosition(focusedView)
        if (position == RecyclerView.NO_POSITION) return

        val itemTop = focusedView.top
        val itemBottom = focusedView.bottom
        val recyclerHeight = recyclerView.height
        val scrollY = recyclerView.computeVerticalScrollOffset()
        
        val targetScrollY = when {
            itemTop < scrollOffsetTopPx -> {
                max(0, scrollY - (scrollOffsetTopPx - itemTop))
            }
            itemBottom > recyclerHeight - scrollOffsetBottomPx -> {
                scrollY + (itemBottom - (recyclerHeight - scrollOffsetBottomPx))
            }
            else -> scrollY
        }

        if (targetScrollY != scrollY) {
            recyclerView.smoothScrollBy(0, targetScrollY - scrollY)
        }
    }

    fun onDataLoaded() {
        if (!isTvDevice() || !isSetup) return

        recyclerView.post {
            if (recyclerView.adapter != null && recyclerView.adapter!!.itemCount > 0) {
                recyclerView.scrollToPosition(0)
            }
        }
    }
}