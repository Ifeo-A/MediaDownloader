import data.CommandOptions
import data.DownloadProperties
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import util.DownloadUtil
import java.io.File


class ViewModel() {
    private var downloadJob: Job? = null
    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)
    private var downloadProcess: Process? = null

    private var _downloadProperties: Channel<DownloadProperties> = Channel()
    var downloadProperties: Flow<DownloadProperties> = _downloadProperties.receiveAsFlow()

    fun startDownload(commandOptions: CommandOptions) {

        when (downloadLocationExistsOrCreate(commandOptions.downloadFolder)) {
            FileCreatedResult.FileCreated,
            FileCreatedResult.FileAlreadyExists -> {
                if (commandOptions.url.isNotEmpty()) {
                    downloadJob = myCoroutineScope.launch {
                        println("Starting download")
                        println("data.CommandOptions: $commandOptions")

                        println("BUILT COMMAND")
                        println("------------------------------------------------------")
                        commandOptions.builtCommand().toTypedArray().mapIndexed { index, string ->
                            if (index == commandOptions.builtCommand().size - 1) {
                                print("\"$string\"")
                                println()
                            } else {
                                print("\"$string\", ")
                            }
                        }
                        println("------------------------------------------------------")

                        DownloadUtil.startDownload(
                            downloadFolder = commandOptions.downloadFolder,
                            builtCommand = mutableListOf(*commandOptions.builtCommand().toTypedArray())
                        ).collect { downloadProperties ->
                            _downloadProperties.send(downloadProperties)

                            if (downloadProcess == null) {
                                downloadProcess = downloadProperties.process
                            }
                        }
                    }

                } else {
                    println("Nothing to download")
                }
            }
            is FileCreatedResult.SecurityException -> {}
        }
    }

    sealed class FileCreatedResult {
        object FileCreated : FileCreatedResult()
        object FileAlreadyExists : FileCreatedResult()
        class SecurityException(message: String) : FileCreatedResult()
    }

    private fun downloadLocationExistsOrCreate(downloadFolder: String): FileCreatedResult {

        return try {
            if (File(downloadFolder).mkdir()) {
                FileCreatedResult.FileCreated
            } else {
                FileCreatedResult.FileAlreadyExists
            }

        } catch (e: SecurityException) {
            println(e.printStackTrace())
            FileCreatedResult.SecurityException(e.localizedMessage)
        }
    }

    fun stopDownload() {
        downloadProcess?.let { DownloadUtil.stopDownload(it) }
        downloadProcess?.destroy() ?: return
        downloadJob?.cancelChildren() ?: return

//        downloadUtil.deleteUnfinishedDownloadedFile(
//            "$USER_HOME/Documents/mediaDownloader"
//        )
    }

}