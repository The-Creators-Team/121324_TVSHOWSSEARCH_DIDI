package com.example.tvshowssearch.data.model

data class TVShow(
    val id: Int,
    val name: String,
    val premiered: String,
    val image: Image?,
    val url: String,
    var daysSincePremiere: Long = 0L
) {
    data class Image(
        val medium: String
    )
}

