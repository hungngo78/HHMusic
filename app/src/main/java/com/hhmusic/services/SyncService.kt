package com.hhmusic.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SyncService : Service() {

    private val TAG = "SyncService"

    private val sSyncAdapterLock = Any()
    private var sSyncAdapter: SyncAdapter? = null

    /**
     * Thread-safe constructor, creates static [SyncAdapter] instance.
     */
    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "Service created")
        synchronized(sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = SyncAdapter(applicationContext, true)
            }
        }
    }

    /**
     * Logging-only destructor.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service destroyed")
    }

    /**
     * Return Binder handle for IPC communication with [SyncAdapter].
     *
     *
     * New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for [SyncAdapter]
     */
    override fun onBind(intent: Intent): IBinder? {
        return sSyncAdapter!!.getSyncAdapterBinder()
    }
}