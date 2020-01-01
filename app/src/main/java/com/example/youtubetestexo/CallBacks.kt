package com.example.youtubetestexo

interface CallBacks {
    fun callbackObserver(obj : Object)

    interface playerCallBack{
        fun onItemClickOnItem(albumList : Int)

        fun onPlayingEnd()
    }
}