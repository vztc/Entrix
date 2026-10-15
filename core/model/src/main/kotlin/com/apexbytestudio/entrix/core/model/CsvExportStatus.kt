package com.apexbytestudio.entrix.core.model

data class CsvExportStatus(
    val documentUri: String? = null,
    val fileName: String? = null,
    val lastWrittenMillis: Long? = null,
    val lastDigest: String? = null,
    val revision: Long = 0,
    val writtenRevision: Long = -1,
    val error: String? = null,
) {
    val isLinked: Boolean get() = documentUri != null
    val isPending: Boolean get() = isLinked && revision > writtenRevision
}
