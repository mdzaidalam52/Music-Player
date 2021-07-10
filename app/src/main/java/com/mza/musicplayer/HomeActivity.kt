package com.mza.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mza.musicplayer.adapters.SongItemClick
import com.mza.musicplayer.adapters.SongsAdapter
import com.mza.musicplayer.models.SongModel

class HomeActivity : AppCompatActivity(), SongItemClick {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songsAdapter: SongsAdapter
    private val songPermissionReqCode = 123
    var mContext = this
    private val mediaPlayer = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.hide()

        songRecyclerView = findViewById(R.id.song_recycler_view)
        songRecyclerView.layoutManager = LinearLayoutManager(mContext)
        songsAdapter = SongsAdapter(mContext, mContext)
        songRecyclerView.adapter = songsAdapter
        songRecyclerView.setHasFixedSize(true)
        fetchAllSongs()
    }

    private fun fetchAllSongs() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), songPermissionReqCode);
        }else{
            getSongs()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == songPermissionReqCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getSongs()
        }
    }

    private fun getSongs() {
        val songList = ArrayList<SongModel>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,

        )

        val cursor = applicationContext.contentResolver
            .query(uri, projection, null, null, null)

        if (cursor != null) {
            while(cursor.moveToNext()){
                var title = cursor.getString(0)
                var duration = cursor.getString(1).toInt()/1000
                var album = cursor.getString(2)
                var path = cursor.getString(3)
                var artist = cursor.getString(4)
                songList.add(SongModel(title, duration.toString(), album, path, artist))
            }
            cursor.close()
        }

        songsAdapter.updateSongList(songList)
    }

    override fun onSongItemClick(songModel: ArrayList<SongModel>, position: Int) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(songModel[position].path)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
    }
}