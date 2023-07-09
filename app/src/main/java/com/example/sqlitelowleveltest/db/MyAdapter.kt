package com.example.sqlitelowleveltest.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlitelowleveltest.EditActivity
import com.example.sqlitelowleveltest.R

class MyAdapter(listMain:ArrayList<ListItem>, contextM: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    var listArray = listMain  //переменная для передачи в getItemCount
    var context = contextM

    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {

        val tvTitle:TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime:TextView = itemView.findViewById(R.id.tvTime)
        val context = contextV

        fun setData(item:ListItem){

            tvTitle.text = item.title
            tvTime.text = item.time
            itemView.setOnClickListener{

                val intent = Intent(context, EditActivity::class.java).apply {

                    putExtra(MyIntentConstants.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstants.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstants.I_ID_KEY, item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyHolder {   //МОЖЕТ БЫТЬ ОШИБКА, ПРОВЕРИТЬ ПОТОМ. Берем шаблон rc_item и готовим его для рисования и создаем МайХолдер
        val inflater = LayoutInflater.from(parent.context)      //inflater - это уже вшитый специальный класс для работы с xml файлом. inflate - надувать!
        return MyHolder(inflater.inflate(R.layout.rc_item, parent, false), context)
    }

    override fun getItemCount(): Int {  //здесь говорим сколько элементов будет в прокручиваемом списке RecyclerView. Сюда передаем размер массива
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyAdapter.MyHolder, position: Int) { //МОЖЕТ БЫТЬ ОШИБКА, ПРОВЕРИТЬ ПОТОМ. Подключает данные массива для заполнения
        holder.setData(listArray.get(position))
    }

    fun updateAdapter(listItems:List<ListItem>){

        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()   //для обновления адаптера

    }

    fun removeItem(pos:Int, dbManager: MyDbManager){  //удаление по свайпу


        dbManager.removeItemFromDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)

    }

}