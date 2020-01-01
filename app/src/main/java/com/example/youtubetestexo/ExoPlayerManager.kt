package com.example.youtubetestexo

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

class ExoPlayerManager {
    companion object{
        private val BANDWIDTH_METER = DefaultBandwidthMeter()
        private val TAG = "ExoPlayerManager"
        private var mInstance : ExoPlayerManager? = null

        fun getSharedInstance(context: Context) : ExoPlayerManager{
            if(mInstance == null){
                mInstance = ExoPlayerManager()
                mInstance!!.init(context)
            }
            return mInstance!!
        }
    }

    var mPlayerView : PlayerView? = null
    var dataSourceFactory : DefaultDataSourceFactory? = null
    var uriString : String = ""
    var mPlayList : ArrayList<String>?  = null
    var playlistIndex : Int = 0
    var listener : CallBacks.playerCallBack? = null
    var mPlayer : SimpleExoPlayer? = null

    private fun init(context : Context){
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(BANDWIDTH_METER)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        mPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

        mPlayerView = PlayerView(context)
        mPlayerView!!.useController = true
        mPlayerView!!.requestFocus()
        mPlayerView!!.player = mPlayer

        val mp4VideoUri = Uri.parse(uriString)

        dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "youtubeTestExo"), BANDWIDTH_METER)

        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri)

        mPlayer!!.prepare(videoSource)
        mPlayer!!.addListener(object : Player.EventListener{

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.i(TAG, "타임라인 변경")
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
                Log.i(TAG, "트랙 변경")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.i(TAG, "로딩여부 변경")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.i(TAG, "onPlayerStateChanged: ")
                if(playbackState == 4 && mPlayList != null && playlistIndex +1 < mPlayList!!.size){
                    Log.e(TAG, "노래바뀐당")

                    playlistIndex++
                    listener!!.onItemClickOnItem(playlistIndex)
                    playStream(mPlayList!!.get(playlistIndex))
                }else if(playbackState == 4 && mPlayList != null && playlistIndex +1 == mPlayList!!.size){
                    mPlayer!!.playWhenReady = false
                }
                if(playbackState == 4 && listener != null){
                    listener!!.onPlayingEnd()
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.i(TAG, "반복상태 변경")
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.i(TAG, "섞기 여부? 변경")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {

                Log.i(TAG, "에러 : " + error.toString())

                Log.i(TAG, "에러 : " + error!!.sourceException)
                Log.i(TAG, "에러 : " + error!!.message)
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.i(TAG, "onPositionDiscontinuity")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.i(TAG, "onPlaybackParametersChanged")
            }

            override fun onSeekProcessed() {
                Log.i(TAG, "onSeekProcessed")
            }
        })
    }

    fun playStream(uriToPlay : String){
        uriString = uriToPlay
        var mp4VideoUri = Uri.parse(uriString)
        var videoSource : MediaSource? = null
        val fileNameArray : List<String> = uriToPlay.split("\\.")
        if(uriString.toUpperCase().contains("M3U8")){
            videoSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri)
        }
        else{
            mp4VideoUri = Uri.parse(uriToPlay)
            videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri)
        }

        mPlayer!!.prepare(videoSource)
        mPlayer!!.playWhenReady = true
    }

    fun setPlayerVolume(vol : Float){
        mPlayer!!.volume = vol
    }

    fun setPlayList(uriArray : ArrayList<String>, index : Int, callBack : CallBacks.playerCallBack){
        mPlayList = uriArray
        playlistIndex = index
        listener = callBack
        playStream(mPlayList!!.get(playlistIndex))
    }

    fun playerPlaySwitch(){
        if(uriString !=""){
            mPlayer!!.playWhenReady = !mPlayer!!.playWhenReady
        }
    }

    fun stopPlayer(state : Boolean){
        mPlayer!!.playWhenReady = !state
    }

    fun destroyPlayer(){
        mPlayer!!.stop()
    }

    fun isPlayerPlaying() : Boolean{
        return mPlayer!!.playWhenReady
    }

    fun readURLs(url : String) : ArrayList<String>{
        if(url == null) return ArrayList()
        val allURIs = ArrayList<String>()
        try{
            val urls = URL(url)
            val reader = BufferedReader(InputStreamReader(urls.openStream()))
            var str : String
            do{
                str = reader.readLine()
                if(str == null)
                    break
                allURIs.add(str)
            }
            while (true)

            reader.close()
            return allURIs
        }catch (e : Exception){
            return ArrayList()
        }
    }
}