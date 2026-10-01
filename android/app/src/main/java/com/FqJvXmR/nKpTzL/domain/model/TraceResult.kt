package com.FqJvXmR.nKpTzL.domain.model

data class TraceResult(
    val traces: List<BeamTrace>,
    val conflicts: Set<Int>,
    val linked: Int,
    val total: Int
) {
    val allLinked: Boolean
        get() = total > 0 && linked == total
}
