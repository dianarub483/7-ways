package com.FqJvXmR.nKpTzL.domain.model

enum class CellPiece {
    EMPTY,
    PRISM_SLASH,
    PRISM_BACKSLASH,
    WALL;

    val isPrism: Boolean
        get() = this == PRISM_SLASH || this == PRISM_BACKSLASH

    fun flipped(): CellPiece = when (this) {
        PRISM_SLASH -> PRISM_BACKSLASH
        PRISM_BACKSLASH -> PRISM_SLASH
        else -> this
    }

    fun deflect(incoming: Direction): Direction = when (this) {
        PRISM_SLASH -> incoming.throughSlash()
        PRISM_BACKSLASH -> incoming.throughBackslash()
        else -> incoming
    }
}
