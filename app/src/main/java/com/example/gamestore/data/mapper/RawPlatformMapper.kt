package com.example.gamestore.data.mapper


import com.example.gamestore.data.dto.RawPlatform
import com.example.gamestore.data.model.Platform

object RawPlatformMapper {

    fun fromDto(dto: RawPlatform): Platform {
        return Platform(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            totalGames = dto.totalGames,
            backgroundImage = dto.backgroundImage ?: "",
            associatedGames = dto.associatedGames?.map { RawGameMapper.fromDto(it) }
        )
    }
}

