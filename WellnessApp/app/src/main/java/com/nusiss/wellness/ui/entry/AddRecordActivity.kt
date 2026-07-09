/**
 * @author ZhangMingchang
 */
package com.nusiss.wellness.ui.entry

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var selectedExerciseType = "Running"
    private lateinit var exerciseTypeChips: List<TextView>
    private var recordToEdit: WellnessRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordToEdit = intent.getSerializableExtra("RECORD_DATA") as? WellnessRecord

        binding.etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

        exerciseTypeChips = listOf(
            binding.chipExerciseRunning,
            binding.chipExerciseYoga,
            binding.chipExerciseSwimming,
            binding.chipExerciseGym,
            binding.chipExerciseOther
        )
        exerciseTypeChips.forEach { chip ->
            chip.setOnClickListener { selectExerciseType(chip.text.toString()) }
        }

        if (recordToEdit != null) {
            setupEditMode(recordToEdit!!)
        } else {
            selectType("SLEEP")
        }

        binding.chipSleep.setOnClickListener { selectType("SLEEP") }
        binding.chipExercise.setOnClickListener { selectType("EXERCISE") }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveRecord() }
    }

    private fun setupEditMode(record: WellnessRecord) {
        binding.btnBack.text = "Edit Record"
        binding.etDate.setText(record.recordDate)
        binding.etValue.setText(if (record.value % 1.0 == 0.0) record.value.toInt().toString() else record.value.toString())
        binding.etNote.setText(record.note ?: "")
        selectedExerciseType = record.exerciseType ?: "Running"
        selectType(record.type)
    }

    private fun selectType(type: String) {
        selectedType = type
        val isSleep = type == "SLEEP"
        binding.chipSleep.setBackgroundResource(
            if (isSleep) R.drawable.bg_pill_sleep_accent else R.drawable.bg_pill_outline
        )
        binding.chipSleep.setTextColor(
            ContextCompat.getColor(this, if (isSleep) R.color.green_primary else R.color.text_secondary)
        )
        binding.chipExercise.setBackgroundResource(
            if (!isSleep) R.drawable.bg_pill_exercise_accent else R.drawable.bg_pill_outline
        )
        binding.chipExercise.setTextColor(
            ContextCompat.getColor(this, if (!isSleep) R.color.white else R.color.text_secondary)
        )
        binding.tvValueLabel.text = if (isSleep) "Sleep Duration (hrs)" else "Exercise Duration (min)"

        binding.tvExerciseTypeLabel.visibility = if (isSleep) View.GONE else View.VISIBLE
        binding.scrollExerciseType.visibility = if (isSleep) View.GONE else View.VISIBLE
        if (!isSleep) {
            selectExerciseType(selectedExerciseType)
        }
    }

    private fun selectExerciseType(type: String) {
        selectedExerciseType = type
        exerciseTypeChips.forEach { chip ->
            val isSelected = chip.text.toString() == type
            chip.setBackgroundResource(
                if (isSelected) R.drawable.bg_pill_exercise_accent else R.drawable.bg_pill_outline
            )
            chip.setTextColor(
                ContextCompat.getColor(this, if (isSelected) R.color.white else R.color.text_secondary)
            )
        }
    }

    private fun saveRecord() {
        val valueText = binding.etValue.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        if (valueText.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val value = valueText.toDoubleOrNull()
        if (value == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        val log = if (selectedType == "SLEEP") {
            WellnessLog(logDate = date, sleepHours = value, moodScore = 5, notes = note.ifEmpty { null })
        } else {
            WellnessLog(
                logDate = date,
                exerciseType = selectedExerciseType,
                exerciseMinutes = value.toInt(),
                moodScore = 5,
                notes = note.ifEmpty { null }
            )
        }

        binding.btnSave.isEnabled = false
        lifecycleScope.launch {
            try {
                val recordId = recordToEdit?.id?.toLongOrNull()
                val response = if (recordId != null) {
                    RetrofitClient.api.updateRecord(recordId, log)
                } else {
                    RetrofitClient.api.addRecord(log)
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@AddRecordActivity, if (recordId != null) "Entry updated" else "Entry saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddRecordActivity, "Save failed. Please try again", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddRecordActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSave.isEnabled = true
            }
        }
    }
}
