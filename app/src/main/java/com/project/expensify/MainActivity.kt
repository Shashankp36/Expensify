package com.project.expensify

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions :List<Transaction>
    private lateinit var oldTransactions :List<Transaction>
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        //swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }

        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)

        drawerLayout  = findViewById(R.id.drawerlayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId){
                R.id.nav_home -> Toast.makeText(applicationContext,"Clicked Home",Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> replaceFragment(SettingFragment(), it.title.toString())
                R.id.nav_login -> replaceFragment(SignInFragment(), it.title.toString())
                R.id.nav_logout -> replaceFragment(SignOutFragment(), it.title.toString())
                R.id.nav_share -> Toast.makeText(applicationContext,"Clicked share",Toast.LENGTH_SHORT).show()
                R.id.nav_rate -> Toast.makeText(applicationContext,"Clicked rate",Toast.LENGTH_SHORT).show()
            }
                true

        }



        val button : Button = findViewById(R.id.add_customer)
        button.setOnClickListener{
            startActivity(Intent(this@MainActivity , CustomerActivity::class.java))

        }

    }

    private fun replaceFragment(fragment: Fragment, title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }


    private fun fetchAll(){
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()

            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun updateDashboard(){
        val totalAmount = transactions.map{ it.amount}.sum()
        val budgetAmount = transactions.filter{ it.amount>0}.map{it.amount}.sum()
        val expenseAmount = totalAmount - budgetAmount

        give_amount.text = "₹ %.2f".format(budgetAmount)
        get_amount.text = "₹ %.2f".format(expenseAmount)
        balance_amount.text = "₹ %.2f".format(totalAmount)
    }

    //----for making item functional when clicked----
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Navigation view open and close function
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private  fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id != transaction.id }
            runOnUiThread{
                updateDashboard()

            }
        }
    }
    override fun onResume() {
        super.onResume()
        fetchAll()
    }

}

