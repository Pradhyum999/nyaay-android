package com.nyaayneeti.app.data

import com.nyaayneeti.app.data.model.CaseProfile
import kotlinx.coroutines.flow.MutableStateFlow

/** Holds the most recent AI intake profile so the directory can match lawyers to it. */
object IntakeStore {
    val profile = MutableStateFlow<CaseProfile?>(null)
}
