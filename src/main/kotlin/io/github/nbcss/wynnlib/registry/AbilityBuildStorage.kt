package io.github.nbcss.wynnlib.registry

import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.builder.TreeBuildData

object AbilityBuildStorage : SavingStorage<TreeBuildData>() {
    private const val PATH = "config/WynnLib/AbilityBuilds.json"

    override fun read(data: JsonObject): TreeBuildData? = TreeBuildData.fromData(data)

    override fun getKey(): String = "ABILITY_BUILD"

    override fun getData(item: TreeBuildData): JsonObject = item.getData()

    override fun getSavePath(): String = PATH
}