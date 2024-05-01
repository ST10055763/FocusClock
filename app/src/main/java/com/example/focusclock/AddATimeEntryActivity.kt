package com.example.focusclock

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddATimeEntryActivity : AppCompatActivity() {
lateinit var backBtn:FloatingActionButton
lateinit var addTaskBtn: FloatingActionButton
lateinit var logBtn: Button
lateinit var timeEntryPic : ImageButton
lateinit var timeEntryProject : Spinner
lateinit var timeEntryTask:Spinner
lateinit var timeEntryStartTime : EditText
lateinit var timeEntryEndTime : EditText
 var t: List<String> = emptyList()
 var proj: List<String> = emptyList()

    private val TimeEntrydb = FirebaseFirestore.getInstance()
    // db may be redundant, but would rather use in case of confusion leading to loss of data
    lateinit var db : FirebaseFirestore

    private lateinit var storageRef: StorageReference

    private lateinit var imageUri: Uri
    private lateinit var imageUriFStorage: String
    private var currentPhotoPath: String? = null

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_atime_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backBtn = findViewById(R.id.AEfloatingReturnButton)
        addTaskBtn = findViewById(R.id.floatingAddTaskButton)
        logBtn = findViewById(R.id.AELoginBtn)
        timeEntryProject = findViewById(R.id.spinnerTimeEntProj)
        timeEntryTask = findViewById(R.id.spinnerTimeEntTask)
        timeEntryStartTime = findViewById(R.id.AEStartTimetxt)
        timeEntryEndTime = findViewById(R.id.AEendTimetxt)
        timeEntryPic = findViewById(R.id.AEAddPictureBtn)

        storageRef = Firebase.storage.reference
        val user = Firebase.auth.currentUser
        val userId = user?.uid
        fetchFireStoreProjects(userId)
        fetchFireStoreTasks(userId)

        timeEntryStartTime.setOnClickListener{
            showTimePickerDialog(isStartTime = true)
        }

        timeEntryEndTime.setOnClickListener{
            showTimePickerDialog(isStartTime = true)
        }

        logBtn.setOnClickListener {
             //val userId = FirebaseAuth.getInstance().currentUser?.uid
            //val user = Firebase.auth.currentUser
            //val userId = user?.uid
            if (userId != null) {

                createTimeEntry(userId)
            }

        }
        addTaskBtn.setOnClickListener{
            var returnLoginIntent = Intent(this, AddATaskActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

        backBtn.setOnClickListener{
            var returnLoginIntent = Intent(this, HomePageActivity::class.java)
            startActivity(returnLoginIntent)
            // using finish() to end the activity
            finish()
        }

        timeEntryPic.setOnClickListener{
            showImageSourceDialog()
        }

    }
    fun populateprojectSpinner(projectList: List<String>)
    {
        //val projectName = proj.map { it.pname }
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectName)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //timeEntryProject.adapter = adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeEntryProject.adapter = adapter

    }
    fun populatetaskSpinner(taskList: List<String>)
    {
       // val taskname = t.map { it.tname }
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskname)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //timeEntryTask.adapter = adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeEntryTask.adapter = adapter

    }
    fun getCurrentDate(): String
    {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun fetchFireStoreProjects(userId: String?)
    {
        db = FirebaseFirestore.getInstance()
        val progref = db.collection("projects")
        progref
            .whereEqualTo("firebaseUUID", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val projectList = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val pname = document.getString("pname") ?: ""
                    projectList.add(pname)
                }
                proj = projectList // Update projects with fetched project names
                populateprojectSpinner(projectList)
                //saveTaskBtn.isEnabled = true
            }
    }
    fun fetchFireStoreTasks(userId: String?)
    {
        db = FirebaseFirestore.getInstance()
        val taskref = db.collection("task")
        taskref
            .whereEqualTo("firebaseUUID", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val tname = document.getString("tname") ?: ""
                    taskList.add(tname)
                }
                t = taskList
                populatetaskSpinner(taskList)
            }
    }

    private fun showImageSourceDialog() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload a Time Sheet Picture")
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    takePhoto()
                }
                items[item] == "Choose from Gallery" -> {
                    chooseFromGallery()
                }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    if (data != null && data.data != null) {
                        imageUri = data.data!!
                        setImageButton()
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    if (data != null && data.extras != null) {
                        val imageBitmap = data.extras!!.get("data") as Bitmap
                        imageUri = getImageUri(imageBitmap)
                        setImageButton()
                    }
                }
            }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun setImageButton() {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            timeEntryPic.setImageBitmap(bitmap)
            // uploadImageToFirebaseStorage()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun uploadImageToFirebaseStorage(callback: ()-> Unit) {
        // Get a reference to where the image will be stored in Firebase Storage
        val imageRef = storageRef.child("timeentryimages/${UUID.randomUUID()}")

        // Upload image to Firebase Storage
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, now get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                //val imageUrl = uri.toString()
                imageUriFStorage = uri.toString()
                callback()
                // Store image URL in Firestore
                // storeImageUrlInFirestore(imageUrl)
            }
        }.addOnFailureListener { exception ->
            // Handle unsuccessful uploads
            Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
        }
    }

    fun createTimeEntry(userId : String)
    {
        val startTime = timeEntryStartTime.text.toString()
        val endTime = timeEntryEndTime.text.toString()
        //val currentDate = getCurrentDate().text

        if(startTime.isEmpty() || endTime.isEmpty())
        {
            Toast.makeText(this, "Please Fill In All Necessary Time Entry Details", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if an image is selected
        val hasImage = imageUri != Uri.EMPTY

        if (hasImage)
        {
            uploadImageToFirebaseStorage{
                createTimeEntry(userId)
            }
        }
        else
        {
            imageUriFStorage = "null"
            createTimeEntry(userId)
        }

        val entryProject = proj[timeEntryProject.selectedItemPosition]//correct
        val selectedTask = t[timeEntryTask.selectedItemPosition]//correct
        val timeEntry = TimeEntry(
            firebaseUUID = userId,
            startTime = startTime,
            endTime = endTime,
            selectedTask = selectedTask,
            entryProject = entryProject,
            timeEntryPicRef = imageUriFStorage,
            currentDate = getCurrentDate()
        )
        TimeEntrydb.collection("timeentry")
            .add(timeEntry)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Time Entry Added Successfully", Toast.LENGTH_SHORT).show()

            }

    }

    private fun showTimePickerDialog(isStartTime : Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Format the time chosen by the user (HH:mm).
            val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour,selectedMinute)
            if (isStartTime) {
                timeEntryStartTime.setText(selectedTime)
            } else {
                timeEntryEndTime.setText(selectedTime)
            }
        }, hour, minute, true)

        timePickerDialog.show()
    }



    }




