package com.ultra.launcher

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

class LauncherLibrary(private val context: Context) {
    private val root = File(context.filesDir, "ultra-library").apply { mkdirs() }
    private val mods = File(root, "mods").apply { mkdirs() }
    private val resourcePacks = File(root, "resourcepacks").apply { mkdirs() }
    private val modpacks = File(root, "modpacks").apply { mkdirs() }

    fun importFile(uri: Uri): File {
        val name = queryName(uri).replace(Regex("[^A-Za-z0-9._ -]"), "_")
        val extension = name.substringAfterLast('.', "").lowercase()
        val destinationDir = when (extension) {
            "jar" -> mods
            "zip" -> resourcePacks
            "mrpack" -> modpacks
            else -> throw IllegalArgumentException("Formato não suportado. Use .jar, .zip ou .mrpack.")
        }
        val safeName = name.ifBlank { "import-${System.currentTimeMillis()}.$extension" }
        val destination = File(destinationDir, safeName)
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Não foi possível ler o arquivo selecionado." }
            destination.outputStream().use { output -> input.copyTo(output) }
        }
        return destination
    }

    fun listImportedFiles(): List<File> =
        (mods.listFiles()?.toList().orEmpty() +
         resourcePacks.listFiles()?.toList().orEmpty() +
         modpacks.listFiles()?.toList().orEmpty()).sortedBy { it.name.lowercase() }

    private fun queryName(uri: Uri): String {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) return cursor.getString(index)
                }
            }
        return "import-${System.currentTimeMillis()}"
    }
}
