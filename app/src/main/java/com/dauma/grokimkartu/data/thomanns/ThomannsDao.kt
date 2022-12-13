package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.thomanns.entities.*

interface ThomannsDao {
    fun create(createRequest: CreateThomannRequest, accessToken: String, onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun update(thomannId: Int, updateRequest: UpdateThomannRequest, accessToken: String, onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun delete(thomannId: Int, accessToken: String, onComplete: (ThomannsDaoResponseStatus) -> Unit)
    fun thomanns(thomannsRequest: ThomannsRequest, accessToken: String, onComplete: (ThomannsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun myThomanns(page: Int, pageSize: Int, accessToken: String, onComplete: (ThomannsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun thomannDetails(thomannId: Int, accessToken: String, onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun join(thomannId: Int, joinRequest: JoinThomannRequest, accessToken: String, onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun quit(thomannId: Int, accessToken: String, onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit)
    fun kick(thomannId: Int, kickRequest: KickThomannRequest, accessToken: String, onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit)
}