package util

object Constants {

    val FILE_SEPARATOR = System.getProperty("file.separator")
    val OS_NAME = System.getProperty("os.name").lowercase()
    val USER_HOME = System.getProperty("user.home").lowercase()

    object Buttons {
        const val START = "START"
        const val STOP = "STOP"
    }

    object Settings {
        const val PROGRAM_NAME = "Media Downloader"
        const val SAVE_LOCATION = "Save Location"
        const val SETTINGS = "Settings"
        const val SETTINGS_WINDOW_TITLE = SETTINGS
        const val SETTINGS_FILE_NAME = "settings.json"

        object SettingsJSON {
            const val SETTINGS_FILE_NAME = "settings.json"
            val DEFAULT_DOWNLOAD_DIRECTORY = "${USER_HOME}/Documents/${PROGRAM_NAME}"
            val DEFAULT_SETTINGS_DIRECTORY = "${DEFAULT_DOWNLOAD_DIRECTORY}/${SETTINGS}"
            val SETTINGS_FILE_LOCATION = "${USER_HOME}/Documents/${PROGRAM_NAME}/${SETTINGS_FILE_NAME}"
        }
    }

    const val MAIN_WINDOW_TITLE = "Media Downloader"
    const val FILE = "File"
    const val INVALID_FILE_PATH = "Invalid path"
    const val URL_PLACEHOLDER = "Paste URL here"
    const val DOWNLOAD_COMPLETE = "DOWNLOAD COMPLETE"
    const val MEDIA_FORMAT = "Media Format"
    const val VIDEO_MEDIA_FORMAT = "Video"
    const val AUDIO_MEDIA_FORMAT = "Audio"
    const val MP3 = "mp3"
    const val MP4 = "mp4"
    const val AAC = "aac"
    val MEDIA_FORMAT_OPTIONS = listOf(VIDEO_MEDIA_FORMAT, AUDIO_MEDIA_FORMAT)
    val VIDEO_FORMAT_OPTIONS = listOf(MP4)
    val AUDIO_FORMAT_OPTIONS = listOf(MP3, AAC)

}