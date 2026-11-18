package com.example.practica_12.data

data class NamedResource(val name: String, val url: String) {
    val id: String get() = url.trimEnd('/').substringAfterLast('/')
}

data class TypeListResponse(val results: List<NamedResource>)
data class TypeDetailResponse(val pokemon: List<PokemonSlot>)
data class PokemonSlot(val pokemon: NamedResource)

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val stats: List<StatSlot>,
    val types: List<TypeSlot>
)
data class StatSlot(val base_stat: Int, val stat: NamedResource)
data class TypeSlot(val type: NamedResource)
