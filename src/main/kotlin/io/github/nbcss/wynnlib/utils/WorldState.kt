package io.github.nbcss.wynnlib.utils

import io.github.nbcss.wynnlib.events.EventHandler
import io.github.nbcss.wynnlib.events.PlayerReceiveChatEvent
import io.github.nbcss.wynnlib.events.WorldStateChangeEvent

enum class WorldState {
    // Big thanks to the Artemis team for the idea.
    NOT_CONNECTED,
    HUB,
    CHARACTER_SELECTION,
    IN_WORLD;

    companion object {
        private var state: WorldState = NOT_CONNECTED

        fun getState(): WorldState {
            return state
        }

        fun setState(state: WorldState) {
            WorldStateChangeEvent.handleEvent(WorldStateChangeEvent(this.state, state))
            this.state = state
        }
    }


    object HandleChat : EventHandler<PlayerReceiveChatEvent> {
        override fun handle(event: PlayerReceiveChatEvent) {
            if (event.message.toString().contains("Welcome to Wynncraft!")) {
                setState(IN_WORLD)
            } else if (event.message.toString().contains("Select a character!")) {
                setState(CHARACTER_SELECTION)
            }

        }

    }
}