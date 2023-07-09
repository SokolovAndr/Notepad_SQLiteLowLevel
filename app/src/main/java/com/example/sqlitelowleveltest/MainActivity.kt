package com.example.sqlitelowleveltest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlitelowleveltest.db.MyAdapter
import com.example.sqlitelowleveltest.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val myDbManager = MyDbManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initSearchView()
    }

    override fun onDestroy() { //когда активити закрывается, то закрываем базу данных тоже
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() { //когда активити отрывается, то запускаем базу данных
        super.onResume()
        myDbManager.openDb()
        fillAdapter("")
    }

    fun onClickNew(view: View) {
        val i = Intent (this, EditActivity::class.java)  //Intent запускает новую активити!
        startActivity(i)

    }

    fun init(){
       var rcView = findViewById<RecyclerView>(R.id.rcView)
        rcView.layoutManager = LinearLayoutManager(this)  //элементы списка будут распологатьтся по вертикали
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcView)
        rcView.adapter = myAdapter   //подключаем адаптер
    }

    private fun initSearchView(){   //для поиска
        var searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                fillAdapter(p0!!)
                return true
            }
        })
    }


    private fun fillAdapter(text:String){

        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch{  //здесь запустили основной поток

            val list = myDbManager.readDbData(text)
            myAdapter.updateAdapter(list)
            var tvNoElements = findViewById<TextView>(R.id.tvNoElements)
            if(list.size > 0){
                tvNoElements.visibility = View.GONE
            }
            else {
                tvNoElements.visibility = View.VISIBLE
            }
        }
    }

    //для того чтобы свайпать
    private fun getSwapMg(): ItemTouchHelper {
        return ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){ //свайп вправо и влево

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)

            }
        })
    }


}