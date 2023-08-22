package com.example.noodleolympics.util

/**
 * Utility class for time-related operations.
 */
class TimeUtil {

    companion object {
        /**
         * Converts milliseconds to minutes.
         *
         * @param milliseconds The duration in milliseconds.
         * @return The number of minutes in the given duration.
         */
        fun convertMillisecondsToMinute(milliseconds: Long): Long {
            return (milliseconds / (1000 * 60)) % 60
        }

        /**
         * Converts milliseconds to seconds.
         *
         * @param milliseconds The duration in milliseconds.
         * @return The number of seconds in the given duration.
         */
        fun convertMillisecondsToSeconds(milliseconds: Long): Long {
            return (milliseconds / 1000) % 60
        }
    }
}
