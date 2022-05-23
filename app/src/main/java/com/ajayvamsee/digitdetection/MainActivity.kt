package com.ajayvamsee.digitdetection

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajayvamsee.digitdetection.classifier.Classifier
import com.ajayvamsee.digitdetection.classifier.Recognition
import com.ajayvamsee.digitdetection.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var classifier: Classifier

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        initClassifier()
        initView()
    }

    private fun initClassifier() {
        try {
            classifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, R.string.failed_to_create_classifier, Toast.LENGTH_LONG).show()
        }
    }

    private fun initView() {

        binding.btnDetect.setOnClickListener { onDetectClick() }

        binding.btnClear.setOnClickListener { clearResults() }
    }

    private fun clearResults() {
        binding.fpvPaint.clear()
        binding.tvPrediction.setText(R.string.empty)
        binding.tvProbability.setText(R.string.empty)
        binding.tvTimecost.setText(R.string.empty)
    }

    private fun onDetectClick() {
        if (!this::classifier.isInitialized) {
            Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized")
            return
        } else if (binding.fpvPaint.empty) {
            Toast.makeText(this, R.string.please_write_a_digit, Toast.LENGTH_SHORT).show()
            return
        }

        val image: Bitmap = binding.fpvPaint.exportToBitmap(
            classifier.inputShape.width, classifier.inputShape.height
        )

        val result = classifier.classify(image)

        renderResult(result)
    }


    private fun renderResult(result: Recognition) {
        binding.tvPrediction.text = java.lang.String.valueOf(result.label)
        binding.tvProbability.text = java.lang.String.valueOf(result.confidence)
        binding.tvTimecost.text = java.lang.String.format(
            getString(R.string.timecost_value),
            result.timeCost
        )
    }


    override fun onDestroy() {
        super.onDestroy()
//        classifier.close()
    }

    companion object {
        private val LOG_TAG: String = MainActivity::class.java.simpleName
    }
}
