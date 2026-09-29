package com.example

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity 2: Display Details Screen
 *
 * Demonstrates:
 * - Unpacking explicit Intent data passed from MainActivity
 * - Non-editable TextViews styled using a TableLayout
 * - Spinner and Enrolled Courses List containing at least five static enrolled courses
 * - Lifecycle logging via Log.d (onCreate, onStart, onResume, onPause, onStop, onDestroy, onRestart)
 * - State preservation across configuration changes via onSaveInstanceState()
 */
class DisplayDetailsActivity : AppCompatActivity() {

    companion object {
        const val TAG = "StudentLoginDemo_Act2"

        private const val KEY_SAVED_REG_ID = "saved_act2_reg_id"
        private const val KEY_SAVED_FILTER_INDEX = "saved_act2_filter_index"
    }

    // TableLayout non-editable TextView widgets
    private lateinit var tvDisplayRegId: TextView
    private lateinit var tvDisplayName: TextView
    private lateinit var tvDisplayEmail: TextView
    private lateinit var tvDisplayGender: TextView
    private lateinit var tvDisplayDob: TextView
    private lateinit var tvDisplayCourse: TextView
    private lateinit var tvDisplayTerms: TextView
    private lateinit var tvDisplayTimestamp: TextView

    // Courses UI widgets
    private lateinit var spinnerCourseFilter: Spinner
    private lateinit var coursesContainer: LinearLayout
    private lateinit var tvSelectedCourseDetail: TextView
    private lateinit var tvCoursesSubtitle: TextView

    // Navigation and status widgets
    private lateinit var btnBack: Button
    private lateinit var btnNewRegistration: Button
    private lateinit var tvLifecycleStatus2: TextView

    // Course data structure
    data class EnrolledCourse(
        val code: String,
        val title: String,
        val credits: Int,
        val department: String,
        val semester: String,
        val instructor: String
    )

    // Static list of at least five enrolled courses (Requirement d)
    private val allCourses = listOf(
        EnrolledCourse(
            code = "MCA101",
            title = "Advanced Data Structures & Algorithms",
            credits = 4,
            department = "MCA",
            semester = "Sem 1",
            instructor = "Dr. Alan Turing"
        ),
        EnrolledCourse(
            code = "MCA102",
            title = "Relational Database Management Systems & SQL",
            credits = 4,
            department = "MCA",
            semester = "Sem 1",
            instructor = "Prof. Edgar Codd"
        ),
        EnrolledCourse(
            code = "MCA103",
            title = "Mobile Application Development (Android)",
            credits = 3,
            department = "MCA",
            semester = "Sem 2",
            instructor = "Prof. Andy Rubin"
        ),
        EnrolledCourse(
            code = "MCA104",
            title = "Cloud Computing & Distributed Systems",
            credits = 3,
            department = "MCA",
            semester = "Sem 2",
            instructor = "Dr. Werner Vogels"
        ),
        EnrolledCourse(
            code = "MCA105",
            title = "Artificial Intelligence & Machine Learning",
            credits = 4,
            department = "MCA",
            semester = "Sem 3",
            instructor = "Dr. Geoffrey Hinton"
        ),
        EnrolledCourse(
            code = "MBA201",
            title = "Strategic Management & Corporate Leadership",
            credits = 4,
            department = "MBA",
            semester = "Sem 1",
            instructor = "Prof. Michael Porter"
        ),
        EnrolledCourse(
            code = "BCA301",
            title = "Object Oriented Software Development (Java)",
            credits = 4,
            department = "BCA",
            semester = "Sem 3",
            instructor = "Dr. James Gosling"
        )
    )

    private var currentFilteredCourses = mutableListOf<EnrolledCourse>()

    // ------------------------------------------------------------------------
    // Activity Lifecycle Callbacks (Requirement e)
    // ------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "DisplayDetailsActivity: onCreate() called - Activity 2 created")

        setContentView(R.layout.activity_display_details)

        initViews()
        populateSubmittedDetailsFromIntent()
        setupCoursesSection()
        setupNavigationButtons()

