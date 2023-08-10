package io.github.nbcss.wynnlib.events

import io.github.nbcss.wynnlib.utils.WorldState

class WorldStateChangeEvent(val oldState: WorldState, val newState: WorldState) {
    companion object : EventHandler.HandlerList<WorldStateChangeEvent>()
}