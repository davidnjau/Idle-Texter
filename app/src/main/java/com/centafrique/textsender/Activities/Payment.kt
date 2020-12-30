package com.centafrique.textsender.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.centafrique.textsender.Helperclass.CheckPhoneNumber
import com.centafrique.textsender.R
import com.centafrique.textsender.mpesa.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import net.cachapa.expandablelayout.ExpandableLayout
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Url
import java.util.*


class Payment : AppCompatActivity(), RewardedVideoAdListener {

    private lateinit var btnActivatePlan: Button
    private lateinit var etMpesaCode: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var progressDialog: ProgressDialog

    private var liveAccessToken = ""
    private lateinit var apiInterface1: InterFaces
    private lateinit var stringStringMap: HashMap<String, String>
    private lateinit var etPhoneNUmber:EditText
    private lateinit var database : DatabaseReference

    private var phoneNumber = ""
    private var baseUrl = ""

    private lateinit var expanded_menu_payment : ExpandableLayout
    private lateinit var expanded_menu_add : ExpandableLayout

    private lateinit var checkPhoneNumber: CheckPhoneNumber

    private lateinit var mRewardedVideoAd : RewardedVideoAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this

        loadRewardVideo()

        stringStringMap = HashMap()
        baseUrl = resources.getString(R.string.mpesa_base_url)
        checkPhoneNumber = CheckPhoneNumber()

        etPhoneNUmber = findViewById(R.id.etPhoneNUmber)
        expanded_menu_payment = findViewById(R.id.expanded_menu_payment)
        expanded_menu_add = findViewById(R.id.expanded_menu_add)

        btnActivatePlan = findViewById(R.id.btnActivatePlan)
        etMpesaCode = findViewById(R.id.etMpesaCode)

        progressDialog = ProgressDialog(this)

