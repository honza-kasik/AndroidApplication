package cz.honzakasik.geography.learning.countryinfotabs.audioplayer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.devbrackets.android.exomedia.EMAudioPlayer;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cz.honzakasik.geography.R;

/***
 * Class containing a small audio player and playing files by URI using ExoMedia library
 */
public class AudioPlayerView extends RelativeLayout {

    private Logger logger = LoggerFactory.getLogger(AudioPlayerView.class);

    private EMAudioPlayer mediaPlayer;
    private ImageButton mainController;
    private SeekBar seekBar;

    public AudioPlayerView(Context context) {
        super(context);
        init();
    }

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mainController = (ImageButton) findViewById(R.id.play_pause_button);
        seekBar = (SeekBar) findViewById(R.id.player_seekbar);
        mainController.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        setState(PlayerState.PAUSED);
                    } else {
                        setState(PlayerState.PLAYING);
                    }
                } catch (NullPointerException e) {
                    if (e.toString().contains(MediaPlayer.class.getName())) {
                        logger.error("Media player is not initialized!", e);
                    }
                }
            }
        });
    }

    private void init() {
        inflate(getContext(), R.layout.audio_player_layout, this);
    }

    /**
     * Initializes media player and all necessary variables
     * @param sourceFileUri Uri of file which will be played
     * @param activity Parent activity of view - necessary for running code on UI thread
     * @throws IOException when source file cannot be read
     */
    public void initializePlayer(Uri sourceFileUri, final Activity activity) throws IOException {
        if (sourceFileUri == null) {
            throw new IllegalStateException("No source file specified!");
        }
        logger.info("Initializing media player with file '{}'", sourceFileUri.toString());
        mediaPlayer = new EMAudioPlayer(getContext());
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getContext(), sourceFileUri);

        mediaPlayer.setOnBufferUpdateListener(new OnBufferUpdateListener() {
            @Override
            public void onBufferingUpdate(@IntRange(from = 0L, to = 100L) int percent) {
                logger.info("Buffer full at {}%!", percent);
            }
        });
        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                logger.info("Prepared media player with file {}ms long", mediaPlayer.getDuration());
                setState(PlayerState.PAUSED);
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //Not usable for touch event only - this listener is called every time when something changes position on seekbar
                        //NOT IMPLEMENTED
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //NOT IMPLEMENTED
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int position = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(position);
                            }
                        });
                    }
                }, 100, 100);
            }
        });
        mediaPlayer.prepareAsync();
    }

    /**
     * Set state of player to one of possible values
     * @param state state of player - e.g. Playing, paused...
     */
    private void setState(PlayerState state) {
        if (state == PlayerState.PLAYING) {
            mediaPlayer.start();
            mainController.setImageDrawable(getResources().getDrawable(R.drawable.pause_button));
        } else if (state == PlayerState.PAUSED) {
            mediaPlayer.pause();
            mainController.setImageDrawable(getResources().getDrawable(R.drawable.play_button));
        }
    }

    /***
     * Stops music playback and releases media player
     */
    public void close() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stopPlayback();
            mediaPlayer.release();
        }
    }
}
