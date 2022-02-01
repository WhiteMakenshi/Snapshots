package com.makenshi.snapshots

import com.google.firebase.database.Exclude

data class Snapshot(@get:Exclude var id: String = "",
                    var title: String = "",
                    var photoUrl: String = "",
                    var likeList: Map<String, Boolean> = mutableMapOf())
