package com.FqJvXmR.nKpTzL.domain.usecase

import com.FqJvXmR.nKpTzL.domain.model.ProgressSnapshot
import com.FqJvXmR.nKpTzL.domain.repository.ProgressRepository

class GetProgressUseCase(private val progressRepository: ProgressRepository) {

    operator fun invoke(): ProgressSnapshot = progressRepository.snapshot()
}
