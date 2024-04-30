package com.example.focusclock

import android.app.Activity
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
import java.io.ByteArrayOutputStream
import java.io.IOException
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
lateinit var t: List<Task>
lateinit var proj: List<Project>

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

        fetchFireStoreProjects()
        fetchFireStoreTasks()

        logBtn.setOnClickListener {
             //val userId = FirebaseAuth.getInstance().currentUser?.uid
            val user = Firebase.auth.currentUser
            val userId = user?.uid
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
    fun populateprojectSpinner()
    {
        val projectName = proj.map { it.pname }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeEntryProject.adapter = adapter

    }
    fun populatetaskSpinner()
    {
        val taskname = t.map { it.tname }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskname)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeEntryTask.adapter = adapter

    }
    fun fetchFireStoreProjects()
    {
        db=FirebaseFirestore.getInstance()
        val progref = db.collection("projects")
        progref.get()
            .addOnSuccessListener { querySnapshot ->
                val projectList = mutableListOf<Project>()
                for (document in querySnapshot.documents) {
                    val firebaseUUID = document.getString("firebaseUUID")
                    val pname = document.getString("pname")?: ""
                    val ddate = document.getString("ddate")?: ""
                    val ghrs = document.getLong("ghrs")?.toInt() ?: 0
                    val project = Project(firebaseUUID,pname, ddate, ghrs)
                    projectList.add(project)
                }
                proj = projectList
                populateprojectSpinner()
                //addTaskBtn.isEnabled = true
            }
    }
    fun fetchFireStoreTasks()
    {
        db = FirebaseFirestore.getInstance()
        val taskref = db.collection("task")
        taskref.get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<Task>()
                for (document in querySnapshot.documents) {
                    val firebaseUUID = document.getString("firebaseUUID")
                    val tname = document.getString("tname")
                    val tdescription = document.getString("tdescription")
                    val projectID = document.getString("selectedproject")
                    //val project = fetchFireStoreProjects(projectID)
                    val project = proj.find { it.firebaseUUID == projectID }
                    val task = Task(firebaseUUID, tname, tdescription, project)
                    taskList.add(task)
                }
                t=taskList
                populatetaskSpinner()
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

    private fun uploadImageToFirebaseStorage() {
        // Get a reference to where the image will be stored in Firebase Storage
        val imageRef = storageRef.child("timeentryimages/${UUID.randomUUID()}")

        // Upload image to Firebase Storage
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, now get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                //val imageUrl = uri.toString()
                imageUriFStorage = uri.toString()
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

        if(startTime.isEmpty() || endTime.isEmpty())
        {
            Toast.makeText(this, "Please Fill In All Necessary Time Entry Details", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if an image is selected
        val hasImage = imageUri != Uri.EMPTY

        if (hasImage)
        {
            uploadImageToFirebaseStorage()
        }
        else
        {
            imageUriFStorage = "null"
        }

        val entryProject = proj[timeEntryProject.selectedItemPosition]
        val selectedTask = t[timeEntryTask.selectedItemPosition]
        val timeEntry = TimeEntry(
            firebaseUUID = userId,
            startTime = startTime,
            endTime = endTime,
            selectedTask = selectedTask,
            entryProject = entryProject,
            timeEntryPicRef = imageUriFStorage
        )
        TimeEntrydb.collection("timeentry")
            .add(timeEntry)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Time Entry Added Successfully", Toast.LENGTH_SHORT).show()

            }

    }



    }




