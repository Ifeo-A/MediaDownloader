package Util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class CommandUtil {

    fun getOutputFromCommand(process: Process): Flow<String> = flow {

        val stdInput = BufferedReader(InputStreamReader(process.inputStream))
        var outputFromCommand: String

        try {
            while (true) {
                outputFromCommand = stdInput.readLine() ?: break
                println("outputFromCommand: $outputFromCommand")
                emit(outputFromCommand)
            }
            process.destroy()
        } catch (e: IOException) {
            println("ERROR OCCURRED: ${e.printStackTrace()}")
        }
    }
        .flowOn(Dispatchers.IO)

}