package com.example.a24012021063_practical_7

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a24012021063_practical_7.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: PersonAdapter
    private var personList = ArrayList<Person>()

    companion object {
        private const val TAG = "MainActivity"
        private const val API_URL = "https://api.json-generator.com/templates/ILfcJrDbCbBv/data"
        private const val TOKEN = "b255walr92zgtbe7clq31yj09dxlx43a6m5fx0v3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseHelper = DatabaseHelper(this)

        setupRecyclerView()
        loadPersonsFromDatabase()
        getPersonDataFromApi()
    }
    private fun setupRecyclerView() {
        adapter = PersonAdapter(this,personList,databaseHelper)
        binding.recyclerViewPersons.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPersons.adapter = adapter
    }
    private fun getPersonDataFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = HttpRequest().makeServiceCall(
                    reqUrl = API_URL,
                    token = TOKEN,
                )
                withContext(Dispatchers.Main) {
                    if (data != null) {
                        getPersonDetailsFromJson(data)
                    } else {
                        Log.e(TAG,"No Data Received")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG,"API Error",e)
            }
        }
    }
    private fun getPersonDetailsFromJson(json: String) {
        val db = databaseHelper.writableDatabase
        db.delete(PersonDbTableData.TABLE_NAME, null, null)
        db.close()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val email = obj.getString("email")
                val phone = obj.getString("phone")
                val profile = obj.getJSONObject("profile")
                val name = profile.getString("name")
                val address = profile.getString("address")
                val location = profile.getJSONObject("location")
                val latitude = location.getDouble("lat")
                val longitude = location.getDouble("long")
                val person = Person(
                    id,
                    name,
                    email,
                    phone,
                    address,
                    latitude,
                    longitude
                )
                databaseHelper.insertPerson(person)
            }
            loadPersonsFromDatabase()
        } catch (e: Exception) {
            Log.e(TAG,"JSON Parsing Error",e)
        }
    }
    private fun loadPersonsFromDatabase() {
        personList.clear()
        personList.addAll(
            databaseHelper.getAllPersons()
        )
        adapter.notifyDataSetChanged()
    }
}