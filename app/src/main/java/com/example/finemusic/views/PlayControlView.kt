package com.example.finemusic.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.example.finemusic.R
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.msg


class PlayControlView : LinearLayout {

    private lateinit var view: View

    private lateinit var tvDuration: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var tvMusicName: TextView
    private lateinit var tvSingerName: TextView

    private lateinit var ivPlayStatus: ImageView
    private lateinit var ivNext: ImageView

    lateinit var ivMusicCover: ImageView

    private var seekBarOnTouch = false

    constructor(context: Context?) : super(context) {
        init(context!!)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context!!)
    }

    /**
     * 初始化当前的view
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun init(ctx: Context) {
        this.view = LayoutInflater.from(ctx).inflate(R.layout.custom_play_control_view, this, true)

        tvDuration = view.findViewById(R.id.tvDuration)
        seekBar = view.findViewById(R.id.seekBar)

        tvMusicName = view.findViewById(R.id.tvMusic)
        tvSingerName = view.findViewById(R.id.tvSinger)

        ivPlayStatus = view.findViewById(R.id.ivPlayStatus)
        ivNext = view.findViewById(R.id.ivNext)
        ivMusicCover = view.findViewById(R.id.ivMusicCover)

        ivPlayStatus.setOnClickListener {
            switchPlayStatus()
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarOnTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                putMediaPlayerPosition()
                seekBarOnTouch = false
            }
        })

        ivNext.setOnClickListener {
            playNextMusic()
        }

        bindCallbackEvent()

        visibility = if (MusicManager.isPlaying()) VISIBLE else GONE
    }

    /**
     * 绑定当前view的回调事件
     */
    private fun bindCallbackEvent() {
        MusicManager.apply {
            setOnMusicPlayListener(object : MusicManager.OnMusicPlayListener {
                override fun onPlayPositionChanged(
                    curPosition: Int,
                    duration: Int,
                    percent: Int
                ) {
                    bindPlayProgress(curPosition, duration, percent)
                }

                override fun onPlayStatusChanged(isPlaying: Boolean) {
                    bindPlayStatus(isPlaying)
                }

                override fun onPlaySourceChange(musicInfo: MusicInfo) {
                    visibility = View.VISIBLE
                    bindMusicInfo(musicInfo)
                }
            })
        }
    }

    private fun putMediaPlayerPosition() {
        val value = seekBar.progress
        MusicManager.seekTo(value)
    }

    private fun switchPlayStatus() {
        if (MusicManager.isPlaying())
            MusicManager.pausePlay()
        else {
            MusicManager.getCurrentMusic()?.apply {
                MusicManager.playMusic(this)
            } ?: "Sorry please select a music first!".msg()
        }
    }

    /**
     * 绑定当前的播放进度
     */
    private fun bindPlayProgress(curPosition: Int, duration: Int, percent: Int) {
        val curTime = convertTime(curPosition)
        val durationTime = convertTime(duration)

        tvDuration.text = "$curTime/$durationTime"

        if (!seekBarOnTouch)
            seekBar.setProgress(percent, true)
    }

    /**
     * 绑定当前播放音乐信息
     */
    private fun bindMusicInfo(musicInfo: MusicInfo) {
        tvMusicName.text = musicInfo.name
        tvSingerName.text = musicInfo.singerName
    }

    private fun bindPlayStatus(isPlaying: Boolean) {
        if (isPlaying)
            ivPlayStatus.setImageResource(R.drawable.pause)
        else
            ivPlayStatus.setImageResource(R.drawable.play)
    }

    /**
     * 转换时间成指定的格式
     */
    private fun convertTime(time: Int): String {
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        return "${if (min < 10) "0$min" else min}:${if (sec < 10) "0$sec" else sec}"
    }

    private fun playNextMusic() {
        MusicManager.nextMusic()
    }
}