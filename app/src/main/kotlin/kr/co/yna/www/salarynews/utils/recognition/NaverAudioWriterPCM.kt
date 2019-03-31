package kr.co.yna.www.salarynews.utils.recognition

import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class NaverAudioWriterPCM(internal var path: String) {
	private var filename: String = ""
	private var speechFile: FileOutputStream? = null

	fun open(sessionId: String) {
		val directory = File(path)
		if (!directory.exists()) {
			directory.mkdirs()
		}

		filename = "$directory/$sessionId.pcm"
		Log.d("PCM", "filename: $filename")
		try {
			speechFile = FileOutputStream(File(filename))
		} catch (e: FileNotFoundException) {
			System.err.println("Can't open file : $filename")
			speechFile = null
		}
	}

	fun close() {
		speechFile?.run {
			try {
				close()
			} catch (e: IOException) {
				System.err.println("Can't close file: $filename")
			}
		}
	}

	fun write(data: ShortArray) {
		speechFile?.run {
			val buffer = ByteBuffer.allocate(data.size*2)
			buffer.order(ByteOrder.LITTLE_ENDIAN)
			for (i in data.indices) {
				buffer.putShort(data[i])
			}
			buffer.flip()

			try {
				write(buffer.array())
			} catch (e: IOException) {
				System.err.println("Can't write file : $filename")
			}
		}
	}
}
