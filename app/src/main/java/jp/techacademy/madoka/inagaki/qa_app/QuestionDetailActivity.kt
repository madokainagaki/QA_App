package jp.techacademy.madoka.inagaki.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.list_question_detail.*
import kotlinx.android.synthetic.main.list_questions.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBaseReference: DatabaseReference
//質問の詳細画面――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――●

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    val dataBaseReference = FirebaseDatabase.getInstance().reference


    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {


            val map = dataSnapshot.value as Map<*, *>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)


        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras!!.get("question") as Question
        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        //クリックで変動するようにした。後はfirebaseを使用する。
        imageFavorite.setImageResource(R.drawable.ic_star)
        // アカウント作成の時は表示名をFirebaseに保存する

        val userFavorite = FirebaseAuth.getInstance().currentUser
        val uid = userFavorite?.uid
        //ログイン中のユーザーidを取得する
        val Favorite = dataBaseReference.child(FavoritePATH).child(uid.toString()).child(mQuestion.questionUid)

        imageFavorite.setOnClickListener{


            //↓uidは質問者のidになってしまうのでログイン中のidに直す必要がある
            //val Favorite = dataBaseReference.child(FavoritePATH).child(mQuestion.uid).child(mQuestion.questionUid)

            val data = HashMap<String, Int>()
            data["userfavorite"] = 1
            //setValueメソッドはkeyにvalueを保存する場合に使用
            Favorite.setValue(data)
        }


        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser


            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }
//
//        imageFavorite.setOnClickListener{
//            Log.d("test","だっふんだ")
//        }


        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(answersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }
}

