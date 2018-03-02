package com.fhate.homefood.ui.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Toast
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import com.fhate.homefood.R
import com.fhate.homefood.model.CartItem
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import kotlinx.android.synthetic.main.activity_order.*
import java.util.*
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.R.array
import android.provider.ContactsContract
import android.util.Log
import com.fhate.homefood.model.User
import com.google.firebase.database.*
import kotlin.collections.ArrayList


class OrderActivity : AppCompatActivity() {

    private var mailUsername = "homefood.kirov@gmail.com"
    private var mailPassword = ""
    private var mailTo = "f.hate95@yandex.ru"
    private var mailSubject = ""
    private var mailBody = ""

    private lateinit var database: FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private lateinit var valueReference: DatabaseReference
    private lateinit var user: User

    private var orderNumber: Long = 1

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(this@OrderActivity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(this@OrderActivity) }

    private var cartList = ArrayList<CartItem>()
    private var autoCompleteNameList = ArrayList<String>()
    private var autoCompleteAddressList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        database = FirebaseDatabase.getInstance()
        userReference = database.getReference(repo.TAG_USERS)
        valueReference = database.getReference(repo.TAG_VALUES)

        getGMailAccountData()

        etNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        setAutoComplete()
        etName.setOnClickListener {
            etName.showDropDown()
        }
        etAddress.setOnClickListener {
            etAddress.showDropDown()
        }

        buttonOrder.setOnClickListener { v ->
            if (isFildsFilled()) {
                user = User(etName.text.toString(), etAddress.text.toString(), etNumber.text.toString(), 1, tools.getCartPrice())

                userReference.child(user.number).child(repo.TAG_ORDER_COUNT).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val value = dataSnapshot.getValue(Long::class.java).toString()
                            user.orderCount = value.toLong() + 1
                        } catch (E: NullPointerException) {

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("db", "Failed to read value.", error.toException())
                    }
                })

                userReference.child(user.number).child(repo.TAG_TOTAL_PRICE).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val value = dataSnapshot.getValue(Long::class.java).toString()
                            user.totalPrice = value.toLong() + user.totalPrice
                        } catch (E: NullPointerException) {

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("db", "Failed to read value.", error.toException())
                    }
                })

                tools.hideKeyBoard(v, this@OrderActivity)
                buttonOrder.startAnimation()
                setMailBody()
                sendEmail()
                saveAutoCompleteNameList(etName.text.toString())
                saveAutoCompleteAddressList(etAddress.text.toString())
            }
            else {
                tools.makeToast(resources.getString(R.string.error_fields))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        buttonOrder.dispose()
    }

    private fun isFildsFilled() : Boolean {
        var res = true
        when {
            etName.text.isEmpty() -> res = false
            etAddress.text.isEmpty() -> res = false
            etNumber.text[etNumber.text.length - 1] == '*' -> res = false
        }

        return res
    }

    private fun setMailBody() {
        cartList = repo.getCartList()
        var allPrice: Long = 0

        mailSubject += resources.getString(R.string.order) + " №" + orderNumber
        mailBody += resources.getString(R.string.name) + ": " + user.name + "\n" +
                resources.getString(R.string.address) + ": " + user.address + "\n" +
                resources.getString(R.string.number) + ": " + user.number + "\n" +
                resources.getString(R.string.order) + ": " + "\n"

        for (i in 0 until cartList.size) {
            mailBody += cartList[i].name + " " + "(" + cartList[i].count + " " + resources.getString(R.string.count) + "." +")" + "\n"
            allPrice += cartList[i].price * cartList[i].count
        }

        mailBody += resources.getString(R.string.to_pay) + ": " + allPrice + resources.getString(R.string.ruble_sign)
    }

    private fun getGMailAccountData() {
        valueReference.child(repo.TAG_MAIL_USERNAME).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val value = dataSnapshot.getValue(String::class.java).toString()
                    mailUsername = value
                } catch (E: NullPointerException) {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("db", "Failed to read value.", error.toException())
            }
        })

        valueReference.child(repo.TAG_MAIL_PASSWORD).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val value = dataSnapshot.getValue(String::class.java).toString()
                    mailPassword = value
                } catch (E: NullPointerException) {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("db", "Failed to read value.", error.toException())
            }
        })

        valueReference.child(repo.TAG_MAIL_TO).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val value = dataSnapshot.getValue(String::class.java).toString()
                    mailTo = value
                } catch (E: NullPointerException) {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("db", "Failed to read value.", error.toException())
            }
        })

        valueReference.child(repo.TAG_ORDER_NUMBER).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val value = dataSnapshot.getValue(Long::class.java).toString()
                    orderNumber = value.toLong() + 1
                } catch (E: NullPointerException) {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("db", "Failed to read value.", error.toException())
            }
        })
    }

    private fun sendEmail() {
        BackgroundMail.newBuilder(this)
                .withUsername(mailUsername)
                .withPassword(mailPassword)
                .withSenderName(resources.getString(R.string.app_name))
                .withMailTo(mailTo)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject(mailSubject)
                .withBody(mailBody)
                .withProcessVisibility(false)
                .withOnSuccessCallback(BackgroundMail.OnSuccessCallback {
                    saveUserData()
                    buttonOrder.doneLoadingAnimation(ContextCompat.getColor(this, R.color.colorSuccess),
                            BitmapFactory.decodeResource(resources, R.drawable.ico_done))
                    val handler = Handler()
                    handler.postDelayed({
                        this@OrderActivity.finish()
                    }, 1000)
                })
                .withOnFailCallback(BackgroundMail.OnFailCallback {
                    buttonOrder.doneLoadingAnimation(ContextCompat.getColor(this, R.color.colorError),
                            BitmapFactory.decodeResource(resources, R.drawable.ico_error))
                    tools.makeToast(resources.getString(R.string.error))
                    val handler = Handler()
                    handler.postDelayed({
                        this@OrderActivity.finish()
                    }, 1000)
                })
                .send()
    }

    private fun saveUserData() {
        userReference.child(user.number).child(repo.TAG_NAME).setValue(user.name)
        userReference.child(user.number).child(repo.TAG_ADDRESS).setValue(user.address)
        userReference.child(user.number).child(repo.TAG_ORDER_COUNT).setValue(user.orderCount)
        userReference.child(user.number).child(repo.TAG_TOTAL_PRICE).setValue(user.totalPrice)
        valueReference.child(repo.TAG_ORDER_NUMBER).setValue(orderNumber)
    }

    private fun setAutoComplete() {
        autoCompleteNameList = repo.getAutoCompleteList(repo.TAG_AUTOCOMPLETE_NAME)
        autoCompleteAddressList = repo.getAutoCompleteList(repo.TAG_AUTOCOMPLETE_ADDRESS)
        etName.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteNameList))
        etAddress.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteAddressList))
    }

    private fun saveAutoCompleteNameList(item: String) {
        if (!autoCompleteNameList.contains(item)) {
            autoCompleteNameList.add(item)
            repo.setAutoCompleteList(autoCompleteNameList, repo.TAG_AUTOCOMPLETE_NAME)
        }
    }

    private fun saveAutoCompleteAddressList(item: String) {
        if (!autoCompleteAddressList.contains(item)) {
            autoCompleteAddressList.add(item)
            repo.setAutoCompleteList(autoCompleteAddressList, repo.TAG_AUTOCOMPLETE_ADDRESS)
        }
    }
}