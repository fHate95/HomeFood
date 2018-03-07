package com.fhate.homefood.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ScrollView
import com.fhate.homefood.R
import com.fhate.homefood.util.Repository
import com.fhate.homefood.util.Tools
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_review.*
import kotlinx.android.synthetic.main.toolbar.*

/* Фрагмент "О нас" + отправка отзыва */
class ReviewFragment : Fragment() {

    private val tools: Tools
            by lazy(LazyThreadSafetyMode.NONE) { Tools(activity) }
    private val repo: Repository
            by lazy(LazyThreadSafetyMode.NONE) { Repository(activity) }

    private lateinit var database: FirebaseDatabase
    private lateinit var reviewReference: DatabaseReference
    private lateinit var valueReference: DatabaseReference

    private var currentReviewTag = "0"

    private var autoCompleteNameList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /* Создание View */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        (activity as AppCompatActivity).setSupportActionBar(activity.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        return view
    }

    /* View создан */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance()
        reviewReference = database.getReference(repo.TAG_REVIEWS)
        valueReference = database.getReference(repo.TAG_VALUES)

        autoCompleteNameList = repo.getAutoCompleteList(repo.TAG_AUTOCOMPLETE_NAME)
        etName.setAdapter(ArrayAdapter(activity, android.R.layout.simple_dropdown_item_1line, autoCompleteNameList))

        /* Получаем данные "о нас" */
        valueReference.child(repo.TAG_ABOUT).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val value = dataSnapshot.getValue(String::class.java).toString()
                    tvAbout.text = value
                    pBar.visibility = View.INVISIBLE
                    reviewScrollView.visibility = View.VISIBLE
                } catch (E: NullPointerException) {

                } catch (e: TypeCastException) {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("db", "Failed to read value.", error.toException())
            }
        })

        /* Получаем кол-во отзывов для формирования тэга.
        * Например при n = 3 (0,1,2) следующий тэг будет равен "3" */
        FirebaseDatabase.getInstance().reference.child(repo.TAG_REVIEWS).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val map = dataSnapshot.value as ArrayList<Any>
                            //var items = ArrayList<String>(map.keys)

                            currentReviewTag = map.size.toString()

                        } catch (e: TypeCastException) {
                            tools.makeToast(resources.getString(R.string.error))
                        }
                        catch (E: NullPointerException) {
                            tools.makeToast(resources.getString(R.string.error))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        tools.makeToast(resources.getString(R.string.error))
                    }
                })

        /* Обработчик клика по кнопке "Оставить отзыв"
        * анимированно покажем view и скроем кнопку */
        buttonReview.setOnClickListener {
            llReview.startAnimation(tools.fadeInAnim)
            llReview.visibility = View.VISIBLE

            buttonReview.startAnimation(tools.fadeOutAnim)
            buttonReview.visibility = View.INVISIBLE
        }

        /* Обработчик клика по полю "Имя"
         * покажем drop menu */
        etName.setOnClickListener {
            etName.showDropDown()
        }

        /* Обработчик клика по полю "Отзыв"
         * скроллим вниз */
        etReview.setOnClickListener {
            reviewScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        /* Обработчик клика по кнопке "отправить"
         * отправляем данные в бд */
        buttonReviewDone.setOnClickListener {
            buttonReviewDone.startAnimation(tools.clickAnim)

            if (isFieldsFilled()) { //если все поля заполнены
                reviewReference.child(currentReviewTag).child(repo.TAG_NAME).setValue(etName.text.toString())
                reviewReference.child(currentReviewTag).child(repo.TAG_REVIEW).setValue(etReview.text.toString())
                saveAutoCompleteNameList(etName.text.toString())
                tools.makeToast(resources.getString(R.string.review_done))
                etName.setText("")
                etReview.setText("")
                activity.onBackPressed()
            } else {
                tools.makeToast(resources.getString(R.string.error_fields))
                activity.onBackPressed()
            }
        }
    }

    /* Проверяем все ли поля заполнены */
    private fun isFieldsFilled() : Boolean {
        var res = true
        when {
            etName.text.isEmpty() -> res = false
            etReview.text.isEmpty() -> res = false
        }

        return res
    }

    /* Сохраним AutoCompleteTV data */
    private fun saveAutoCompleteNameList(item: String) {
        if (!autoCompleteNameList.contains(item)) {
            autoCompleteNameList.add(item)
            repo.setAutoCompleteList(autoCompleteNameList, repo.TAG_AUTOCOMPLETE_NAME)
        }
    }

}
