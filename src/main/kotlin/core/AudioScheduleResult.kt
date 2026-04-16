package core

sealed interface AudioScheduleResult {

    data class TrackAdded(val trackName: String) : AudioScheduleResult
    data class TrackPlaying(val trackName: String) : AudioScheduleResult
    data class TrackNotFound(val keyword: String): AudioScheduleResult
    data class PlaylistAdded(val playlistName: String) : AudioScheduleResult
    data class PlaylistPlaying(val trackName: String, val playlistName: String) : AudioScheduleResult
    data object NoMatches : AudioScheduleResult
    data class Error(val exception: Exception) : AudioScheduleResult
    data class EmptyPlaylist(val playlistName: String) : AudioScheduleResult
}