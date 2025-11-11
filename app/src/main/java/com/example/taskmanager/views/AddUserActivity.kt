package com.example.taskmanager.views

import android.R.attr.description
import android.app.DatePickerDialog
import android.content.ClipDescription
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.CaseMap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityAddUserBinding
import com.example.taskmanager.db.User
import com.example.taskmanager.db.UserDao
import com.example.taskmanager.db.UserDatabase
import java.util.Calendar
import androidx.core.graphics.toColorInt
import androidx.room.Update
import kotlinx.coroutines.NonCancellable.isCompleted

@Suppress("DEPRECATION")
class AddUserActivity : AppCompatActivity() {
    companion object {
        const val editKey = "edit"
        const val update = "Update Task "
        const val save = "Save Task"
    }

    private lateinit var binding: ActivityAddUserBinding
    private lateinit var dao: UserDao

    var userId = 0
    private var isSwitchTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "User_DB"
        ).allowMainThreadQueries()
            .build()

        dao = db.getUserDao()
        if (intent.hasExtra(editKey)) {
            binding.btnSaveTask.text = update

            val user = intent.getParcelableExtra<User>(editKey)

            user?.let {
                binding.apply {
                    etTaskTitle.setText(it.title)
                    etTaskDescription.setText(it.description)
                    etDueDate.setText(it.date)
                    switchStatus.setText(it.isCompleted.toString())
                    userId = it.id
                }
            }


        } else {
            binding.btnSaveTask.text = save
        }


        binding.etDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val date = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etDueDate.setText(selectedDate)
                },
                year, month, day
            )
            date.show()
        }




        binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->


            isSwitchTouched = true

            if (isChecked) {

                binding.switchStatus.text = "Task Completed "
                binding.switchStatus.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")))
                binding.switchStatus.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")))

            } else {

                binding.switchStatus.text = "Task Pending "
                binding.switchStatus.setThumbTintList(ColorStateList.valueOf("#F44336".toColorInt()))
                binding.switchStatus.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#F44336")))

            }
        }

        binding.btnSaveTask.setOnClickListener {
            val title = binding.etTaskTitle.text.toString().trim()
            val description = binding.etTaskDescription.text.toString().trim()
            val date = binding.etDueDate.text.toString().trim()
            val isCompleted = binding.switchStatus.isChecked


            if (title.isEmpty()) {
                binding.etTaskTitle.error = "Please Enter Your Task Title"
                binding.etTaskTitle.requestFocus()
                return@setOnClickListener
            } else if (description.isEmpty()) {
                binding.etTaskDescription.error = "Please Enter Your Task Description"
                binding.etTaskDescription.requestFocus()
                return@setOnClickListener
            } else if (date.isEmpty()) {
                binding.etDueDate.error = "Please Enter Due Date"
                Toast.makeText(this, "Please Enter Due Date", Toast.LENGTH_SHORT).show()
                binding.etDueDate.requestFocus()
                return@setOnClickListener
            } else if (!isSwitchTouched) {
                Toast.makeText(this, "Please Select Completion Status", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (binding.btnSaveTask.text.toString() == save) {
                addUser(title, description, date, isCompleted)
            } else {
                updateUser(title, description, date, isCompleted)

            }


        }
    }

    fun updateUser(title: String, description: String, date: String, isCompleted: Boolean) {
        val user = User(userId, title, description, date, isCompleted)
        dao.editUser(user)
        startActivity(Intent(this, MainActivity::class.java))

    }

    private fun addUser(title: String, description: String, date: String, isCompleted: Boolean) {
        val user = User(userId, title, description, date, isCompleted)
        dao.addUser(user)
        finish()
    }

}