package com.example.gamestore.data.mapper

import com.example.gamestore.data.dto.RawGame
import com.example.gamestore.data.mapper.RawGenreMapper.fromDto as mapGenre
import com.example.gamestore.data.model.Game
import kotlin.String

object RawGameMapper {

    fun fromDto(dto: RawGame): Game {
        return Game(
            id = dto.id,
            slug = dto.slug,
            title = dto.title,
            releaseDate = dto.releaseDate,
            imageUrl = dto.imageUrl,
            averageRating = dto.averageRating,
            topRating = dto.topRating,
            totalRatings = dto.totalRatings,
            metacriticScore = dto.metacriticScore,
            estimatedPlaytime = dto.estimatedPlaytime,
            platforms = dto.platforms?.map { it.platform.name } ?: emptyList(),
            developers = dto.developers?.map { it.name } ?: emptyList(),
            publishers = dto.publishers?.map { it.name } ?: emptyList(),
            stores = dto.stores?.map { it.store.name } ?: emptyList(),
            storesDomain = dto.stores?.map { it.store.domain } ?: emptyList(),
            description = dto.description,
            genres = dto.genres.map { mapGenre(it) }  ?: emptyList() // FIX
        )
    }
}