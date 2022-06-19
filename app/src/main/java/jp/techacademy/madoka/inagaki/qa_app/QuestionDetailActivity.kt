package jp.techacademy.madoka.inagaki.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBaseReference: DatabaseReference

    private var mIsFavorite = false

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    val dataBaseReference = FirebaseDatabase.getInstance().reference

            private val mEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val map = dataSnapshot.value as Map<*, *>

                    val answerUid = dataSnapshot.key ?: ""

                    for (answer in mQuestion.answers) {
                        if (answerUid == answer.answerUid) {
                            return
                        }
                    }

            val body = map["body"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val uid = map["uid"] as? String ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
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

    private val favoriteListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            mIsFavorite = true
            imageFavorite.setImageResource(R.drawable.ic_star)
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
        setContentView(R.layout.activity_question_detail)

        val extras = intent.extras
        mQuestion = extras!!.get("question") as Question
        title = mQuestion.title

        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        val userFavorite = FirebaseAuth.getInstance().currentUser
        if (userFavorite == null){
            imageFavorite.visibility = View.GONE
        }else{
            imageFavorite.visibility = View.VISIBLE
        }

        val uid = userFavorite?.uid
        val Favorite = dataBaseReference.child(FavoritePATH).child(uid.toString()).child(mQuestion.questionUid)
        Favorite.addChildEventListener(favoriteListener)

        imageFavorite.setOnClickListener{

            if (mIsFavorite) {
                Favorite.removeValue()
                imageFavorite.setImageResource(R.drawable.ic_star_border)
                mIsFavorite = false
            }else {
                val data = HashMap<String, Int>()
                data["userfavorite"] = 1
                Favorite.setValue(data)
                imageFavorite.setImageResource(R.drawable.ic_star)
                mIsFavorite = true
            }
        }

        fab.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(answersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }
}

