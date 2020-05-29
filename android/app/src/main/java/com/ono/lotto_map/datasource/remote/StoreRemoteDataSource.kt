package com.ono.lotto_map.datasource.remote

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.ono.lotto_map.R
import io.reactivex.Completable
import io.reactivex.functions.Cancellable
import java.io.File

class StoreRemoteDataSource(private val context: Context) {
    private val downloadFileName = "data.json"
    private val saveFilePath by lazy { "${context.filesDir.absolutePath}/$downloadFileName" }

    fun downloadStoreDataFromS3(): Completable {
        return Completable.create { emitter ->
            val credentialsProvider = CognitoCachingCredentialsProvider(
                context,
                context.getString(R.string.aws_iam_key), // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
            )

            // 반드시 호출해야 한다.
            TransferNetworkLossHandler.getInstance(context)

            // TransferUtility 객체 생성
            val transferUtility = TransferUtility.builder()
                .context(context)
                .defaultBucket("lottomap163619-dev") // 디폴트 버킷 이름.
                .s3Client(
                    AmazonS3Client(
                        credentialsProvider,
                        Region.getRegion(Regions.AP_NORTHEAST_2)
                    )
                )
                .build()

            // 다운로드 실행. object: "SomeFile.mp4". 두 번째 파라메터는 Local경로 File 객체.
            val downloadObserver =
                transferUtility.download(downloadFileName, File(saveFilePath))

            // 다운로드 과정을 알 수 있도록 Listener를 추가할 수 있다.
            downloadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        emitter.onComplete()
                        Log.d("AWS", "DOWNLOAD Completed!")
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
                    emitter.onError(ex)
                }
            })
        }
    }

}