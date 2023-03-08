package com.dauma.grokimkartu.repositories.thomanns

import com.dauma.grokimkartu.repositories.thomanns.entities.*
import com.dauma.grokimkartu.repositories.thomanns.paginator.ThomannsPaginator
import com.dauma.grokimkartu.repositories.Result

interface MyThomannsRepository {
    val paginator: ThomannsPaginator
    suspend fun thomannDetails(thomannId: Int): Result<ThomannDetails?, ThomannsErrors?>
    suspend fun reload(): Result<ThomannsPage?, ThomannsErrors?>
}