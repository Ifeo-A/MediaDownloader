import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import util.DownloadUtil


class ViewModel() {
    private var downloadJob: Job? = null
    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)
    private var downloadProcess: Process? = null

    private var _downloadProperties: Channel<DownloadProperties> = Channel()
    var downloadProperties: Flow<DownloadProperties> = _downloadProperties.receiveAsFlow()

    fun startDownload(commandOptions: CommandOptions) {
        if(commandOptions.url.isNotEmpty()){
            downloadJob = myCoroutineScope.launch {
                println("Starting download")
                DownloadUtil.startDownload(
                    downloadFolder = "/Users/ife/Documents/mediaDownloader",
                    builtCommand = mutableListOf("youtube-dl", "--format", commandOptions.format, commandOptions.url)
//                builtCommand = mutableListOf("youtube-dl", "--format", "mp4", "https://www.youtube.com/watch?v=wPfn8GrR3ic&t=20s")
                ).collect { downloadProperties ->
                    _downloadProperties.send(downloadProperties)

                    if(downloadProcess == null){
                        downloadProcess = downloadProperties.process
                    }
                }
            }
        } else {
            println("Nothing to download")
        }
    }

    fun stopDownload(){
        downloadProcess?.let { DownloadUtil.stopDownload(it) }
        downloadProcess?.destroy() ?: return
        downloadJob?.cancelChildren() ?: return

//        downloadUtil.deleteUnfinishedDownloadedFile(
//            "/Users/ife/Documents/mediaDownloader"
//        )
    }

}