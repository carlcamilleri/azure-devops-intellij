package com.microsoft.tfs

import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ChangeType
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet
import com.microsoft.tfs.model.host.TfsLocalPath
import com.microsoft.tfs.model.host.TfsPendingChange
import com.microsoft.tfs.model.host.TfsServerStatusType
import java.nio.file.Paths
import java.text.SimpleDateFormat

private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

private val pendingChangeTypeMap = mapOf(
    ChangeType.ADD to TfsServerStatusType.ADD,
    ChangeType.EDIT to TfsServerStatusType.EDIT,
    ChangeType.ENCODING to TfsServerStatusType.UNKNOWN,
    ChangeType.RENAME to TfsServerStatusType.RENAME,
    ChangeType.DELETE to TfsServerStatusType.DELETE,
    ChangeType.UNDELETE to TfsServerStatusType.UNDELETE,
    ChangeType.BRANCH to TfsServerStatusType.BRANCH,
    ChangeType.MERGE to TfsServerStatusType.MERGE,
    ChangeType.LOCK to TfsServerStatusType.LOCK,
    ChangeType.ROLLBACK to TfsServerStatusType.UNKNOWN,
    ChangeType.SOURCE_RENAME to TfsServerStatusType.RENAME,
    ChangeType.TARGET_RENAME to TfsServerStatusType.UNKNOWN,
    ChangeType.PROPERTY to TfsServerStatusType.EDIT
)

private fun extractChangeTypes(changeType: ChangeType): List<TfsServerStatusType> =
    pendingChangeTypeMap.entries.mapNotNull { (k, v) -> if (changeType.contains(k)) v else null }

private fun mapPendingChange(pendingSet: PendingSet, pc: PendingChange) = TfsPendingChange(
    pc.serverItem,
    pc.localItem,
    pc.version,
    pc.pendingSetOwner,
    isoDateFormat.format(pc.creationDate.time),
    pc.lockLevelName,
    extractChangeTypes(pc.changeType),
    pendingSet.name,
    pendingSet.computer,
    pc.isCandidate,
    pc.sourceServerItem
)

fun toTfsPendingChanges(pendingSet: PendingSet): Iterable<TfsPendingChange> =
    (pendingSet.pendingChanges.asSequence().map { mapPendingChange(pendingSet, it) }
            + pendingSet.candidatePendingChanges.map { mapPendingChange(pendingSet, it) }).asIterable()

fun TfsLocalPath.toPath() = Paths.get(path)