package com.sgk.myworldmogilev.helper

import android.content.ClipDescription
import android.content.Context
import android.view.DragEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.sgk.myworldmogilev.views.activities.DragDropActivity
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.views.activities.TimeLineActivity

class DragAndDropListener : View.OnDragListener {
    companion object {
        lateinit var activity: Context
    }

    override fun onDrag(view: View, event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.setDashedOutline()
                (((event.localState as View).findViewById(R.id.colored)) as View).setDashedOutline()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                val views: DragAndDropContainer =
                    ((event.localState as View).parent as DragAndDropContainer)

                if (views.tag != "choice") views.setSolidOutline()
                if (view.tag == "check")
                    view.setSolidOutline()
                else
                    view.setSolidInput()

                true
            }
            DragEvent.ACTION_DROP -> {
                val draggingView = event.localState as View
                val draggingViewParent = draggingView.parent as DragAndDropContainer
                val landingContainer = view as DragAndDropContainer

                (((event.localState as View).findViewById(R.id.colored)) as View).setInput()

                if (view.getChildAt(0) != null) {
                    val landingView = view.getChildAt(0)

                    draggingViewParent.removeContent(draggingView)
                    landingContainer.removeContent(landingView)
                    landingContainer.setContent(draggingView)
                    draggingViewParent.setContent(landingView)
                } else {
                    draggingViewParent.removeContent(draggingView)
                    landingContainer.setContent(draggingView)
                    if (activity == DragDropActivity.context)
                        DragDropActivity.checkers()
                    else if (activity == TimeLineActivity.context)
                        TimeLineActivity.checkers()
                }
                true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            else -> {
                true
            }
        }
    }

    private fun View.setSolidOutline() {
        background = ContextCompat.getDrawable(context, R.color.drag_drop_box)
    }

    private fun View.setSolidInput() {
        background = ContextCompat.getDrawable(context, R.color.light_gray)
    }

    private fun View.setInput() {
        background = ContextCompat.getDrawable(context, android.R.color.transparent)
    }

    private fun View.setDashedOutline() {
        background = ContextCompat.getDrawable(context, R.color.drag_drop_box_gray)
    }
}