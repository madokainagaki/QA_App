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
import com.google.android.gms.common.util.ArrayUtils.contains
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
    var questionArrayList = ArrayList<String>()

    private lateinit var mAdapter: QuestionsListAdapter
    private lateinit var mQuestionRef: DatabaseReference
    private lateinit var mQuestionRef2: DatabaseReference
    val dataBaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var mQuestion: Question

    private val questionListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val mp = dataSnapshot.key.toString()

            val map = dataSnapshot.value as Map<*, *>
            val answers = arrayListOf<Answer>()
            val answerMap = map["answers"] as Map<*, *>?
            if (answerMap != null){
            for (key in answerMap.keys){
                    val answer = Answer("","","","")
                    answers.add(answer)
                }
            }

            //questionArrayList - これが選択されたジャンル内の全ての質問
            questionArrayList.add(mp)
            Log.d("test2-6", questionArrayList.toString())
            Log.d("test2-6map", map.toString())

            //favoriteArrayList - これがお気に入りされている質問
//            val list1 = favariteArrayList[0]
            Log.d("test2-66", favariteArrayList.toString())

//            val aaa = questionArrayList.contains(list1)
//            Log.d("test2-67",aaa.toString() )

            val question = Question(map["title"].toString(),"bbb",map["name"].toString(), "uid1", "questionUid1", 1, ByteArray(0), answers )

            for (i in favariteArrayList) {
                val list = questionArrayList.contains(i)
                if (list){
                    mQuestionArrayList.add(question)
                }

            }
            mAdapter.notifyDataSetChanged()

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

        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

        mAdapter.setQuestionArrayList(mQuestionArrayList)
        FavoriteListView.adapter = mAdapter

        mQuestionArrayList.clear()

        mQuestionRef = dataBaseReference.child(ContentsPATH).child(mGenre.toString())
        mQuestionRef.addChildEventListener(questionListener)

    }
}



