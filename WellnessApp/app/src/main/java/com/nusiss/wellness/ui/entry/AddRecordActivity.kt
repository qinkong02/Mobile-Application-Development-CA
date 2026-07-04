package com.nusiss.wellness.ui.entry

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nusiss.wellness.R
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.model.WellnessRecord
import com.nusiss.wellness.databinding.ActivityAddRecordBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.nusiss.wellness.data.model.WellnessLog

class AddRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecordBinding
    private var selectedType = "SLEEP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

        selectType("SLEEP")
        binding.chipSleep.setOnClickListener { selectType("SLEEP") }
        binding.chipExercise.setOnClickListener { selectType("EXERCISE") }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveRecord() }
    }

    private fun selectType(type: String) {
        selectedType = type
        val isSleep = type == "SLEEP"
        binding.chipSleep.setBackgroundResource(
            if (isSleep) R.drawable.bg_pill_accent else R.drawable.bg_pill_outline
        )
        binding.chipExercise.setBackgroundResource(
            if (!isSleep) R.drawable.bg_pill_accent else R.drawable.bg_pill_outline
        )
        binding.tvValueLabel.text = if (isSleep) "睡眠时长（小时）" else "运动时长（分钟）"
    }

    private fun saveRecord() {
        val valueText = binding.etValue.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        if (valueText.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }

        val value = valueText.toDoubleOrNull()
        if (value == null) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show()
            return
        }

        val log = if (selectedType == "SLEEP") {
            WellnessLog(logDate = date, sleepHours = value, moodScore = 5, notes = note.ifEmpty { null })
        } else {
            WellnessLog(
                logDate = date,
                exerciseType = note.ifEmpty { "运动" },  // 备注暂当运动类型用
                exerciseMinutes = value.toInt(),
                moodScore = 5,
                notes = note.ifEmpty { null }
            )
        }

        binding.btnSave.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.addRecord(log)
                if (response.isSuccessful) {
                    Toast.makeText(this@AddRecordActivity, "记录已保存", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddRecordActivity, "保存失败，请重试", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddRecordActivity, "网络连接失败：${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSave.isEnabled = true
            }
        }
    }
}
