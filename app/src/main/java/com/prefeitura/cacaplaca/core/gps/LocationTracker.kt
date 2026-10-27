package com.prefeitura.cacaplaca.core.gps

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}
