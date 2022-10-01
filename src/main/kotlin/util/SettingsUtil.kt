package util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import components.SettingsOptions
import util.Constants.USER_HOME
import java.io.File
import java.io.FileReader

object SettingsUtil {

    fun initDefaultDirectories(){
        //create download directory
        val defaultDownloadDirectory = File("$USER_HOME/Documents/mediaDownloader")
        if(defaultDownloadDirectory.mkdir()){
            println("Default download directory created: $defaultDownloadDirectory")
        } else {
            println("Default download directory not created: $defaultDownloadDirectory")
        }

        //create settings file in /Documents/mediaDownloader
        if(defaultDownloadDirectory.exists()){
            val defaultSettingsDirectory= File("$USER_HOME/Documents/mediaDownloader/settings")
            if(defaultSettingsDirectory.mkdir()){
                val settingsOptions = SettingsOptions(
                    downloadLocation = defaultDownloadDirectory.toString(),
                    settingsFileLocation = defaultSettingsDirectory.toString()
                )
                saveSettings(defaultSettingsDirectory, settingsOptions)
            } else {
                println("Default settings directory not created: $defaultSettingsDirectory")
            }
        } else {
            println("Default download directory does not exist")
        }
    }

    fun saveSettings(settingsDirectory: File, settingsOptions: SettingsOptions) {

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val settingsContentValueJson = gsonPretty.toJson(settingsOptions, SettingsOptions::class.java)

        println("Settings content: $settingsContentValueJson")
        println("Settings directory in saveSettings: $settingsDirectory")

        if(settingsDirectory.exists()){
            val settingsFile = File("${settingsDirectory}/settings.json")
            if(settingsFile.createNewFile()){
                println("Created settings file")
                settingsFile.writeText(settingsContentValueJson)
            } else {
                println("Could not create settings file maybe because it exists already")
            }
        }
    }

    fun readDownloadLocation(): String? {
        val defaultSettingsDirectory = File("$USER_HOME/Documents/mediaDownloader/settings")
        var downloadLocation: String? =  "$USER_HOME/Documents/mediaDownloader"
        var settingsFile: File? = null

        if (defaultSettingsDirectory.exists()) {
            println("Settings directory already exists: $defaultSettingsDirectory")
            settingsFile = File("${defaultSettingsDirectory}/settings.json")
            println("Settings File: $settingsFile")
            if(settingsFile.createNewFile()){
                val gson = Gson()
                val jsonReader = JsonReader(FileReader(settingsFile))
                val settingsOptions: SettingsOptions = gson.fromJson(jsonReader, SettingsOptions::class.java)
                downloadLocation = settingsOptions.downloadLocation
            } else {
                println("settingsFile: $settingsFile already exists")
                val gson = Gson()
                val jsonReader = JsonReader(FileReader(settingsFile))
                val settingsOptions: SettingsOptions = gson.fromJson(jsonReader, SettingsOptions::class.java)
                downloadLocation = settingsOptions.downloadLocation
            }
        } else {
            println("Settings directory does not exist = Create one")
        }
        return downloadLocation
    }

    fun updateDownloadDirectory(downloadDirectory: String) {

        val settingsDirectory = File("$USER_HOME/Documents/mediaDownloader/settings")
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

