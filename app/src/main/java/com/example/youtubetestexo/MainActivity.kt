package com.example.youtubetestexo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var YOUTUBE_VIDEO_ID = "uZnWUZW1hQo"
    private val BASE_URL =  "https://www.youtube.com"
    private var mYoutubeLink : String ? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        extractYoutubeUrl()
    }

    fun extractYoutubeUrl(){
        mYoutubeLink = BASE_URL + "/watch?v=" + YOUTUBE_VIDEO_ID
        @SuppressLint("StaticFieldLeak")val mExtractor = object : YouTubeExtractor(this){
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if(ytFiles != null){
                    playVideo(ytFiles.get(ytFiles.keyAt(17)).url)
                }
            }
        }
        mExtractor.extract(mYoutubeLink, true, true)
    }

    private fun playVideo(downloadUrl : String){
        val mPlayerView : PlayerView = findViewById(R.id.mPlayerView)
        mPlayerView.player = (ExoPlayerManager.getSharedInstance(this).mPlayerView!!.player)
        ExoPlayerManager.getSharedInstance(this).playStream(downloadUrl)
    }
}
