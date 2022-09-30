package util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import components.SettingsOptions
import util.Constants.USER_HOME
import java.io.File
import java.io.FileReader
import java.io.IOException

object SettingsUtil {

    fun initDefaultDirectories(){
        //create download location
        val defaultDownloadDirectory = File("$USER_HOME/Downloads/mediaDownloader")
        if(defaultDownloadDirectory.mkdir()){
            println("Default download directory created: $defaultDownloadDirectory")
        } else {
            println("Default download directory not created: $defaultDownloadDirectory")
        }

        //create settings file
        if(defaultDownloadDirectory.exists()){
            val defaultSettingsDirectory= File("$USER_HOME/Downloads/mediaDownloader/settings")
            if(defaultSettingsDirectory.mkdir()){
                println("Default settings directory created: $defaultSettingsDirectory")
                val settingsFile = File("${defaultSettingsDirectory}/settings.json")
                if(settingsFile.createNewFile()){
                    println("Settings file created: $settingsFile")
                } else {
                    println("Settings file not created: $settingsFile")
                }

            } else {
                println("Default settings directory not created: $defaultSettingsDirectory")
            }
        } else {
            println("Default download directory does not exist")
        }
    }

    fun saveSettings(settingsDirectory: File, content: SettingsOptions) {

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val settingsContentValueJson = gsonPretty.toJson(content, SettingsOptions::class.java)

        println("Settings content: ${settingsContentValueJson.toString()}")

        println("Settings directory in saveSettings: $settingsDirectory")

        if(settingsDirectory.exists()){
            val settingsFile = File("${settingsDirectory}/settings.json")
            if(settingsFile.createNewFile()){
                println("Created settings file")
                settingsFile.writeText(settingsContentValueJson)
            } else {
                println("Could not create settings file")
            }
        }
    }

    fun readDownloadLocation(): String? {
        val settingsDirectory = File("$USER_HOME/Downloads/mediaDownloader/settings")
        var downloadLocation: String? = null

        if (settingsDirectory.exists()) {
            val settingsFile = File("${settingsDirectory}/settings.json")
            if(settingsFile.createNewFile()){
                val gson = Gson()
                val jsonReader = JsonReader(FileReader(settingsFile))
                val settingsOptions: SettingsOptions = gson.fromJson(jsonReader, SettingsOptions::class.java)
                downloadLocation = settingsOptions.downloadLocation
            }
        }
        return downloadLocation
    }

    fun updateDownloadDirectory(downloadDirectory: String) {

        val settingsDirectory = File("$USER_HOME/Downloads/mediaDownloader/settings")
        if (settingsDirectory.exists()) {
            //todo if settings exists get the SettingsOptions from it

            val newSettingsOptions = SettingsOptions(downloadLocation = downloadDirectory)
            saveSettings(File(downloadDirectory), newSettingsOptions)
        }
    }

//    fun readSettingsFile(settingsFile: File): SettingsOptions? {
//        val gson = Gson()
//        val jsonReader = JsonReader(FileReader(settingsFile))
//        return gson.fromJson(jsonReader, SettingsOptions::class.java)
//    }
}

