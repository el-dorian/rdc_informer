package net.veldor.rdc_informer.model.handler

import net.veldor.rdc_informer.model.exception.TokenException
import net.veldor.rdc_informer.model.exception.WrongDirException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class PreferencesHandler private constructor() {

    private lateinit var mProperties: Properties

    init {
        // инициализирую хранилище настроек
        try {
            val propertiesFile: File = getPropertiesFile()
            //Создаем объект свойст
            mProperties = Properties()
            //Загружаем свойства из файла
            mProperties.load(FileInputStream(propertiesFile))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getPropertiesFile(): File {
        val propertiesFile = File("myProperties")
        if (!propertiesFile.isFile) {
            // если файл настроек ещё не создан-создам его
            if (!propertiesFile.createNewFile()) {
                throw IOException("Не смог создать файл настроек")
            }
        }
        return propertiesFile
    }

    private fun save() {
        try {
            mProperties.store(FileOutputStream(getPropertiesFile()), null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(TokenException::class)
    fun getExternalServerToken(): String {
        return mProperties.getProperty("externalServerToken", null) ?: throw TokenException("Отсутствует токен доступа")
    }

    @Throws(WrongDirException::class)
    fun setDir(dirType: String, selectedDirectory: File?) {
        println("set " + dirType + " on " + selectedDirectory!!.absolutePath)
        if (selectedDirectory.isDirectory && selectedDirectory.exists()) {
            mProperties.setProperty(dirType, selectedDirectory.absolutePath)
            save()
        } else {
            throw WrongDirException()
        }
    }

    fun getDir(dirType: String): File? {
        val path = mProperties.getProperty(dirType, null)
        if (path != null) {
            return File(path)
        }
        return null
    }

    fun getInitialDir(dirType: String): File? {
        return getDir(dirType)
    }

    companion object {
        const val LOCAL_DIR_PATH = "local dir"
        const val NETWORK_DIR_PATH = "network dir"
        val instance: PreferencesHandler = PreferencesHandler()
    }
}