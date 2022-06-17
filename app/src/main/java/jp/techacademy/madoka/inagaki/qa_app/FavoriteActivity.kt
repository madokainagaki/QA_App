package jp.techacademy.madoka.inagaki.qa_app

import android.content.Intent
import android.graphics.Insets.add
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
// findViewById()を呼び出さずに該当Viewを取得するために必要となるインポート宣言
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class FavoriteActivity : AppCompatActivity() {

//    mQuestionArrayListにお気に入りを入れる
    var mQuestionArrayList = ArrayList<Question>()

    private lateinit var mAdapter: QuestionsListAdapter
    private lateinit var mQuestionRef: DatabaseReference
    private lateinit var mQuestionRef2: DatabaseReference
    val dataBaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var mQuestion: Question

    private val questionListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            val map = dataSnapshot.value as Map<*, *>
            val answers = arrayListOf<Answer>()
            val answerMap = map["answers"] as Map<*, *>?
            if (answerMap != null){
            for (key in answerMap.keys){
                    val answer = Answer("","","","")
                    answers.add(answer)
                }
            }

            val sp2 = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val name = sp2.getString(favoriteQuestion, "")
            Log.d("test8name2",name)

//            val question1 = Question(map["title"].toString(),"bbb",map["name"].toString(), "uid1", "questionUid1", 1, ByteArray(0), answers )
            val question1 = Question(map["title"].toString(),"bbb",map["name"].toString(), "uid1", "questionUid1", 1, ByteArray(0), answers )

            if (name != null) {
                val aaa = name.contains("-N4BEp5_jk2jMfHSIHPF")
                if (aaa){
                    mQuestionArrayList.add(question1)
                }
            }


//            Log.d("test3",question1.title)
//            mQuestionArrayList.add(question1)
            mAdapter.notifyDataSetChanged()

            val key = dataSnapshot.key ?: ""
//            Log.d("test2",key)
//            Log.d("test2",map["title"].toString())
        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        }


        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val userFavorite = FirebaseAuth.getInstance().currentUser
        val uid = userFavorite?.uid

        var mGenre =intent.getIntExtra("genre",0)
        Log.d("test2",mGenre.toString())

        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

//        var list1 = ArrayList<Question>()
//        val question1 = Question("list1","bbb","ccc", "uid1", "questionUid1", 1, ByteArray(0), arrayListOf<Answer>())
//        val question2 = Question("list2","bbb","ccc", "uid1", "questionUid1", 1, ByteArray(0), arrayListOf<Answer>())
//        val question3 = Question("list3","bbb","ccc", "uid1", "questionUid1", 1, ByteArray(0), arrayListOf<Answer>())
//        val question4 = Question("list4","bbb","ccc", "uid1", "questionUid1", 1, ByteArray(0), arrayListOf<Answer>())
//        val list1 : ArrayList<Question> = arrayListOf(question1,question2,question3,question4)
//        val list2 : ArrayList<Question> = arrayListOf(question1,question4)

//        val favoriteList : ArrayList<Question> = arrayListOf()
//        favoriteList.add(question1)
//        favoriteList.add(question2)
//        mQuestionArrayList.addAll(favoriteList)
//
//        //list2と比べる実装をする。
//
//
//        Log.d("test4",list1.size.toString())
//        Log.d("test4",list1.toString())
//        Log.d("test4",list2.toString())
//        Log.d("test4",list1.contains(question2).toString())
//        Log.d("test4",list2.contains(question2).toString())


        mAdapter.setQuestionArrayList(mQuestionArrayList)
        FavoriteListView.adapter = mAdapter

        mQuestionArrayList.clear()

        mQuestionRef = dataBaseReference.child(ContentsPATH).child(mGenre.toString())
        mQuestionRef.addChildEventListener(questionListener)

//        //ログイン中のユーザーidを取得する
//        val Favorite = dataBaseReference.child(FavoritePATH).child(uid.toString()).child(mQuestion.questionUid)
//        Log.d("test2",Favorite.toString())
    }
}



