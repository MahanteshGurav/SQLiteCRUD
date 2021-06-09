package com.mahantesh.sqlitecrud

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahantesh.sqlitecrud.adapter.ItemAdapter
import com.mahantesh.sqlitecrud.helper.DatabaseHandler
import com.mahantesh.sqlitecrud.model.Employee
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_update.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tbMain)
        btnAdd.setOnClickListener { view ->
            addRecord(view)
        }
        setupListDataIntoRecyclerView()
    }

    fun addRecord(view: View) {
        val name = etName.text.toString()
        val email = etEmailId.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (name.isNotEmpty() && email.isNotEmpty()) {
            val status = databaseHandler.addEmployee(Employee(0, name, email))
            if (status > -1) {
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                etName.text.clear()
                etEmailId.text.clear()

                setupListDataIntoRecyclerView()
            }
        } else {
            Toast.makeText(this, "Name or email cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupListDataIntoRecyclerView() {
        if (getItemsList().size > 0) {
            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            rvItemsList.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getItemsList())
            rvItemsList.adapter = itemAdapter
        } else {
            rvItemsList.visibility = View.GONE
        }
    }

    private fun getItemsList(): ArrayList<Employee> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<Employee> = databaseHandler.viewEmployee()
        return empList
    }

    fun updateRecordDialog(employee: Employee) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.etUpdateName.setText(employee.name)
        updateDialog.etUpdateEmailId.setText(employee.email)

        updateDialog.tvUpdate.setOnClickListener {
            val name = updateDialog.etUpdateName.text.toString().trim()
            val email = updateDialog.etUpdateEmailId.text.toString().trim()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            if (name.isNotEmpty() && email.isNotEmpty()) {
                val status = databaseHandler.updateEmployee(Employee(employee.id, name, email))

                if (status > -1) {
                    Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_SHORT).show()
                    setupListDataIntoRecyclerView()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or email cannot be blank",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        updateDialog.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    fun deleteRecordAlertDialog(emp: Employee) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure you want to delete ${emp.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteEmployee(Employee(emp.id, "", ""))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()
                setupListDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}