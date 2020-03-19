package com.karthikeyan.myapplication.dataInterface

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karthikeyan.myapplication.utils.DataConfig
import com.karthikeyan.myapplication.utils.Utils
import java.util.Date


data class CustomerProfile(val cid:Int, val cname:String="", val price:Double, var balEmpty:Int, var balAmt:Double)
data class SaleEntry(val doe: Date, val cid: Int, val scname: String?, val load:Int, val empty:Int, val amt:Double)

data class ExpenseEntry(val doe: Date, val type:String, val expense:Double)
data class LeakReturnEntry(val doe: Date,val totLoad: Int,val leak:Int,val returnCanes:Int)

@RequiresApi(Build.VERSION_CODES.P)
open class DataBaseHelper(context:Context): SQLiteOpenHelper(context, DATABASENAME, null,DATABASE_VERSION ) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_CUSTOMER_PROFILE)
        db?.execSQL(CREATE_TABLE_SALE_ENTRY)
        db?.execSQL(CREATE_TABLE_EXPENSE_ENTRY)
        db?.execSQL(CREATE_TABLE_LEAK_RETURN_ENTRY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
          db?.execSQL("Drop Table if exists "+ TABLE_CUSTOMER_PROFILE)
          db?.execSQL("Drop Table if exists "+ TABLE_SALES_ENTRY)
          db?.execSQL("Drop Table if exists "+ TABLE_EXPENSE_ENTRY)
          db?.execSQL("Drop Table if exists "+ TABLE_LEAK_RETURN_ENTRY)
        onCreate(db)

    }

    open fun getAllProfile():List<CustomerProfile> {
        if(DataConfig.fromJson){
            val jsonfile: String = Utils.fromJsonToString("profile.json")


            return Gson().fromJson(jsonfile, object :TypeToken<List<CustomerProfile>>(){}.type)

        }else{
            val customerList:ArrayList<CustomerProfile>
            customerList= ArrayList()
            val dbr=this.readableDatabase
            var cursor = dbr.rawQuery("Select * from "+ TABLE_CUSTOMER_PROFILE,null)
            if(cursor!=null&&cursor.count>0){
                cursor.moveToFirst()
                do{
                    val customer = CustomerProfile(cursor.getInt(cursor.getColumnIndex(KEY_CID)),
                    cursor.getString(cursor.getColumnIndex(KEY_CNAME)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_BAL_EMPTY)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_BAL_BALANCE)))
                    customerList.add(customer)
                }while (cursor.moveToNext())

            }
            cursor.close()
            cursor=null
            return customerList

        }
    return emptyList()
    }

    open fun getSalesDetails(cid: Int, fromDate: Date?, toDate: Date?):ArrayList<SaleEntry>{
        var saleList :ArrayList<SaleEntry> = ArrayList()
        val dbr=this.readableDatabase
        var cursor: Cursor? =null
        if(fromDate!=null && toDate!=null) {
            cursor = dbr.query(
                TABLE_SALES_ENTRY,
                arrayOf(KEY_DOE, KEY_CID, KEY_LOAD, KEY_EMPTY, KEY_AMT)
                ,
                "${KEY_CID} = ? AND ${KEY_DOE} >= ? AND ${KEY_DOE} <= ?"
                ,
                arrayOf(cid.toString(), fromDate!!.time.toString(), toDate!!.time.toString()),
                null,
                null,
                "${KEY_DOE} DESC",
                null
            )
        }else{
            cursor = dbr.query(
                TABLE_SALES_ENTRY,
                arrayOf(KEY_DOE, KEY_CID, KEY_LOAD, KEY_EMPTY, KEY_AMT)
                ,
                "${KEY_CID} = ?"
                ,
                arrayOf(cid.toString()),
                null,
                null,
                "${KEY_DOE} DESC",
                null
            )
        }
       Log.d("ss","count:"+cursor!!.count.toString())
        if(cursor.count>0){
            cursor.moveToFirst()
            do{
                val sale = SaleEntry(Date(cursor.getLong(cursor.getColumnIndex(KEY_DOE))),cursor.getInt(cursor.getColumnIndex(KEY_CID)),null,
                    cursor.getInt(cursor.getColumnIndex(KEY_LOAD)),cursor.getInt(cursor.getColumnIndex(KEY_EMPTY)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_AMT))
                    )
                saleList.add(sale)
                Log.d("ss","1")
            }while (cursor.moveToNext())
          cursor.close()
        }
        return saleList
    }

    open fun getDaySaleDetails(fromTime:Long ,toTime:Long):ArrayList<SaleEntry>{
        val saleList :ArrayList<SaleEntry> = ArrayList()
        val dbr=this.readableDatabase
        var cursor: Cursor? =null

//            cursor = dbr.query(
//                TABLE_SALES_ENTRY ,
//                arrayOf(KEY_DOE, KEY_CID, KEY_LOAD, KEY_EMPTY, KEY_AMT)
//                ,
//                "${KEY_CID} = ?"
//                ,
//                arrayOf(cid.toString()),
//                null,
//                null,
//                "${KEY_DOE} DESC",
//                null
//            )
        cursor = dbr.rawQuery("Select SalesEntry.cid, CustomerProfile.cname , "+ KEY_DOE+","+ KEY_LOAD+","+ KEY_EMPTY+","+ KEY_AMT+" from "+ TABLE_SALES_ENTRY+ " INNER JOIN "+ TABLE_CUSTOMER_PROFILE +" ON SalesEntry.cid=CustomerProfile.cid AND "+ KEY_DOE +">="+fromTime+" AND "+ KEY_DOE+"<="+toTime+" order by cname",null)
        Log.d("ss","count:"+cursor!!.count.toString())
        if(cursor.count>0){

            cursor.moveToFirst()
            Log.d("ss","cname:"+cursor.getString(cursor.getColumnIndex(KEY_CNAME)))

            do{
                val sale = SaleEntry(Date(cursor.getLong(cursor.getColumnIndex(KEY_DOE))),cursor.getInt(cursor.getColumnIndex(KEY_CID)),cursor.getString(cursor.getColumnIndex(KEY_CNAME)),
                    cursor.getInt(cursor.getColumnIndex(KEY_LOAD)),cursor.getInt(cursor.getColumnIndex(KEY_EMPTY)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_AMT))
                )
                saleList.add(sale)
                Log.d("ss","1")
            }while (cursor.moveToNext())
            cursor.close()
        }
        return saleList

    }
    open fun getExpenseDetails(fromTime:Long ,toTime:Long):ArrayList<ExpenseEntry>{
        val expenseList :ArrayList<ExpenseEntry> = ArrayList()
        val dbr=this.readableDatabase
        val cursor = dbr.query(TABLE_EXPENSE_ENTRY,
            arrayOf(KEY_DOE, KEY_TYPE, KEY_EXPENSE),
            "${KEY_DOE} >=? AND ${KEY_DOE} <=?  ",
            arrayOf(fromTime.toString(),toTime.toString()),
            null,null,"${KEY_DOE} DESC",null)
        Log.d("ss","count expense:"+cursor.count)
        if(cursor.count>0){

            cursor.moveToFirst()
            do{
                expenseList.add(ExpenseEntry(Date(cursor.getLong(cursor.getColumnIndex(KEY_DOE))),cursor.getString(cursor.getColumnIndex(
                    KEY_TYPE)),cursor.getDouble(cursor.getColumnIndex(KEY_EXPENSE))))

            }while (cursor.moveToNext())
            cursor.close()
        }

        return  expenseList

    }
    open fun addCustomer(customerProfile:CustomerProfile):Long{

           val dbw = this.writableDatabase
           val values = ContentValues().apply {
               put(KEY_CNAME, customerProfile.cname)
               put(KEY_PRICE, customerProfile.price)
               put(KEY_BAL_EMPTY,0)
               put(KEY_BAL_BALANCE,0)
           }
       val new_id= dbw.insert(TABLE_CUSTOMER_PROFILE, null, values)
        Log.d("ss",new_id.toString()+" inserted")
       return new_id
    }

    open fun addSalesEntry(saleEntry:SaleEntry,profile: CustomerProfile): Array<Any> {

         this.writableDatabase.insert(TABLE_SALES_ENTRY,null,ContentValues().apply {put(KEY_DOE,saleEntry.doe.time)
            put(KEY_CID,saleEntry.cid)
            put(KEY_LOAD,saleEntry.load)
        put(KEY_EMPTY,saleEntry.empty)
        put(KEY_AMT,saleEntry.amt)
        })
        return updateBalance(saleEntry,profile)

    }

    private fun updateBalance(saleEntry: SaleEntry,profile: CustomerProfile): Array<Any> {
        val balAmt = (profile.balAmt + (Math.abs(saleEntry.load * profile.price - saleEntry.amt)))
        val balEmpty = profile.balEmpty +(saleEntry.load - saleEntry.empty)
        this.writableDatabase.update(TABLE_CUSTOMER_PROFILE, ContentValues().apply {
           put(KEY_BAL_BALANCE, balAmt)
           put(KEY_BAL_EMPTY, balEmpty)

       },"${KEY_CID} = ?", arrayOf(profile.cid.toString()))
        return  arrayOf(balAmt,balEmpty)
    }

    open fun addExpenseEntry(expenseEntry: ExpenseEntry):Long{
        return this.writableDatabase.insert(TABLE_EXPENSE_ENTRY,null,ContentValues().apply{put(KEY_DOE,expenseEntry.doe.time)
        put(KEY_TYPE,expenseEntry.type)
            put(KEY_EXPENSE,expenseEntry.expense)})
    }

    open fun addLeakReturnEntry(leak:LeakReturnEntry):Long{
        return this.writableDatabase.insert(TABLE_EXPENSE_ENTRY,null,ContentValues().apply { put(KEY_DOE,leak.doe.time)
        put(KEY_LEAK,leak.leak)
        put(KEY_RETURN,leak.returnCanes)})
    }

    companion object{
        //database name
        const val DATABASENAME:String ="CaneLedger.db"
        const val DATABASE_VERSION:Int=3

        //table name
        const val TABLE_CUSTOMER_PROFILE="CustomerProfile"
        const val TABLE_SALES_ENTRY = "SalesEntry"
        const val TABLE_EXPENSE_ENTRY="ExpenseEntry"
        const val TABLE_LEAK_RETURN_ENTRY="LeakReturnEntry"

        //Customer table key
        const val KEY_CID="cid"
        const val KEY_CNAME="CNAME"
        const val KEY_PRICE="PRICE"
        const val KEY_BAL_EMPTY="BAL_EMPTY"
        const val KEY_BAL_BALANCE="BALANCE"

        //sale entry table key
        const val KEY_DOE="doe"
        const val KEY_LOAD="load"
        const val KEY_EMPTY="empty"
        const val KEY_AMT="amt"

        //expense entry table key

        const val KEY_TYPE="type"
        const val KEY_EXPENSE="expense"

        //Leak return table key
        const val KEY_LEAK="leak"
        const val KEY_RETURN="return"
        const val KEY_TOTLoad="totLoad"

        const val CREATE_TABLE_CUSTOMER_PROFILE="CREATE TABLE IF NOT EXISTS "+ TABLE_CUSTOMER_PROFILE+" ("+ KEY_CID+" Integer PRIMARY KEY AUTOINCREMENT,"+
         KEY_CNAME+" Text,"+ KEY_PRICE+" Real, ${KEY_BAL_EMPTY} Integer, ${KEY_BAL_BALANCE} Real)"
        const val CREATE_TABLE_SALE_ENTRY="CREATE TABLE IF NOT EXISTS "+ TABLE_SALES_ENTRY+ "("+ KEY_DOE+" Integer,"+ KEY_CID+" Integer,"+
                KEY_LOAD+" Integer,"+ KEY_EMPTY+" Integer,"+ KEY_AMT+" Integer)"

        const val CREATE_TABLE_EXPENSE_ENTRY="CREATE TABLE IF NOT EXISTS "+ TABLE_EXPENSE_ENTRY+" ("+ KEY_DOE+" Integer,"+
                KEY_TYPE+" Text,"+ KEY_EXPENSE+" Real)"

        const val CREATE_TABLE_LEAK_RETURN_ENTRY="CREATE TABLE IF NOT EXISTS "+ TABLE_LEAK_RETURN_ENTRY+"("+ KEY_DOE+" Integer,"+
                KEY_TOTLoad+" Integer,"+ KEY_LEAK+" Integer,"+ KEY_RETURN+" Integer)"



    }

}