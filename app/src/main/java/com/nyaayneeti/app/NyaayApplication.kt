package com.nyaayneeti.app

import android.app.Application
import com.nyaayneeti.app.data.firebase.FirebaseModule
import com.nyaayneeti.app.data.firebase.SyncEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

class NyaayApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        FirebaseModule.init(this)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName
        container = AppContainer(this)
        appScope.launch { container.seedIfEmpty() }
        if (container.firebaseEnabled) {
            SyncEngine(container.database, FirebaseModule.firestore, appScope).start()
        }
    }
}
