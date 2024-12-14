package com.example.tvshowssearch

data class TVShow(
    val id: Int,
    val name: String,
    val premiered: String,
    val image: Image?,
    var daysSincePremiere: Long = 0L
) {
    data class Image(
        val medium: String
    )
}

