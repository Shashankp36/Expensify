package com.project.expensify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_customer.*
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.android.synthetic.main.activity_detailed.amountInput
import kotlinx.android.synthetic.main.activity_detailed.amountLayout
import kotlinx.android.synthetic.main.activity_detailed.descriptionInput
import kotlinx.android.synthetic.main.activity_detailed.labelInput
import kotlinx.android.synthetic.main.activity_detailed.labelLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)


        updateBtn.setOnClickListener{
            val label = labelInput.text.toString()
            val phonebox = phoneboxInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if (label.isEmpty())
                labelLayout.error ="Please enter a valid name"
            else if (amount == null)
                amountLayout.error = "please enter a valid amount"
            else {
                val transaction = Transaction(0,label,phonebox,amount, description)
                update(transaction)
            }
        }
    }
    private fun update(transaction: Transaction){
        val  db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()
        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
}
}