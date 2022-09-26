package data

sealed class MediaFormat {

    class AudioMediaFormat{
        object MP3 : MediaFormat()
        object AAC : MediaFormat()
    }

    class VideoMediaFormat{
        object MP4 : MediaFormat()
    }
}