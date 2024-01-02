package com.project.expensify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_customer.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        labelInput.addTextChangedListener{

            if(it!!.count()>0)
                labelLayout.error = null
        }
        amountInput.addTextChangedListener{

            if(it!!.count()>0)
                amountLayout.error = null
        }

        confirm_btn.setOnClickListener{
            val label = labelInput.text.toString()
            val phonebox = phoneboxInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isEmpty())
                labelLayout.error ="Please enter a valid name"
            else if (amount == null)
                amountLayout.error = "please enter a valid amount"
            else {
                val transaction =Transaction(0,label,phonebox,amount, description)
                insert(transaction)
            }
        }
    }
    private fun insert(transaction: Transaction){
        val  db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()
        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}