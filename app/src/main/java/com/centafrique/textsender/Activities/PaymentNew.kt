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
import com.centafrique.textsender.Helperclass.CustomDialogMessage
import com.centafrique.textsender.Helperclass.ShowMessage
import com.centafrique.textsender.R
import com.centafrique.textsender.mpesa.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_payment_new.*
import kotlinx.android.synthetic.main.activity_payment_new.adView
import kotlinx.coroutines.*
import net.cachapa.expandablelayout.ExpandableLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PaymentNew : AppCompatActivity(), RewardedVideoAdListener{

    private lateinit var btnActivatePlan: Button
    private var paymentMethod = ""
    private lateinit var etPhoneNUmber: EditText
    private lateinit var progressDialog: ProgressDialog
    private var baseUrl = ""
    private lateinit var apiInterface1: InterFaces
    private var liveAccessToken = ""
    private lateinit var stringStringMap: HashMap<String, String>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mRewardedVideoAd : RewardedVideoAd

    private lateinit var expanded_menu_payment : ExpandableLayout
    private lateinit var expanded_menu_add : ExpandableLayout

    private lateinit var mInterstitialAd: InterstitialAd

    private var liveRewardVideo = "ca-app-pub-2341333181404752/7164436318"
    private var testRewardVideo = "ca-app-pub-3940256099942544/5224354917"

    private var liveInterstitialAd = "ca-app-pub-2341333181404752/7889707501"
    private var testInterstitialAd = "ca-app-pub-3940256099942544/1033173712"

    private var isLiveReward = false

    private var isInterstitialAdLoaded = false
    private var isInterstitialAdOpened = false
    private var isInterstitialAdClicked = false
    private var isInterstitialAdClosed = false

    private var rewardIntestial = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_new)

//        setAdId(liveInterstitialAd)

        stringStringMap = HashMap()
        btnActivatePlan = findViewById(R.id.btnActivatePlan)
        etPhoneNUmber = findViewById(R.id.etPhoneNUmber)
        progressDialog = ProgressDialog(this)
        baseUrl = resources.getString(R.string.mpesa_base_url)
        apiInterface1 = APIClientJava.getClient(baseUrl).create(InterFaces::class.java)

        sharedPreferences = applicationContext.getSharedPreferences("payments", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        expanded_menu_payment = findViewById(R.id.expanded_menu_payment)
        expanded_menu_add = findViewById(R.id.expanded_menu_add)

        findViewById<LinearLayout>(R.id.linear).setOnClickListener {

            if (expanded_menu_add.isExpanded)
                expanded_menu_add.collapse()
            else
                expanded_menu_add.expand()

            if (expanded_menu_payment.isExpanded)
                expanded_menu_payment.collapse()

        }
        findViewById<TextView>(R.id.tvPayment).setOnClickListener {

            if (expanded_menu_payment.isExpanded) {
                expanded_menu_payment.collapse()
            }else {
                expanded_menu_payment.expand()
            }

            if (expanded_menu_add.isExpanded)
                expanded_menu_add.collapse()

        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this

//        loadRewardVideo()


        loadInterstitialAdd()

        btnActivatePlan.setOnClickListener {

            val sdf = SimpleDateFormat("dd MM yyyy HH:mm")
            val currentDate = sdf.format(Date())
            Log.e("-*-*-*date ", currentDate)

            val txtPhoneNumber = etPhoneNUmber.text.toString()

            if (paymentMethod != "" && !TextUtils.isEmpty(txtPhoneNumber)){

                if (currentDate == txtPhoneNumber){
                    var plan = 0
                    var amnt = 0

                    when(paymentMethod){

                        "silver" -> {plan = 100; amnt = 200 }
                        "bronze" -> {plan = 150; amnt = 500}
                        "gold" -> {plan = 200; amnt = 750}
                        "premium" -> {plan = 250; amnt = 1000}
                    }
                    finaliseProcess(plan)

                }else{
                    makeMpesaPayment(txtPhoneNumber)
                }

            }else{

                if (TextUtils.isEmpty(txtPhoneNumber)) etPhoneNUmber.error = "Enter Phone number.."
                if (paymentMethod == "")Toast.makeText(this, "Select a payment plan first..", Toast.LENGTH_LONG).show()
            }

        }

        findViewById<Button>(R.id.btnViewAdd).setOnClickListener {

            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }else {
                progressDialog.setTitle("Please wait..")
                progressDialog.setMessage("Processing advert..")
                progressDialog.setCanceledOnTouchOutside(false)

                progressDialog.show()

                CoroutineScope(Dispatchers.Main).launch {

                    val job = Job()
                    CoroutineScope(Dispatchers.IO + job).launch {

                        loadInterstitialAdd()
//                        loadRewardVideo()
                        delay(3000)

                    }.join()
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                        progressDialog.dismiss()

                    }

                }

            }
        }

        mInterstitialAd.adListener = object :AdListener(){

            override fun onAdLoaded() {
                super.onAdLoaded()

                Log.e("-*-*-* ", "-*-*-*-")
            }

            override fun onAdClosed() {
                super.onAdClosed()

                CoroutineScope(Dispatchers.IO).launch {

                    rewardItem1(10, "")
                }

            }

            override fun onAdClicked() {
                super.onAdClicked()

                CoroutineScope(Dispatchers.IO).launch {

                    rewardItem1(15, "")
                }
            }

            override fun onAdFailedToLoad(error: LoadAdError?) {
                super.onAdFailedToLoad(error)

                if (error != null) {
                    Log.e("-*-*domain ", error.domain.toString())
                    Log.e("-*-*code ", error.code.toString())
                    Log.e("-*-*message ", error.message.toString())
                    Log.e("-*-*responseInfo ", error.cause.toString())
                    Log.e("-*-*cause ", error.cause.toString())

                    CustomDialogMessage(this@PaymentNew, error.code.toString())

                }

                progressDialog.dismiss()
            }

        }

