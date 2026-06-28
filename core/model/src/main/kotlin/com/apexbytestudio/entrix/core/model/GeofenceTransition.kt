package com.apexbytestudio.entrix.core.model

/**
 * The geofence transitions Entrix reacts to.
 *
 * [Enter] is used only to *cache* the precise boundary-crossing time; a visit is not opened
 * until [Dwell] confirms the user actually stayed (filtering out drive-bys and GPS jitter at
 * the boundary). [Exit] closes the open visit.
 */
enum class GeofenceTransition {
    Enter,
    Dwell,
    Exit,
}
