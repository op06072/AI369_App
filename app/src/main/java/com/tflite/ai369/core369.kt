package com.tflite.ai369

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.call
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate
import java.io.FileInputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.pow

class core369(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var interpreter2: Interpreter? = null
    private var interpreter3: Interpreter? = null
    var isInitialized = false
        private set

    private var gpuDelegate: GpuDelegate? = null
    private var nnApiDelegate: NnApiDelegate? = null

    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    private var inputImageWidth: Int = 0 // will be inferred from TF Lite model
    private var inputImageHeight: Int = 0 // will be inferred from TF Lite model
    private var modelInputSize: Int = 0 // will be inferred from TF Lite model

    fun initialize(): Task<Void> {
        return call(
            executorService,
            Callable<Void> {
                initializeInterpreter()
                null
            }
        )
    }

    @Throws(IOException::class)
    private fun initializeInterpreter() {
        // Load the TF Lite model
        val assetManager = context.assets
        val model = loadModelFile(assetManager)
        val model2 = loadModelFile2(assetManager)
        val model3 = loadModelFile3(assetManager)

        // Initialize TF Lite Interpreter with NNAPI enabled
        var options = Interpreter.Options()
        //options.setUseNNAPI(true)
        try{
            gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
            val interpreterGPU = Interpreter(model, options)
            val interpreterGPU2 = Interpreter(model2, options)
            val interpreterGPU3 = Interpreter(model3, options)
            // Finish interpreter initialization
            this.interpreter = interpreterGPU
            this.interpreter2 = interpreterGPU2
            this.interpreter3 = interpreterGPU3
            // Read input shape from model file
            val inputShape = interpreterGPU.getInputTensor(0).shape()
            inputImageWidth = inputShape[1]
            inputImageHeight = inputShape[2]
        } catch (e: IllegalArgumentException){
            nnApiDelegate = NnApiDelegate()
            options = Interpreter.Options()
            options.addDelegate(nnApiDelegate)
            val interpreter = Interpreter(model, options)
            val interpreter2 = Interpreter(model2, options)
            val interpreter3 = Interpreter(model3, options)
            // Finish interpreter initialization
            this.interpreter = interpreter
            this.interpreter2 = interpreter2
            this.interpreter3 = interpreter3
            // Read input shape from model file
            val inputShape = interpreter.getInputTensor(0).shape()
            inputImageWidth = inputShape[1]
            inputImageHeight = inputShape[2]
        }

        modelInputSize = FLOAT_TYPE_SIZE * inputImageWidth * inputImageHeight * PIXEL_SIZE

        isInitialized = true
        //Log.d(TAG, "Initialized TFLite interpreter.")
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Throws(IOException::class)
    private fun loadModelFile2(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd(MODEL_FILE2)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Throws(IOException::class)
    private fun loadModelFile3(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd(MODEL_FILE3)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun classify(int: Int): String {
        if (!isInitialized) {
            throw IllegalStateException("TF Lite Interpreter is not initialized yet.")
        }

        //var startTime: Long
        //var elapsedTime: Long

        // Preprocessing: resize the input
        //startTime = System.nanoTime()
        val byteBuffer = binaryEncode(intArrayOf(int))
        //elapsedTime = (System.nanoTime() - startTime) / 1000000
        //Log.d(TAG, "Preprocessing time = " + elapsedTime + "ms")

        //startTime = System.nanoTime()
        val result = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        interpreter?.run(byteBuffer, result)
        //elapsedTime = (System.nanoTime() - startTime) / 1000000
        //Log.d(TAG, "Inference time = " + elapsedTime + "ms")

        var output = getOutputString(result[0])
        if (output == "0") output = int.toString()

        return "AI1 : " + output
    }

    private fun classify2(int: Int): String {
        if (!isInitialized) {
            throw IllegalStateException("TF Lite Interpreter is not initialized yet.")
        }

        //var startTime: Long
        //var elapsedTime: Long

        // Preprocessing: resize the input
        //startTime = System.nanoTime()
        val byteBuffer = binaryEncode(intArrayOf(int))
        //elapsedTime = (System.nanoTime() - startTime) / 1000000
        //Log.d(TAG2, "Preprocessing time = " + elapsedTime + "ms")

        //startTime = System.nanoTime()
        val result = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        interpreter2?.run(byteBuffer, result)
        //elapsedTime = (System.nanoTime() - startTime) / 1000000
        //Log.d(TAG2, "Inference time = " + elapsedTime + "ms")

        var output = getOutputString(result[0])
        if (output == "0") output = int.toString()

        return "AI2 : " + output
    }

    private fun classify3(int: Int): String {
        if (!isInitialized) {
            throw IllegalStateException("TF Lite Interpreter is not initialized yet.")
        }

        //var startTime: Long
        //var elapsedTime: Long

        // Preprocessing: resize the input
        //startTime = System.nanoTime()
        val byteBuffer = binaryEncode(intArrayOf(int))
        //elapsedTime = (System.nanoTime() - startTime) / 1000000
        //Log.d(TAG3, "Preprocessing time = " + elapsedTime + "ms")

        //startTime = System.nanoTime()
        val result = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        interpreter3?.run(byteBuffer, result)
        //elapsedTime = (System.nanoTime() - startTime) / 1000000
        //Log.d(TAG3, "Inference time = " + elapsedTime + "ms")

        var output = getOutputString(result[0])
        if (output == "0") output = int.toString()

        return "AI3 : " + output
    }

    fun classifyAsync(int: Int, turn: Int): Task<String>? {
        var result : Task<String>? = null
        when (turn) {
            1 -> result = call(executorService, Callable<String> { classify(int) })
            2 -> result = call(executorService, Callable<String> { classify2(int) })
            3 -> result = call(executorService, Callable<String> { classify3(int) })
        }
        return result
    }

    fun benchAll(int: Int): Task<Array<String>> {
        val result1 = classify(int)
        val result2 = classify2(int)
        val result3 = classify3(int)

        return call(executorService, Callable { arrayOf(result1, result2, result3) })
    }

    fun closeAll() {
        if (gpuDelegate != null) {
            gpuDelegate!!.close()
        }
        if (nnApiDelegate != null) {
            nnApiDelegate!!.close()
        }
        close()
        close2()
        close3()
    }

    fun close() {
        call(
            executorService,
            Callable<String> {
                interpreter?.close()
                //Log.d(TAG, "Closed TFLite interpreter.")
                null
            }
        )
    }

    fun close2() {
        call(
            executorService,
            Callable<String> {
                interpreter2?.close()
                //Log.d(TAG2, "Closed TFLite interpreter.")
                null
            }
        )
    }

    fun close3() {
        call(
            executorService,
            Callable<String> {
                interpreter3?.close()
                //Log.d(TAG3, "Closed TFLite interpreter.")
                null
            }
        )
    }

    private fun getOutputString(output: FloatArray): String {
        val maxIndex = output.indices.maxBy { output[it] } ?: -1
        var result = ""
        when (maxIndex) {
            0 -> result = "0"
            1 -> result = "X"
            2 -> result = "XX"
            3 -> result = "XXX"
        }
        return result
    }

    private fun binaryEncode(nums: IntArray): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val l = 4
        val ten = 10.0

        val Nums = Array(nums.size,{ Array(l,{ _ -> 0 }) })
        for (i in 0..nums.size-1)
            for (j in 1..l)
                Nums[i][l-j] = ((nums[i] % ten.pow(j).toInt()) / ten.pow(j-1).toInt()).toString().toCharArray()[0].toInt()
        val binary = Array(nums.size, {Array(l, {Array(7, { _ -> 0.0f })})})
        for (i in 0..nums.size-1) for (j in 0..l-1) for ( d in 0..6) {
            binary[i][j][d] = (Nums[i][j] shr d and 1).toFloat()
            byteBuffer.putFloat(binary[i][j][d])
        }
        return byteBuffer
    }

    companion object {
        private const val TAG = "369Core1"
        private const val TAG2 = "369Core2"
        private const val TAG3 = "369Core3"

        private const val MODEL_FILE = "369_10d_300e_300b.tflite"
        private const val MODEL_FILE2 = "369_10d_300e_400b-2.tflite"
        private const val MODEL_FILE3 = "cp.tflite"

        private const val FLOAT_TYPE_SIZE = 4
        private const val PIXEL_SIZE = 1

        private const val OUTPUT_CLASSES_COUNT = 4
    }

}