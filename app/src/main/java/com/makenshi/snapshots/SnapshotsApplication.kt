package com.makenshi.snapshots

import android.app.Application

class SnapshotsApplication : Application() {
    companion object {
        const val PATH_SNAPSHOTS = "snapshots"
        const val PROPERTY_LIKE_LIST = "likeList"
    }
}