package com.example.tola.worker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SyncReportWorkerTest {

    @Test
    fun workerKeys_areConsistent() {
        assertEquals("KEY_REPORT_ID", SyncReportWorker.KEY_REPORT_ID)
        assertEquals("KEY_OWNER_UID", SyncReportWorker.KEY_OWNER_UID)
        assertEquals("KEY_ITEM_NAME", SyncReportWorker.KEY_ITEM_NAME)
        assertEquals("KEY_CATEGORY", SyncReportWorker.KEY_CATEGORY)
        assertEquals("KEY_DESCRIPTION", SyncReportWorker.KEY_DESCRIPTION)
        assertEquals("KEY_LOCATION", SyncReportWorker.KEY_LOCATION)
        assertEquals("KEY_DATE", SyncReportWorker.KEY_DATE)
        assertEquals("KEY_TIME", SyncReportWorker.KEY_TIME)
        assertEquals("KEY_TYPE", SyncReportWorker.KEY_TYPE)
        assertEquals("KEY_IMAGE_URL", SyncReportWorker.KEY_IMAGE_URL)
    }

    @Test
    fun reportSyncScheduler_objectExists() {
        assertNotNull(ReportSyncScheduler)
    }
}
