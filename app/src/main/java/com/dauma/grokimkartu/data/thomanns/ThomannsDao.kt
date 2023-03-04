package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.thomanns.entities.*

interface ThomannsDao {
    suspend fun create(createRequest: CreateThomannRequest, accessToken: String): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus>
    suspend fun update(thomannId: Int, updateRequest: UpdateThomannRequest, accessToken: String): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus>
    suspend fun delete(thomannId: Int, accessToken: String): DaoResult<Nothing?, ThomannsDaoResponseStatus>
    suspend fun thomanns(thomannsRequest: ThomannsRequest, accessToken: String): DaoResult<ThomannsResponse?, ThomannsDaoResponseStatus>
    suspend fun myThomanns(page: Int, pageSize: Int, accessToken: String): DaoResult<ThomannsResponse?, ThomannsDaoResponseStatus>
    suspend fun thomannDetails(thomannId: Int, accessToken: String): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus>
    suspend fun join(thomannId: Int, joinRequest: JoinThomannRequest, accessToken: String): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus>
    suspend fun quit(thomannId: Int, accessToken: String): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus>
    suspend fun kick(thomannId: Int, kickRequest: KickThomannRequest, accessToken: String): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus>
}