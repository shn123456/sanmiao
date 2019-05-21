package com.sanmiao.wypread.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/9 0009.
 * 类说明{在线音乐播放工具类}
 */
public class Player implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    public MediaPlayer mediaPlayer; // 媒体播放器
    public SeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器
    private TextView nowTime;//当前播放时间
    private TextView allTime;//总时间
    // 初始化播放器
    public Player(SeekBar seekBar, TextView nowTime ,TextView allTime) {
        super();
        this.seekBar = seekBar;
        this.nowTime=nowTime;
        this.allTime = allTime;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }
    public Player(SeekBar seekBar){
        super();
        this.seekBar = seekBar;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    // 计时器
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            try{
                if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                    handler.sendEmptyMessage(0); // 发送消息
                }
            }catch (Exception e){}

        }
    };
    int position;
    int duration;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try{
                position = mediaPlayer.getCurrentPosition();
                duration= mediaPlayer.getDuration();
                if (duration > 0) {
                    // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                    long pos = seekBar.getMax() * position / duration;
                    seekBar.setProgress((int) pos);
                    // 计算时间（当前音乐时长）
                    long now =  position;

                    Date dateOld = new Date(now); // 根据long类型的毫秒数生命一个date类型的时间
                    SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
                    //进行格式化
                    String str=sdf.format(dateOld);
                    nowTime.setText(str);

                    long all = duration;
                    Log.e("now",now+"");
                    Log.e("all",all+"");
                    Date dateAll = new Date(all); // 根据long类型的毫秒数生命一个date类型的时间
                    SimpleDateFormat allT=new SimpleDateFormat("mm:ss");
                    String str1=allT.format(dateAll);
                    allTime.setText(str1);

                }
            }catch (Exception e){}

        };
    };

    public void play() {
        mediaPlayer.start();
    }

    public void getNowTime(){
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (duration > 0) {
            // 计算进度（当前音乐播放位置 /获取进度条最大刻度 * 当前音乐时长）
            long pos =  position / seekBar.getMax()  * duration;
            Date dateOld = new Date(pos); // 根据long类型的毫秒数生命一个date类型的时间
            SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
            //进行格式化
            String str=sdf.format(dateOld);
            nowTime.setText(str);
        }
    }

    /**文件时长*/
    public String getDuration(){
        try{
            long duration =mediaPlayer.getDuration();
            Date dateOld = new Date(duration); // 根据long类型的毫秒数生命一个date类型的时间

            SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
            //进行格式化
            String strs=sdf.format(dateOld);
            return strs;
        }catch (Exception e){
        }

        return "00:00";
    }

    /**
     *
     * @param url
     *            url地址
     */
    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    int i =0;
    //设置播放时间
    public void setPlayTime(int i){
        this.i = i;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(i);
        }
    }


    // 暂停
    public void pause() {
        mediaPlayer.pause();
    }

    // 停止
    public void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                //e.printStackTrace();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //释放
    public void next(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    // 播放准备
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mediaPlayer.seekTo(i);
    }

    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }


    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
        try{
            if(mediaPlayer.getDuration()!= 0 ){
                int currentProgress = seekBar.getMax()
                        * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
                Log.e(currentProgress + "% play", percent + " buffer");
            }
        }catch (Exception e){}

    }

}
