package com.example.a24012021063_practical_7

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.a24012021063_practical_7.databinding.PersonItemBinding

class PersonAdapter(
    private val context: Context,
    private val personList: ArrayList<Person>,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(
        val binding: PersonItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonViewHolder {

        val binding = PersonItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return personList.size
    }

    override fun onBindViewHolder(
        holder: PersonViewHolder,
        position: Int
    ) {

        val person = personList[position]

        holder.binding.txtName.text = person.name
        holder.binding.txtPhone.text = person.phoneNo
        holder.binding.txtEmail.text = person.emailId
        holder.binding.txtAddress.text = person.address
        holder.binding.btnDelete.setOnClickListener {

            databaseHelper.deletePerson(person.id)
            personList.removeAt(position)
            notifyItemRemoved(position)
            Toast.makeText(context,"Person Deleted",Toast.LENGTH_SHORT).show()
        }

    }
}