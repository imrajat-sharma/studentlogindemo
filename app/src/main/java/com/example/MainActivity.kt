package com.example

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Activity 1: Student Registration Screen
 *
 * Demonstrates:
 * - RelativeLayout UI components (editable TextViews for Name/Email, RadioButtons, CheckBox, Spinner, Button)
 * - DatePickerDialog for selecting Date of Birth
 * - AlertDialog for submission confirmation
 * - Explicit Intent for passing collected student data to Activity 2
 * - Lifecycle logging using Log.d (onCreate, onStart, onResume, onPause, onStop, onDestroy, onRestart)
 * - State preservation across screen rotation using onSaveInstanceState()
 */
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "StudentLoginDemo_Act1"

        // State preservation keys
        private const val KEY_NAME = "saved_name"
        private const val KEY_EMAIL = "saved_email"
        private const val KEY_GENDER_ID = "saved_gender_id"
        private const val KEY_DOB = "saved_dob"
        private const val KEY_COURSE_INDEX = "saved_course_index"
        private const val KEY_TERMS = "saved_terms"

        // Intent extra keys
        const val EXTRA_REG_ID = "extra_reg_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_DOB = "extra_dob"
        const val EXTRA_COURSE = "extra_course"
        const val EXTRA_TERMS_ACCEPTED = "extra_terms_accepted"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
    }

    // UI Widgets
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var rbOther: RadioButton
    private lateinit var tvSelectedDob: TextView
    private lateinit var btnSelectDob: Button
    private lateinit var spinnerCourse: Spinner
    private lateinit var cbTerms: CheckBox
    private lateinit var btnSubmit: Button
    private lateinit var btnClear: Button
    private lateinit var btnAutofill: Button
    private lateinit var tvLifecycleStatus: TextView

    // Internal state
    private var selectedDobFormatted: String = ""
    private var selectedCalendar: Calendar = Calendar.getInstance()

    // ------------------------------------------------------------------------
    // Activity Lifecycle Callbacks
    // ------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity: onCreate() called - Activity created")

        setContentView(R.layout.activity_main)

        initViews()
        setupCourseSpinner()
        setupDatePicker()
        setupButtons()

        // Restore state if available
        if (savedInstanceState != null) {
            Log.d(TAG, "MainActivity: onCreate() - Restoring savedInstanceState bundle")
            restoreFormState(savedInstanceState)
        }

        updateLifecycleDisplay("onCreate() - Activity Initialized")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity: onStart() called - Activity becoming visible")
        updateLifecycleDisplay("onStart() - Activity Visible")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity: onResume() called - Activity in foreground & interactive")
        updateLifecycleDisplay("onResume() - Activity Interactive")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity: onPause() called - Activity pausing")
        updateLifecycleDisplay("onPause() - Activity Pausing")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity: onStop() called - Activity stopped / hidden")
        updateLifecycleDisplay("onStop() - Activity Stopped")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "MainActivity: onRestart() called - Activity restarting after stop")
        updateLifecycleDisplay("onRestart() - Activity Restarting")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity: onDestroy() called - Activity finishing or recreating")
    }

    // ------------------------------------------------------------------------
    // Screen Rotation / State Preservation
    // ------------------------------------------------------------------------

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "MainActivity: onSaveInstanceState() called - Preserving form state for configuration change")

        outState.putString(KEY_NAME, etName.text.toString())
        outState.putString(KEY_EMAIL, etEmail.text.toString())
        outState.putInt(KEY_GENDER_ID, rgGender.checkedRadioButtonId)
        outState.putString(KEY_DOB, selectedDobFormatted)
        outState.putInt(KEY_COURSE_INDEX, spinnerCourse.selectedItemPosition)
        outState.putBoolean(KEY_TERMS, cbTerms.isChecked)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "MainActivity: onRestoreInstanceState() called - Restoring form state")
        restoreFormState(savedInstanceState)
        updateLifecycleDisplay("onRestoreInstanceState() - Form Data Restored Successfully")
    }

    private fun restoreFormState(bundle: Bundle) {
        val name = bundle.getString(KEY_NAME, "")
        val email = bundle.getString(KEY_EMAIL, "")
        val genderId = bundle.getInt(KEY_GENDER_ID, -1)
        val dob = bundle.getString(KEY_DOB, "")
        val courseIndex = bundle.getInt(KEY_COURSE_INDEX, 0)
        val terms = bundle.getBoolean(KEY_TERMS, false)

        etName.setText(name)
        etEmail.setText(email)
        if (genderId != -1) {
            rgGender.check(genderId)
        }
        if (dob.isNotEmpty()) {
            selectedDobFormatted = dob
            tvSelectedDob.text = dob
        }
        if (courseIndex in 0 until spinnerCourse.count) {
            spinnerCourse.setSelection(courseIndex)
        }
        cbTerms.isChecked = terms
    }

    // ------------------------------------------------------------------------
    // View Initialization & Event Handling
    // ------------------------------------------------------------------------

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        rgGender = findViewById(R.id.rgGender)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
        rbOther = findViewById(R.id.rbOther)
        tvSelectedDob = findViewById(R.id.tvSelectedDob)
        btnSelectDob = findViewById(R.id.btnSelectDob)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        cbTerms = findViewById(R.id.cbTerms)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnClear = findViewById(R.id.btnClear)
        btnAutofill = findViewById(R.id.btnAutofill)
        tvLifecycleStatus = findViewById(R.id.tvLifecycleStatus)
    }

    private fun setupCourseSpinner() {
        // Spinner for selecting a Course (MCA, MBA, BCA)
        val courses = resources.getStringArray(R.array.course_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourse.adapter = adapter
    }

    private fun setupDatePicker() {
        // Default calendar set to 2002 for student registration convenience
        selectedCalendar.set(Calendar.YEAR, 2002)
        selectedCalendar.set(Calendar.MONTH, Calendar.JULY)
        selectedCalendar.set(Calendar.DAY_OF_MONTH, 15)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            selectedCalendar.set(Calendar.YEAR, year)
            selectedCalendar.set(Calendar.MONTH, monthOfYear)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDobFormatted = sdf.format(selectedCalendar.time)
            tvSelectedDob.text = selectedDobFormatted
            tvSelectedDob.error = null
            Log.d(TAG, "MainActivity: DatePicker selected DOB -> $selectedDobFormatted")
        }

        val showDatePicker = {
            val dpd = DatePickerDialog(
                this,
                dateSetListener,
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
            )
            // Limit DatePicker to past dates (must be at least 15 years old)
            val maxDateCal = Calendar.getInstance()
            maxDateCal.add(Calendar.YEAR, -15)
            dpd.datePicker.maxDate = maxDateCal.timeInMillis
            dpd.show()
        }

        btnSelectDob.setOnClickListener { showDatePicker() }
        tvSelectedDob.setOnClickListener { showDatePicker() }
    }

    private fun setupButtons() {
        btnSubmit.setOnClickListener {
            handleFormSubmission()
        }

        btnClear.setOnClickListener {
            clearForm()
        }

        btnAutofill.setOnClickListener {
            fillSampleData()
        }
    }

    private fun handleFormSubmission() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val selectedGenderId = rgGender.checkedRadioButtonId
        val termsAccepted = cbTerms.isChecked
        val selectedCourse = spinnerCourse.selectedItem?.toString() ?: "MCA"

        // 1. Validation for Name
        if (name.isEmpty()) {
            etName.error = getString(R.string.error_name_required)
            etName.requestFocus()
            Toast.makeText(this, R.string.error_name_required, Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validation for Email
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.error_email_required)
            etEmail.requestFocus()
            Toast.makeText(this, R.string.error_email_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_email_invalid)
            etEmail.requestFocus()
            Toast.makeText(this, R.string.error_email_invalid, Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Validation for Gender
        if (selectedGenderId == -1) {
            Toast.makeText(this, R.string.error_gender_required, Toast.LENGTH_SHORT).show()
            return
        }
        val gender = findViewById<RadioButton>(selectedGenderId).text.toString()

        // 4. Validation for Date of Birth
        if (selectedDobFormatted.isEmpty()) {
            tvSelectedDob.error = getString(R.string.error_dob_required)
            Toast.makeText(this, R.string.error_dob_required, Toast.LENGTH_SHORT).show()
            return
        }

        // 5. Validation for Terms & Conditions CheckBox
        if (!termsAccepted) {
            cbTerms.error = getString(R.string.error_terms_required)
            Toast.makeText(this, R.string.error_terms_required, Toast.LENGTH_LONG).show()
            return
        } else {
            cbTerms.error = null
        }

        // Show Confirmation AlertDialog before proceeding as required by (c)
        showConfirmationDialog(name, email, gender, selectedDobFormatted, selectedCourse)
    }

    /**
     * Requirement c: Add a Dialog (AlertDialog) that asks for confirmation before submitting the form.
     */
    private fun showConfirmationDialog(
        name: String,
        email: String,
        gender: String,
        dob: String,
        course: String
    ) {
        val message = StringBuilder()
            .append("Please verify the registration details:\n\n")
            .append("• Name: ").append(name).append("\n")
            .append("• Email: ").append(email).append("\n")
            .append("• Gender: ").append(gender).append("\n")
            .append("• DOB: ").append(dob).append("\n")
            .append("• Selected Course: ").append(course).append("\n")
            .append("• Terms: Agreed\n\n")
            .append("Do you confirm and wish to proceed?")
            .toString()

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_confirm_title)
            .setMessage(message)
            .setIcon(R.drawable.ic_school)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_confirm_positive) { dialog, _ ->
                dialog.dismiss()
                Log.d(TAG, "MainActivity: Confirmation AlertDialog confirmed by user -> launching Activity 2 via explicit Intent")
                launchDisplayDetailsActivity(name, email, gender, dob, course)
            }
            .setNegativeButton(R.string.dialog_confirm_negative) { dialog, _ ->
                dialog.dismiss()
                Log.d(TAG, "MainActivity: Confirmation AlertDialog cancelled by user")
                Toast.makeText(this, "Submission cancelled for review", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    /**
     * Requirement b: On clicking Submit, use an explicit Intent to pass the entered data
     * to a second Activity (Activity 2).
     */
    private fun launchDisplayDetailsActivity(
        name: String,
        email: String,
        gender: String,
        dob: String,
        course: String
    ) {
        val regId = "STU-" + SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()) + "-" + (1000..9999).random()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Explicit Intent explicitly referencing DisplayDetailsActivity class
        val intent = Intent(this, DisplayDetailsActivity::class.java).apply {
            putExtra(EXTRA_REG_ID, regId)
            putExtra(EXTRA_NAME, name)
            putExtra(EXTRA_EMAIL, email)
            putExtra(EXTRA_GENDER, gender)
            putExtra(EXTRA_DOB, dob)
            putExtra(EXTRA_COURSE, course)
            putExtra(EXTRA_TERMS_ACCEPTED, "Agreed (Confirmed)")
            putExtra(EXTRA_TIMESTAMP, timestamp)
        }

        Log.d(TAG, "MainActivity: Starting Activity 2 (DisplayDetailsActivity) with explicit Intent for RegID: $regId")
        startActivity(intent)
    }

    private fun fillSampleData() {
        etName.setText("Alex Morgan")
        etEmail.setText("alex.morgan@university.edu")
        rbMale.isChecked = true
        selectedDobFormatted = "18/09/2001"
        tvSelectedDob.text = selectedDobFormatted
        tvSelectedDob.error = null
        spinnerCourse.setSelection(0) // MCA
        cbTerms.isChecked = true
        cbTerms.error = null
        Toast.makeText(this, "Sample student data populated", Toast.LENGTH_SHORT).show()
    }

    private fun clearForm() {
        etName.text.clear()
        etName.error = null
        etEmail.text.clear()
        etEmail.error = null
        rgGender.clearCheck()
        selectedDobFormatted = ""
        tvSelectedDob.text = ""
        tvSelectedDob.error = null
        spinnerCourse.setSelection(0)
        cbTerms.isChecked = false
        cbTerms.error = null
        Toast.makeText(this, "Form fields cleared", Toast.LENGTH_SHORT).show()
    }

    private fun updateLifecycleDisplay(status: String) {
        val text = "Status: $status\nLogs: Tag '$TAG' logged to Log.d()\nState: onSaveInstanceState() active on rotation"
        tvLifecycleStatus.text = text
    }
}
