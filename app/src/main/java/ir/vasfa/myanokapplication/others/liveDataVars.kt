package ir.vasfa.myanokapplication.others

import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData

class liveDataVars : LifecycleService() {

    companion object {
        var selectedShapeId = MutableLiveData<Int>(-1)
    }
}