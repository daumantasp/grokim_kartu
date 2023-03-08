package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.*
import com.dauma.grokimkartu.repositories.thomanns.paginator.ThomannsPaginator
import com.dauma.grokimkartu.repositories.Result

interface ThomannsRepository {
    val paginator: ThomannsPaginator
    suspend fun create(createThomann: CreateThomann): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun update(thomannId: Int, updateThomann: UpdateThomann): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun delete(thomannId: Int): Result<Nothing?, ThomannsErrors?>
    suspend fun thomannDetails(thomannId: Int): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun join(thomannId: Int, amount: Double): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun quit(thomannId: Int): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun kick(thomannId: Int, userToKickId: Int): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun cities(): Result<List<ThomannCity>?, ThomannsErrors?>
    suspend fun searchCity(value: String): Result<List<ThomannCity>?, ThomannsErrors?>
    suspend fun reload(): Result<ThomannsPage?, ThomannsErrors?>
}