
package com.ucb.helpet.features.time.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TimeResponse(
    val unixtime: Long
)
