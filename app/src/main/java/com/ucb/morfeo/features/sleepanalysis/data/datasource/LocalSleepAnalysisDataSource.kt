package com.ucb.morfeo.features.sleepanalysis.data.datasource

import com.ucb.morfeo.features.core.database.dao.AudioAnalysisDao
import com.ucb.morfeo.features.core.database.dao.SleepDao

class LocalSleepAnalysisDataSource(
    private val sleepDao: SleepDao,
    private val audioAnalysisDao: AudioAnalysisDao
) {
}