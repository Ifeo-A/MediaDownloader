package util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import components.SettingsOptions
import util.Constants.Settings.SettingsJSON.DEFAULT_DOWNLOAD_DIRECTORY
import util.Constants.Settings.SettingsJSON.DEFAULT_SETTINGS_DIRECTORY
import util.Constants.Settings.SettingsJSON.SETTINGS_FILE_NAME
import java.io.File
import java.io.FileReader

object SettingsUtil {

    fun initDefaultDirectories() {
        //create download directory
        val defaultDownloadDirectory = File(DEFAULT_DOWNLOAD_DIRECTORY)
        if (defaultDownloadDirectory.mkdir()) {
            println("Default download directory created: $defaultDownloadDirectory")
        } else {
            println("Default download directory not created: $defaultDownloadDirectory")
        }

        //create settings file in /Documents/mediaDownloader
        if (defaultDownloadDirectory.exists()) {
            val defaultSettingsDirectory = File(DEFAULT_SETTINGS_DIRECTORY)
            if (defaultSettingsDirectory.mkdir()) {
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

    private fun saveSettings(settingsDirectory: File, settingsOptions: SettingsOptions) {

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val settingsContentValueJson = gsonPretty.toJson(settingsOptions, SettingsOptions::class.java)

        println("Settings content: $settingsContentValueJson")
        println("Settings directory in saveSettings: $settingsDirectory")

        if (settingsDirectory.exists()) {
            println("Settings directory exists")

            val settingsFile = File("${DEFAULT_SETTINGS_DIRECTORY}/${SETTINGS_FILE_NAME}")

            if (settingsFile.createNewFile()) {
                println("Created settings file")
                settingsFile.writeText(settingsContentValueJson)
            } else if (settingsFile.isFile) {
                settingsFile.writeText(settingsContentValueJson)
                println("Settings file already exists")
            } else {
                println("Could not create settings file maybe because it exists already")
            }
        }
    }

    private fun createFolderIfNotExists(folder: File): Boolean {
        var canUseFolder = false

        if (!folder.exists()) {
            try {
                folder.mkdir()
                canUseFolder = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            // Folder exists and can be used
            canUseFolder = true
        }

        return canUseFolder
    }

    private fun createFileIfNotExist(folder: File): Boolean {
        var canUseFolder = false

        if (!folder.exists()) {
            try {
                folder.mkdir()
                canUseFolder = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            // Folder exists and can be used
            canUseFolder = true
        }

        return canUseFolder
    }

    fun readDownloadLocationFromSettingsFile(): String? {
        val defaultSettingsDirectory = File(DEFAULT_SETTINGS_DIRECTORY)
        var downloadLocation: String? = DEFAULT_DOWNLOAD_DIRECTORY
        val settingsFile: File = File("${DEFAULT_SETTINGS_DIRECTORY}/${SETTINGS_FILE_NAME}")
        val gson = Gson()

        if (defaultSettingsDirectory.exists()) {
            println("Settings directory already exists: $defaultSettingsDirectory")
            println("Settings File: $settingsFile")

            downloadLocation = if (settingsFile.createNewFile()) {
                val jsonReader = JsonReader(FileReader(settingsFile))
                val settingsOptions: SettingsOptions = gson.fromJson(jsonReader, SettingsOptions::class.java)
                settingsOptions.downloadLocation
            } else {
                // Settings file already exists so read the download location from it
                println("settingsFile: $settingsFile already exists")
                val jsonReader = JsonReader(FileReader(settingsFile))
                val settingsOptions: SettingsOptions = gson.fromJson(jsonReader, SettingsOptions::class.java)
                settingsOptions.downloadLocation
            }
        } else {
            println("Settings directory does not exist = Create one")
        }
        return downloadLocation
    }

    fun updateDownloadDirectory(downloadDirectory: String) {

        println("Update Download directory to ${downloadDirectory}")
        val settingsDirectory = File(DEFAULT_SETTINGS_DIRECTORY)
        if (settingsDirectory.exists()) {
            //todo if settings exists get the SettingsOptions from it

            val newSettingsOptions = SettingsOptions(downloadLocation = downloadDirectory)
            saveSettings(File(downloadDirectory), newSettingsOptions)
        }
    }
}

