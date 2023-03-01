package com.dauma.grokimkartu.data.instruments

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse

interface InstrumentsDao {
    suspend fun instruments(accessToken: String): DaoResult<List<InstrumentResponse>?, InstrumentsDaoResponseStatus>
    suspend fun search(value: String, accessToken: String): DaoResult<List<InstrumentResponse>?, InstrumentsDaoResponseStatus>
}