package com.example.noodleolympics.enums

/**
 * Enum class representing different game states in Noodle Olympics game.
 */
enum class GameState {
    /**
     * Represents the state when the player's guess is correct.
     */
    CORRECT,

    /**
     * Represents the state when the player decides to skip the current question.
     */
    PASS,

    /**
     * Represents the default state when the player is actively guessing.
     */
    GUESS
}
