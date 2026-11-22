package com.example.tola.worker

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tola.data.LostFoundReport
import java.util.UUID
import java.util.concurrent.TimeUnit

object ReportSyncScheduler {

    fun scheduleReportSync(
        context: Context,
        report: LostFoundReport,
        ownerUid: String
    ): String {
        val reportId = report.id.ifBlank { UUID.randomUUID().toString() }

        val inputData = Data.Builder()
            .putString(SyncReportWorker.KEY_REPORT_ID, reportId)
            .putString(SyncReportWorker.KEY_OWNER_UID, ownerUid)
            .putString(SyncReportWorker.KEY_ITEM_NAME, report.itemName)
            .putString(SyncReportWorker.KEY_CATEGORY, report.category)
            .putString(SyncReportWorker.KEY_DESCRIPTION, report.description)
            .putString(SyncReportWorker.KEY_LOCATION, report.location)
            .putString(SyncReportWorker.KEY_DATE, report.date)
            .putString(SyncReportWorker.KEY_TIME, report.time)
            .putString(SyncReportWorker.KEY_TYPE, report.type)
            .putString(SyncReportWorker.KEY_IMAGE_URL, report.imageUrl)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncReportWorker>()
            .setInputData(inputData.build())
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "sync_report_$reportId",
            ExistingWorkPolicy.KEEP,
            workRequest
        )

        Log.d(SyncReportWorker.TAG, "Scheduled background report sync for reportId $reportId (User: $ownerUid)")
        return reportId
    }
}
