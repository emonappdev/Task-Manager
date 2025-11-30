package com.example.taskmanager.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.db.User
import com.example.taskmanager.db.UserDao
import com.example.taskmanager.db.UserDatabase

class MainActivity : AppCompatActivity(), UserAdapter.HandleUserClick {
    lateinit var binding: ActivityMainBinding
    lateinit var userAdapter: UserAdapter
    private lateinit var dao: UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }// ----------------------------------------------------------------------------------------------------------


        val db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "User_DB"
        ).allowMainThreadQueries().build()
        dao = db.getUserDao()

        setUserData()


        binding.addUserBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddUserActivity::class.java)
            startActivity(intent)

        }
    }

    private fun setUserData() {
        dao.getAllUser().apply {
            userAdapter = UserAdapter(this@MainActivity, this as MutableList<User>)
            binding.rvUser.adapter = userAdapter
        }
    }

    override fun onEditClick(user: User) {
        val editIntent = Intent(this@MainActivity, AddUserActivity::class.java)
        editIntent.putExtra(AddUserActivity.editKey, user)
        startActivity(editIntent)
    }

    override fun onDeleteClick(user: User) {
        dao.deleteUser(user)
        Toast.makeText(this@MainActivity, "${user.title} Has Been Deleted", Toast.LENGTH_LONG).show()
        setUserData()
    }



    override fun onResume() {
        super.onResume()
        val allUser = dao.getAllUser()


        userAdapter.clearAll()
        allUser.forEach { userAdapter.addItem(it) }
    }


}