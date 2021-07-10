package com.mza.musicplayer.adapters

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mza.musicplayer.R
import com.mza.musicplayer.models.SongModel
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

class SongsAdapter(private val mContext: Context, private val listener: SongItemClick): RecyclerView.Adapter<SongViewHolder>() {

    val songList = ArrayList<SongModel>()
    val songImage = ArrayList<ByteArray?>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        val viewHolder = SongViewHolder(view)
        view.setOnClickListener {
            listener.onSongItemClick(songList, viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.name.text = songList[position].title
        holder.duration.text = songList[position].duration
        var image: ByteArray? = getAlbumArt(songList[position].path)
        if(image != null){
            Glide.with(mContext).load(image).into(holder.pic)
        }

    }

    override fun getItemCount(): Int {
        return songList.size
    }

    fun updateSongList(updatedList: ArrayList<SongModel>){
        songList.clear()
        songList.addAll(updatedList)
        songImage.clear()
        for(i in 1..updatedList.size)
            songImage.add(null)
        notifyDataSetChanged()
    }

    fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()
        return art
    }
}

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.song_item_name)
    val duration: TextView = itemView.findViewById(R.id.song_item_duration)
    val pic: ImageView = itemView.findViewById(R.id.song_item_pic)
}

interface SongItemClick{
    fun onSongItemClick(songModel: ArrayList<SongModel>, position: Int)
}