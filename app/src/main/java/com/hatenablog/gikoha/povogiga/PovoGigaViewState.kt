package com.hatenablog.gikoha.povogiga

data class PovoGigaViewState
    (
    val items: List<PovoGiga>?
)
{
    companion object
    {
        val EMPTY = PovoGigaViewState(null)
    }
}
