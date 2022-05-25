package com.dauma.grokimkartu.data.instruments

import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse

interface InstrumentsDao {
    fun instruments(accessToken: String, onComplete: (List<InstrumentResponse>?, InstrumentsDaoResponseStatus) -> Unit)
    fun search(value: String, accessToken: String, onComplete: (List<InstrumentResponse>?, InstrumentsDaoResponseStatus) -> Unit)
}