//        mInterstitialAd.adListener = object: AdListener() {
//            override fun onAdLoaded() {
//                isInterstitialAdLoaded = true
//                rewardInterstitialItem()
//            }
//
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                // Code to be executed when an ad request fails.
//            }
//
//            override fun onAdOpened() {
//                // Code to be executed when the ad is displayed.
//                isInterstitialAdOpened = true
//                rewardInterstitialItem()
//            }
//
//            override fun onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//                isInterstitialAdClicked = true
//                rewardInterstitialItem()
//            }
//
//            override fun onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            override fun onAdClosed() {
//                isInterstitialAdClosed = true
//                rewardInterstitialItem()
//            }
//
//
//        }


    }

    private fun rewardInterstitialItem() {

        when {
            isInterstitialAdLoaded -> {
                rewardIntestial += 10
            }
            isInterstitialAdClicked -> {
                rewardIntestial += 15
            }
            isInterstitialAdClosed -> {
                rewardIntestial += 10
            }
            isInterstitialAdOpened -> {
                rewardIntestial += 15
            }
        }

        rewardItem(rewardIntestial, "")


    }

    private fun setAdId(adId: String) {
        adView.adUnitId = adId

        if (adId == liveRewardVideo) isLiveReward = true

    }

    fun loadInterstitialAdd() {

        MobileAds.initialize(this@PaymentNew)
        val adRequest = AdRequest.Builder().build()
        CoroutineScope(Dispatchers.Main).async { adView.loadAd(adRequest) }

        mInterstitialAd = InterstitialAd(this@PaymentNew)
        mInterstitialAd.adUnitId = liveInterstitialAd //For now use testInterstitial Ad id
        CoroutineScope(Dispatchers.Main).async { mInterstitialAd.loadAd(AdRequest.Builder().build()) }


    }

    private fun loadRewardVideo() {

        CoroutineScope(Dispatchers.Main).launch {
            mRewardedVideoAd.loadAd(testRewardVideo, AdRequest.Builder().build())

        }

    }

    private fun makeMpesaPayment(newNumber: String) {

        progressDialog.setTitle("Please wait..")
        progressDialog.setMessage("Processing payment..")
        progressDialog.setCanceledOnTouchOutside(false)

        progressDialog.show()

        stringStringMap["Authorization"] = "Bearer $liveAccessToken"

        var plan = 0
        var amnt = 0

        when(paymentMethod){

            "silver" -> {plan = 500; amnt = 200 }
            "bronze" -> {plan = 750; amnt = 500}
            "gold" -> {plan = 1000; amnt = 750}
            "premium" -> {plan = 1700; amnt = 1000}
        }



        val lipaNaMpesa = LipaNaMpesa(newNumber,
                "$plan",
                "WTd3ekY5ZUQ5REE1eDluTGZ0amdOb1UyYXZ2V3o5TkRicmlhbm1va2FuZHUzNUBnbWFpbC5jb20wLjgwODMyNDAwIDE2MDgxMjgyNTc=",
                amnt.toString(),
                "IDT")

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

                            finaliseProcess(plan)

                            progressDialog.dismiss()

                        }else{

                            progressDialog.dismiss()
                            Toast.makeText(applicationContext, "1Please try again..", Toast.LENGTH_LONG).show()

                        }

                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "1Please try again..", Toast.LENGTH_LONG).show()

                    }
                }
            })
        }



    }

    private fun finaliseProcess(plan: Int) {

        editor.putString("sms", plan.toString())
        editor.apply()

        Toast.makeText(applicationContext, "Successful payment. Your subscription of $plan sms is now active", Toast.LENGTH_LONG).show()

        val intent = Intent(this@PaymentNew, Main2Activity::class.java)
        startActivity(intent)
        finish()

    }

    fun Payment(view: View) {

        when(view.id){

            R.id.silver -> paymentMethod = "silver"
            R.id.bronze -> paymentMethod = "bronze"
            R.id.gold -> paymentMethod = "gold"
            R.id.premium -> paymentMethod = "premium"

        }

    }

    fun loadToken() {

        val userLogin = UserLogin("brianmokandu35@gmail.com", "ngojakiasi")

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



    }

    override fun onStart() {
        super.onStart()

        loadToken()
    }

    override fun onRewardedVideoAdClosed() {
        Toast.makeText(this, "Video closed. You cannot get the sms plan if you've not watched the video ", Toast.LENGTH_SHORT).show()

    }
    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "You've left the application. You cannot get the sms plan if you've not watched the video ", Toast.LENGTH_SHORT).show()
    }
    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        Toast.makeText(this, "Video not loaded. You cannot get the sms plan if you've not watched the video ", Toast.LENGTH_SHORT).show()

        setAdId(liveInterstitialAd)
        if (mInterstitialAd.isLoaded){
            mInterstitialAd.show()
        }

    }
    override fun onRewarded(p0: RewardItem?) {

        var smsNew = 20
        rewardItem(20, "")

    }

    override fun onRewardedVideoAdLoaded() {

    }
    override fun onRewardedVideoAdOpened() {

    }
    override fun onRewardedVideoCompleted() {

    }
    override fun onRewardedVideoStarted() {

    }

    //Interstitial Ad

    //Dynamic toast
    fun customMessage(message: String){

        ShowMessage(this@PaymentNew, message)

    }

    private fun rewardItem(smsNew: Int, additionalMessage: String) {

        var smsTotal: Int
        val sms = sharedPreferences.getString("sms", null)
        if (sms != null) {
            smsTotal = sms.toInt() + smsNew
            editor.putString("sms", "$smsTotal")
        }else{
            editor.putString("sms", "$smsNew")
        }
        editor.apply()

        customMessage("The application will send $smsNew messages. ")

        val intent = Intent(this@PaymentNew, Main2Activity::class.java)
        startActivity(intent)
        finish()


    }
    suspend fun rewardItem1(smsNew: Int, additionalMessage: String) {

        var smsTotal: Int
        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {

            val sms = sharedPreferences.getString("sms", null)
            if (sms != null) {
                smsTotal = sms.toInt() + smsNew
                editor.putString("sms", "$smsTotal")
            } else {
                editor.putString("sms", "$smsNew")
            }
            editor.apply()
            delay(1000)
        }.join()

        CoroutineScope(Dispatchers.Main).launch {
            customMessage("The application will send $smsNew messages. ")

            val intent = Intent(this@PaymentNew, Main2Activity::class.java)
            startActivity(intent)
            finish()
        }

    }

}