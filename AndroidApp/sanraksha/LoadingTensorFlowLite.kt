package com.example.sanraksha

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class RiskPredictor(private val context : Context){

    private  var interpreter : Interpreter? = null

    init{
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("finalmodel.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(inputData : FloatArray):Float {
        val input = arrayOf(inputData) //shape:[1][7]
        val output = Array(1){FloatArray(1)} // shape : [1][1]

        interpreter?.run(input,output)

        return output [0][0]
    }





}