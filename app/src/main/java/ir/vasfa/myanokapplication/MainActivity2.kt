package ir.vasfa.myanokapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import ir.vasfa.myanokapplication.others.Constants.viewHeight
import ir.vasfa.myanokapplication.others.Constants.viewWidth
import ir.vasfa.myanokapplication.others.liveDataVars
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {

    private var mTop: Int = 0
    var firstTime = true
    var viewId: Int = 0

    var selectedShapeId: Int = -1

    init {
        liveDataVars.selectedShapeId.observe(this, Observer {
            selectedShapeId = it
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnShape.setOnClickListener {
            viewId++;
            addNewShape(viewId, R.color.red)

            if (firstTime) {
                var y=getChildPosition(viewId,"y")
                firstTime = false
                mTop = y
            }
        }

        btnhorizontal.setOnClickListener {

            setConstraintForSelectedView(selectedShapeId)
            val childCount: Int = body.getChildCount()
            for (i in 0 until childCount) {
                val v: View = body.getChildAt(i)
                if (v.id != selectedShapeId)
                    alignVertically(v.id, selectedShapeId)
            }

        }

        btnvertical.setOnClickListener {
            setConstraintForSelectedView(selectedShapeId)
            val childCount: Int = body.getChildCount()
            for (i in 0 until childCount) {
                val v: View = body.getChildAt(i)
                if (v.id != selectedShapeId)
                    alignHorizontally(v.id, selectedShapeId)
            }
        }


    }

    private fun alignVertically(childId: Int, parentId: Int) {

        val y=getChildPosition(childId,"y")
        val constraintSet = ConstraintSet()

        constraintSet.clone(body)
        constraintSet.clear(childId)
        constraintSet.constrainHeight(
            childId,
            viewHeight * childId
        );
        constraintSet.constrainWidth(
            childId,
            viewWidth * childId
        );
        constraintSet.centerHorizontally(childId, parentId)
        constraintSet.connect(
            childId, TOP,
            body.id, TOP, 0
        );
        constraintSet.connect(
            childId, LEFT,
            body.id, LEFT, 0
        )
        constraintSet.setMargin(childId, TOP, (y - mTop))
        constraintSet.connect(childId, END, parentId, END)
        constraintSet.connect(childId, TOP, body.id, TOP)
        constraintSet.connect(childId, START, parentId, START)

        constraintSet.applyTo(body)

    }

    private fun alignHorizontally(childId: Int, parentId: Int) {

        val x=getChildPosition(childId,"x")
        val constraintSet = ConstraintSet()

        constraintSet.clone(body)
        constraintSet.clear(childId)
        constraintSet.constrainHeight(
            childId,
            viewHeight * childId
        );
        constraintSet.constrainWidth(
            childId,
            viewWidth * childId
        );
        constraintSet.centerHorizontally(childId, parentId)
        constraintSet.connect(
            childId, TOP,
            body.id, TOP, 0
        );
        constraintSet.connect(
            childId, LEFT,
            body.id, LEFT, 0
        )
        constraintSet.setMargin(childId, START, x)
        constraintSet.connect(childId, TOP, parentId, TOP)
        constraintSet.connect(childId, BOTTOM, parentId, BOTTOM)
        constraintSet.connect(childId, START, body.id, START)

        constraintSet.applyTo(body)
    }

    private fun getChildPosition(childId: Int, type: String): Int {
        var item: Int = -1
        val chi1 = find<RelativeLayout>(childId)
        val location = IntArray(2)
        chi1.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]

        when (type) {
            "x" -> item = x
            "y" -> item = y
        }

        return item
    }

    private fun addNewShape(id: Int, bgColor: Int) {

        val view = RelativeLayout(this)
        view.id = id
        view.setBackgroundColor(getResources().getColor(bgColor));

        var cc = ChoiceTouchListener(applicationContext)
        view.setOnTouchListener(cc)


        body.addView(view)

        setConstraints(view.id)

    }

    private fun setConstraints(childId: Int) {

        val set = ConstraintSet()
        set.clone(body);
        set.constrainHeight(
            childId, viewHeight * childId
        )
        set.constrainWidth(
            childId,
            viewWidth * childId
        )
        set.connect(
            childId, TOP,
            body.id, TOP, 0
        )
        set.connect(
            childId, LEFT,
            body.id, LEFT, 0
        )
        set.applyTo(body);

    }

    class ChoiceTouchListener @Inject constructor(
        @ActivityContext private val activity: Context,
    ) : View.OnTouchListener {
        private var _xDelta: Float = 0.0f
        private var _yDelta: Float = 0.0f

        @SuppressLint("NewApi")
        override fun onTouch(view: View, event: MotionEvent): Boolean {

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    _xDelta = view.x - event.rawX
                    _yDelta = view.y - event.rawY
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> {

                    liveDataVars.selectedShapeId.postValue(view.id)

                    val constraintLayoutId = (view.getParent() as ConstraintLayout)
                    val childCount: Int = constraintLayoutId.getChildCount()
                    for (i in 0 until childCount) {
                        val v: View = constraintLayoutId.getChildAt(i)
                        v.setBackgroundColor(
                            activity.getResources().getColor(
                                R.color.red
                            )
                        )
                    }

                    view.setBackgroundColor(
                        activity.getResources().getColor(
                            R.color.green
                        )
                    );
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + _xDelta)
                        .y(event.rawY + _yDelta)
                        .setDuration(0)
                        .start()
                }
            }
            return true
        }

    }

    private fun setConstraintForSelectedView(parentId: Int) {

        val chi1 = find<RelativeLayout>(parentId)
        val location = IntArray(2)
        chi1.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        val constraintSet = ConstraintSet()

        constraintSet.clone(body)
        constraintSet.clear(parentId)
        constraintSet.constrainHeight(
            parentId,
            viewHeight * parentId
        )
        constraintSet.constrainWidth(
            parentId,
            viewWidth * parentId
        )
        constraintSet.connect(
            parentId, TOP,
            body.id, TOP, 0
        )
        constraintSet.connect(
            parentId, START,
            body.id, START, 0
        )
        constraintSet.setMargin(parentId, START, x)
        constraintSet.setMargin(parentId, TOP, y - mTop)

        constraintSet.applyTo(body)

    }
}