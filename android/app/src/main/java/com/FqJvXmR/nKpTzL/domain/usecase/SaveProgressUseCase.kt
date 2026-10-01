package com.FqJvXmR.nKpTzL.domain.usecase

import com.FqJvXmR.nKpTzL.domain.model.RoundResult
import com.FqJvXmR.nKpTzL.domain.repository.ProgressRepository

class SaveProgressUseCase(private val progressRepository: ProgressRepository) {

    operator fun invoke(result: RoundResult) {
        progressRepository.store(result)
    }
}
