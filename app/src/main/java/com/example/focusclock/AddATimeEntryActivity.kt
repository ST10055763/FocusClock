package com.example.focusclock

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddATimeEntryActivity : AppCompatActivity() {
lateinit var backBtn:Button
lateinit var addTaskBtn: Button
lateinit var logBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_atime_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupDropDown()
    }
    private fun setupDropDown()
    {
        val items = arrayOf("material", "design")
        val autocompleteTextView: AutoCompleteTextView = findViewById(R.id.auto_project_txt)
        val adapterItem = ArrayAdapter<String>(this, R.layout.list_item, items)
        autocompleteTextView.setAdapter(adapterItem)
        autocompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
            val item = adapterItem.getItem(i).toString()
            Toast.makeText(this@AddATimeEntryActivity, "Item: $item", Toast.LENGTH_SHORT).show()
        }


    }

}


//String[] item = {"Material", "design"};
//AutoCompleteTextView autoCompleteTextView;
//ArrayAdapter<String> adapterItems;

//autoCompleteTextView = findViewById(R.id.auto_complete_txt);
//adapterItems = new ArrayAdapter<String)(this, R.layout.list_item);
//autoCompleteTextView.setAdapter(adapterItems);
//autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
  //  @Override
    //public void OnItemClick(AdapterView<?> AdapterView, View view, int i, long l)
    //{
      //  String item = adapterView.getItemAtPosition(i).toString;
        //Toast.makeText(AddATimeEntryActivity.this, "Item: " + item, Toast.LENGTH_SHORT).show();
    //}

//});