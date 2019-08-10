package com.hhmusic.utilities

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.hhmusic.provider.DBDataContract
import com.hhmusic.provider.DBDataContract.Companion.CONTENT_AUTHORITY

object SyncUtils {

    private val SYNC_FREQUENCY = (60 * 60).toLong()  // 1 hour (in seconds)

    // Value below must match the account type specified in res/xml/syncadapter.xml
    private val ACCOUNT_TYPE = "com.hhmusic.android.contentprovider.account"
    private val ACCOUNT_NAME = "Account"

    private val CONTENT_AUTHORITY = DBDataContract.CONTENT_AUTHORITY

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    fun CreateSyncAccount(context: Context): Account {
        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

        return Account(ACCOUNT_NAME, ACCOUNT_TYPE).also { newAccount ->
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                /*
                 * If you don't set android:syncable="true" in
                 * in your <provider> element in the manifest,
                 * then call context.setIsSyncable(account, AUTHORITY, 1)
                 * here.
                 */
                // Inform the system that this account supports sync
                ContentResolver.setIsSyncable(newAccount, CONTENT_AUTHORITY, 1)

                // Inform the system that this account is eligible for auto sync when the network is up
                ContentResolver.setSyncAutomatically(newAccount, CONTENT_AUTHORITY, true)

                // Recommend a schedule for automatic synchronization. The system may modify this based
                // on other scheduled syncs and network utilization.
                ContentResolver.addPeriodicSync(
                    newAccount, CONTENT_AUTHORITY, Bundle(), SYNC_FREQUENCY
                )
            }
        }
    }


    /**
     * Helper method to trigger an immediate sync ("refresh").
     *
     *
     * This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     *
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    fun TriggerRefresh(syncAcc: Account) {
        val b = Bundle()
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        ContentResolver.requestSync(
            syncAcc, // Sync account
            DBDataContract.CONTENT_AUTHORITY, // Content authority
            b   // Extras
        )
    }
}