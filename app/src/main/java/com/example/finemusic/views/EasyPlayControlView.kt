package com.example.finemusic.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import com.example.finemusic.R
import com.example.finemusic.models.MusicInfo
import com.example.finemusic.music.MusicManager
import com.example.finemusic.utils.msg

class EasyPlayControlView : LinearLayout {

    private lateinit var view: View
    private lateinit var ivPrevious: ImageView
    private lateinit var ivNext: ImageView
    private lateinit var ivPlay: ImageView
    private lateinit var seekBar: SeekBar
    private var seekBarOnTouch: Boolean = false

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

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context!!)
    }

    private fun init(ctx: Context) {
        this.view =
            LayoutInflater.from(ctx).inflate(R.layout.custom_easy_play_control_view, this, true)

        ivPrevious = view.findViewById(R.id.ivPrevious)
        ivNext = view.findViewById(R.id.ivNext)
        ivPlay = view.findViewById(R.id.ivPlay)
        seekBar = view.findViewById(R.id.seekBar)


        ivPlay.setOnClickListener {
            switchPlayStatus()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

        ivPrevious.setOnClickListener {
            playPreviousMusic()
        }

        bindCallbackEvent()

        visibility = if (MusicManager.isPlaying())
            VISIBLE
        else
            GONE

        bindPlayStatus(MusicManager.isPlaying())
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
                    visibility = VISIBLE

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
        if (!seekBarOnTouch)
            seekBar.setProgress(percent, true)
    }

    private fun bindPlayStatus(isPlaying: Boolean) {
        if (isPlaying)
            ivPlay.setImageResource(R.drawable.baseline_pause_24)
        else
            ivPlay.setImageResource(R.drawable.baseline_play_arrow_24)
    }

    private fun playNextMusic() {
        MusicManager.nextMusic()
    }

    private fun playPreviousMusic() {
        MusicManager.prevMusic()
    }
}