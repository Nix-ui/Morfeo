
package com.ucb.morfeo.features.time.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TimeResponse(
    val unixtime: Long
)