        if (savedInstanceState != null) {
            val savedFilter = savedInstanceState.getInt(KEY_SAVED_FILTER_INDEX, 0)
            if (savedFilter in 0 until spinnerCourseFilter.count) {
                spinnerCourseFilter.setSelection(savedFilter)
            }
            Log.d(TAG, "DisplayDetailsActivity: onCreate() - Restored saved filter index $savedFilter")
        }

        updateLifecycleDisplay("onCreate() - Details Populated via Intent")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "DisplayDetailsActivity: onStart() called - Activity 2 visible")
        updateLifecycleDisplay("onStart() - Visible to User")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "DisplayDetailsActivity: onResume() called - Activity 2 in foreground")
        updateLifecycleDisplay("onResume() - Active & Interactive")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "DisplayDetailsActivity: onPause() called - Activity 2 pausing")
        updateLifecycleDisplay("onPause() - Pausing")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "DisplayDetailsActivity: onStop() called - Activity 2 stopped")
        updateLifecycleDisplay("onStop() - Stopped")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "DisplayDetailsActivity: onRestart() called - Activity 2 restarting")
        updateLifecycleDisplay("onRestart() - Restarting")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "DisplayDetailsActivity: onDestroy() called - Activity 2 destroying")
    }

    // ------------------------------------------------------------------------
    // State Preservation (Requirement e)
    // ------------------------------------------------------------------------

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "DisplayDetailsActivity: onSaveInstanceState() called - Preserving Activity 2 state")
        outState.putString(KEY_SAVED_REG_ID, tvDisplayRegId.text.toString())
        outState.putInt(KEY_SAVED_FILTER_INDEX, spinnerCourseFilter.selectedItemPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "DisplayDetailsActivity: onRestoreInstanceState() called - Restored Activity 2 state")
        val regId = savedInstanceState.getString(KEY_SAVED_REG_ID, "")
        if (regId.isNotEmpty()) {
            tvDisplayRegId.text = regId
        }
        val filterIndex = savedInstanceState.getInt(KEY_SAVED_FILTER_INDEX, 0)
        spinnerCourseFilter.setSelection(filterIndex)
        updateLifecycleDisplay("onRestoreInstanceState() - State Restored")
    }

    // ------------------------------------------------------------------------
    // View Setup and Intent Data Binding
    // ------------------------------------------------------------------------

    private fun initViews() {
        tvDisplayRegId = findViewById(R.id.tvDisplayRegId)
        tvDisplayName = findViewById(R.id.tvDisplayName)
        tvDisplayEmail = findViewById(R.id.tvDisplayEmail)
        tvDisplayGender = findViewById(R.id.tvDisplayGender)
        tvDisplayDob = findViewById(R.id.tvDisplayDob)
        tvDisplayCourse = findViewById(R.id.tvDisplayCourse)
        tvDisplayTerms = findViewById(R.id.tvDisplayTerms)
        tvDisplayTimestamp = findViewById(R.id.tvDisplayTimestamp)

        spinnerCourseFilter = findViewById(R.id.spinnerCourseFilter)
        coursesContainer = findViewById(R.id.coursesContainer)
        tvSelectedCourseDetail = findViewById(R.id.tvSelectedCourseDetail)
        tvCoursesSubtitle = findViewById(R.id.tvCoursesSubtitle)

        btnBack = findViewById(R.id.btnBack)
        btnNewRegistration = findViewById(R.id.btnNewRegistration)
        tvLifecycleStatus2 = findViewById(R.id.tvLifecycleStatus2)
    }

    /**
     * Requirement b: displays the submitted details in a non-editable TextView,
     * styled using a Table Layout.
     */
    private fun populateSubmittedDetailsFromIntent() {
        val regId = intent.getStringExtra(MainActivity.EXTRA_REG_ID) ?: "STU-2026-DEFAULT"
        val name = intent.getStringExtra(MainActivity.EXTRA_NAME) ?: "N/A"
        val email = intent.getStringExtra(MainActivity.EXTRA_EMAIL) ?: "N/A"
        val gender = intent.getStringExtra(MainActivity.EXTRA_GENDER) ?: "N/A"
        val dob = intent.getStringExtra(MainActivity.EXTRA_DOB) ?: "N/A"
        val course = intent.getStringExtra(MainActivity.EXTRA_COURSE) ?: "MCA"
        val terms = intent.getStringExtra(MainActivity.EXTRA_TERMS_ACCEPTED) ?: "Agreed (Confirmed)"
        val timestamp = intent.getStringExtra(MainActivity.EXTRA_TIMESTAMP) ?: "N/A"

        Log.d(TAG, "DisplayDetailsActivity: Unpacked Intent extras -> Name: $name, Email: $email, Course: $course")

        tvDisplayRegId.text = regId
        tvDisplayName.text = name
        tvDisplayEmail.text = email
        tvDisplayGender.text = gender
        tvDisplayDob.text = dob
        tvDisplayCourse.text = course
        tvDisplayTerms.text = terms
        tvDisplayTimestamp.text = timestamp
    }

    /**
     * Requirement d: Add a List View / Spinner on Activity 2 to display a static list
     * of at least five enrolled courses.
     */
    private fun setupCoursesSection() {
        val filterOptions = resources.getStringArray(R.array.course_filter_options)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourseFilter.adapter = spinnerAdapter

        // Default to showing all courses (contains at least 5 courses)
        renderCourseItems(allCourses)

        spinnerCourseFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filtered = when (position) {
                    1 -> allCourses.filter { it.department == "MCA" }
                    2 -> allCourses.filter { it.department == "MBA" }
                    3 -> allCourses.filter { it.department == "BCA" }
                    else -> allCourses
                }
                Log.d(TAG, "DisplayDetailsActivity: Filter selected ($position) -> Showing ${filtered.size} courses")
                renderCourseItems(filtered)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun renderCourseItems(courses: List<EnrolledCourse>) {
        currentFilteredCourses.clear()
        currentFilteredCourses.addAll(courses)
        coursesContainer.removeAllViews()

        tvCoursesSubtitle.text = "Showing ${courses.size} Enrolled Courses (Static Roster - Min. 5)"

        val inflater = LayoutInflater.from(this)
        for ((index, course) in courses.withIndex()) {
            val itemView = inflater.inflate(R.layout.item_enrolled_course, coursesContainer, false)

            val tvCourseBadge = itemView.findViewById<TextView>(R.id.tvCourseBadge)
            val tvCourseTitle = itemView.findViewById<TextView>(R.id.tvCourseTitle)
            val tvCourseCodeAndCredits = itemView.findViewById<TextView>(R.id.tvCourseCodeAndCredits)
            val tvCourseStatus = itemView.findViewById<TextView>(R.id.tvCourseStatus)

            tvCourseBadge.text = course.code.take(3)
            tvCourseTitle.text = "${index + 1}. ${course.title}"
            tvCourseCodeAndCredits.text = "${course.code} • ${course.credits} Credits • ${course.semester} • ${course.instructor}"
            tvCourseStatus.text = "Enrolled"

            itemView.setOnClickListener {
                val detailMsg = "Selected: ${course.code} - ${course.title}\nCredits: ${course.credits} | Instructor: ${course.instructor}"
                tvSelectedCourseDetail.text = detailMsg
                Toast.makeText(this, "${course.code}: ${course.title}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "DisplayDetailsActivity: Enrolled course clicked: ${course.code}")
            }

            coursesContainer.addView(itemView)
        }
    }

    private fun setupNavigationButtons() {
        btnBack.setOnClickListener {
            Log.d(TAG, "DisplayDetailsActivity: Back button tapped -> finishing Activity 2 to return to Activity 1")
            finish()
        }

        btnNewRegistration.setOnClickListener {
            Log.d(TAG, "DisplayDetailsActivity: New Registration tapped -> finishing Activity 2")
            finish()
        }
    }

    private fun updateLifecycleDisplay(status: String) {
        val text = "Status: $status\nLogs: Tag '$TAG' logged to Log.d()\nState: TableLayout & Courses active"
        tvLifecycleStatus2.text = text
    }
}
