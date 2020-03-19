package com.karthikeyan.myapplication.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.karthikeyan.myapplication.R
import com.karthikeyan.myapplication.dataInterface.ExpenseEntry
import kotlinx.android.synthetic.main.item_expense.view.*

class ExpenseListAdapter(val expenseList:ArrayList<ExpenseEntry>,val context:Context) : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_expense,p0,false))
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val expense :ExpenseEntry = expenseList.get(p1)
        p0.expenseLabelTV.text = expense.type
        p0.expenseTV.text = expense.expense.toString()
    }

     fun addExpenseList(expenseLst:ArrayList<ExpenseEntry>){
        expenseList.clear()
        expenseList.addAll(expenseLst)
        notifyDataSetChanged()
    }
     fun addExpense(expense:ExpenseEntry){
        expenseList.add(expense)
        notifyItemChanged(expenseList.size-1)
    }
    class ViewHolder(view:View) : RecyclerView.ViewHolder(view){

        var expenseLabelTV :TextView=view.findViewById(R.id.labelExpenseTV)
        var expenseTV:TextView = view.findViewById(R.id.expenseTV)


    }
}