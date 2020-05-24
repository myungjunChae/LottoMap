package com.ono.lotto_map.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.google.gson.Gson
import com.ono.lotto_map.R
import com.ono.lotto_map.application.MyApplication
import com.ono.lotto_map.application.PREF_NAME
import com.ono.lotto_map.application.PreferenceModel
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.data.model.toEntity
import com.ono.lotto_map.data.remote.StoreInfoResponse
import com.ono.lotto_map.data.remote.toModel
import com.ono.lotto_map.databinding.ActivityLoadingBinding
import com.ono.lotto_map.readJsonFromStorage
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

class LoadingActivity : BaseActivity<ActivityLoadingBinding>() {
    override val resourceId: Int = R.layout.activity_loading
    private val pref by lazy { LoadingPref(this) }

    private val jsonFile = "data.json"
    private val jsonPath by lazy { "${filesDir.absolutePath}/${jsonFile}" }
    private val storeData by lazy {
        Gson().fromJson(readJsonFromStorage(jsonPath), Array<StoreInfoResponse>::class.java)
            .toList().toModel().toEntity()
            .sortedByDescending { it.score }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadJsonFromS3()
    }

    private fun downloadJsonFromS3() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            "ap-northeast-2:18422a8f-03bf-44a5-b347-c6257f3b86ba", // 자격 증명 풀 ID
            Regions.AP_NORTHEAST_2 // 리전
        )
        // 반드시 호출해야 한다.
        TransferNetworkLossHandler.getInstance(applicationContext)

        // TransferUtility 객체 생성
        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .defaultBucket("lottomap163619-dev") // 디폴트 버킷 이름.
            .s3Client(AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_NORTHEAST_2)))
            .build()

        // 다운로드 실행. object: "SomeFile.mp4". 두 번째 파라메터는 Local경로 File 객체.
        val downloadObserver =
            transferUtility.download(jsonFile, File(jsonPath))

        // 다운로드 과정을 알 수 있도록 Listener를 추가할 수 있다.
        downloadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    Log.d("AWS", "DOWNLOAD Completed!")
                    onDownloadSuccess()
                }
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                try {
                    val done = (((current.toDouble() / total) * 100.0).toInt()) //as Int
                    Log.d("AWS", "DOWNLOAD - - ID: $id, percent done = $done")
                } catch (e: Exception) {
                    Log.d("AWS", "Trouble calculating progress percent", e)
                }
            }

            override fun onError(id: Int, ex: Exception) {
                Log.d("AWS", "DOWNLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
            }
        })
    }

    private fun onDownloadSuccess(){
        val myApplication = application as MyApplication

//        for(store in storeData){
//            myApplication.storeDao.insert(store)
//        }
    }
}

private class LoadingPref(context: Context) : PreferenceModel(context, PREF_NAME) {
    var isFirst by booleanPreference("isFirst", true)
    var updateDate by stringPreference("updateDate", "")
}