        sharedPreferences = applicationContext.getSharedPreferences("payments", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val sms = sharedPreferences.getString("sms", null)

        findViewById<TextView>(R.id.tvPayment).setOnClickListener {

            if (expanded_menu_payment.isExpanded) {
                expanded_menu_payment.collapse()
            }else {
                expanded_menu_payment.expand()
            }

            if (expanded_menu_add.isExpanded)
                expanded_menu_add.collapse()

        }

        findViewById<Button>(R.id.btnViewAdd).setOnClickListener {

            if (mRewardedVideoAd.isLoaded) {
                mRewardedVideoAd.show()
            }else {

                CoroutineScope(Dispatchers.Main).launch {

                    val job = Job()
                    CoroutineScope(Dispatchers.IO + job).launch {

                        loadRewardVideo()
                        delay(3000)

                    }.join()
                    if (mRewardedVideoAd.isLoaded)
                        mRewardedVideoAd.show()

                }

            }
        }


        findViewById<LinearLayout>(R.id.linear).setOnClickListener {

            if (expanded_menu_add.isExpanded)
                expanded_menu_add.collapse()
            else
                expanded_menu_add.expand()

            if (expanded_menu_payment.isExpanded)
                expanded_menu_payment.collapse()

        }

        database = FirebaseDatabase.getInstance().reference
        val myRef = database.child("payments")

        btnActivatePlan.setOnClickListener {

            progressDialog.setTitle("Please wait..")
            progressDialog.setMessage("Processing payment..")
            progressDialog.setCanceledOnTouchOutside(false)

            val mpesaCode = etMpesaCode.text.toString()
            if (!TextUtils.isEmpty(mpesaCode)){

                progressDialog.show()

                myRef.orderByChild("mpesa_code").equalTo(mpesaCode)
                        .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.exists()){

                            for (ds in p0.children) {

                                val amount = ds.child("amount").getValue(String::class.java)
                                val usage = ds.child("usage").getValue(String::class.java)

                                if (usage == "inactive") {

                                    editor.putString("sms", amount)
                                    editor.apply()

                                    myRef.child(mpesaCode).child("usage").setValue("active")

                                    //exists
                                    val intent = Intent(this@Payment, Main2Activity::class.java)
                                    startActivity(intent)

                                }else{

                                    Toast.makeText(this@Payment, "The M-PESA code is already in use by another person", Toast.LENGTH_LONG).show()
                                    progressDialog.dismiss()


                                }

                            }


                        }else{

                            progressDialog.dismiss()
                            Toast.makeText(this@Payment, "Please wait while we update the payment. Try later on", Toast.LENGTH_LONG).show()


                        }

                    }

                })


            }else{

                etMpesaCode.error = "You cannot proceed without payment."

            }

        }

        loadToken()



    }

    private fun loadRewardVideo() {

        val addId = this.resources.getString(R.string.adId)

        CoroutineScope(Dispatchers.Main).launch {
            mRewardedVideoAd.loadAd(addId, AdRequest.Builder().build())

        }

    }


    fun Payment(view: View) {

        progressDialog.setTitle("Please wait..")
        progressDialog.setMessage("Processing payment..")
        progressDialog.setCanceledOnTouchOutside(false)

        progressDialog.show()

        var amount = 0
        var plan = 0

        if (view.id == R.id.silver){
            //500sms 200/=
            amount = 200
            plan = 500


        }else if (view.id == R.id.bronze){
            //750sms 500/=
            amount = 500
            plan = 750


        }else if (view.id == R.id.gold){
            //1000sms 750/=
            amount = 750
            plan = 1000

        }else if (view.id == R.id.premium){
            //1700sms 1000/=
            amount = 1000
            plan = 1700
        }

        initiatelipaNaMpesa(amount, plan)

    }

    private fun initiatelipaNaMpesa(amount: Int, plan: Int) {

        stringStringMap["Authorization"] = "Bearer $liveAccessToken"
        val amnt = amount.toString()

        val newNumber :String = etPhoneNUmber.text.toString()

        if (!TextUtils.isEmpty(newNumber)){


            val lipaNaMpesa = LipaNaMpesa(newNumber,
                    "$plan",
                    "ZGF2aWxsYW1pbG5lckBnbWFpbC5jb205cWJRRGFKaHBHQ0F3OTVNTjVxZ3pjVEdQQVdNUXhRSTAuNTU1NjI4MDAgMTYwNDk5OTA0Mw==",
                    amnt,
                    "IDT")

            apiInterface1 = APIClientJava.getClient(baseUrl).create(InterFaces::class.java)
            val myRef = database.child("payments")

            val call: Call<PaymentResponse> = apiInterface1.lipaNaMpesa(stringStringMap, lipaNaMpesa).also {
                it.enqueue(object: Callback<PaymentResponse> {
                    override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                        progressDialog.dismiss()

                        Toast.makeText(applicationContext, "Please try again..", Toast.LENGTH_LONG).show()

                    }

                    override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {

                        if (response.isSuccessful) {


                            val resultCode = response.body()?.message
                            if (resultCode != null){

                            val amnt:String = response.body()?.message?.amount.toString()
                            val checkOutId:String = response.body()?.message?.checkout_req_id.toString()

                            myRef.child(checkOutId).child("paid_amount").setValue(amnt)
                            myRef.child(checkOutId).child("amount").setValue(plan.toString())
                            myRef.child(checkOutId).child("mpesa_code").setValue(checkOutId)
                            myRef.child(checkOutId).child("usage").setValue("active")

                            editor.putString("sms", plan.toString())
                            editor.apply()

                            Toast.makeText(applicationContext, "Successful payment. Your subscription of $plan sms is now active", Toast.LENGTH_LONG).show()

                            val intent = Intent(this@Payment, Main2Activity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()


                            }else{
                                progressDialog.dismiss()

//                            val resultDesc:String = response.body()?.message.toString()
//                            Toast.makeText(applicationContext, resultDesc, Toast.LENGTH_LONG).show()
                                Toast.makeText(applicationContext, "Please try again..", Toast.LENGTH_LONG).show()

                            }

                        } else {
                            progressDialog.dismiss()

//                            val errorMessage = response.errorBody()?.string().toString()
//                            val jsonObject = JSONObject(errorMessage)
//                            val resultMessage = jsonObject.getJSONObject("message").getString("message")
//                            Toast.makeText(applicationContext, resultMessage, Toast.LENGTH_LONG).show()

                            Toast.makeText(applicationContext, "Please try again..", Toast.LENGTH_LONG).show()

//                            Log.e("DemoClass1", "Error: ${response.errorBody()}")
                        }
                    }
                })
            }
        }else
            Toast.makeText(applicationContext, "Phone number cannot be empty.", Toast.LENGTH_LONG).show()





    }

    fun loadToken() {

        val userLogin = UserLogin("davillamilner@gmail.com", "123456789")

        apiInterface1 = APIClientJava.getClient(baseUrl).create(InterFaces::class.java)
        val call : Call<TokenResponse> = apiInterface1.getAccessLiveToken(userLogin)
        call.enqueue(object: Callback<TokenResponse> {
            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("DemoClass", t.message, t)
            }

            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {

                    liveAccessToken = response.body()?.access_token.toString()

                } else {
                    Log.e("DemoClass", "Error: ${response.code()} ${response.message()}")
                }
            }
        })

        //Get Phone number

        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid.toString()

        val userData = database.child("users")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                phoneNumber = dataSnapshot.child(userId).child("phone_number").value.toString()
                etPhoneNUmber.setText(phoneNumber)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())

            }
        }
        userData.addValueEventListener(postListener)




    }



    override fun onRewardedVideoAdClosed() {
    }
    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "You cannot get the free sms plan if you've not watched the video ", Toast.LENGTH_SHORT).show()
    }
    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        Toast.makeText(this, "You cannot get the free sms plan if you've not watched the video ", Toast.LENGTH_SHORT).show()
    }
    override fun onRewarded(p0: RewardItem?) {
        editor.putString("sms", "20")
        editor.apply()
    }


    override fun onRewardedVideoAdLoaded() {

    }
    override fun onRewardedVideoAdOpened() {

    }
    override fun onRewardedVideoCompleted() {

    }

    override fun onRewardedVideoStarted() {

    }




}