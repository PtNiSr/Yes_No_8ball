package com.ni___ckel.yesnoshake;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private boolean startOrNot = true;  //If it is true the prediction could be started to download
    private boolean quake = true;
    private MutableLiveData<AnswerFromBall> answerFromBall = new MutableLiveData<>();
    private MutableLiveData<String> str = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LiveData<String> getStr() {
        return str;
    }

    public LiveData<AnswerFromBall> getAnswerFromBall() {
        return answerFromBall;
    }

    public void setStartOrNot(boolean startOrNot) {
        this.startOrNot = startOrNot;
    }

    public boolean isStartOrNot() {
        return startOrNot;
    }

    public void setStartOrNotTrueByTimer() {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startOrNot = true;
                //The new one prediction will be able in 2 sec, after last one
            }
        }, 2000);
    }

    public void setQuake(boolean quake) {
        this.quake = quake;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void printPrediction(float tx, float ty, float tz) {
        if ((tx > 35.0f) && (startOrNot)) {
           loadAnswer();
        }
    }


    public void soundOn(float tx, MediaPlayer mediaPlayer) {        //This method for making shaking nose
        if ((tx > 35.0f) && (quake)) {
            quake = false;
            musicOn(mediaPlayer);

            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    quake = true;
                }
            }, 1700);       //delay to make sound play from begin to end fully.
            // To play sound again will be possible only after 1,7 sec.
        }
    }


    public void musicOn(MediaPlayer mediaPlayer) {                                  //here is "working" with MP3 file from the "raw" folder
        mediaPlayer = MediaPlayer.create(getApplication(), R.raw.shake2v3);       //chosen the MP3 track.
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }

    public void loadAnswer() {
        Disposable disposable = loadAnswerFromBallRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AnswerFromBall>() {
                    @Override
                    public void accept(AnswerFromBall answer) throws Throwable {
                        answerFromBall.setValue(answer);
                        str.setValue(answer.getReading());      //We need to translate it directly to MainActivity via LiveData
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {

                        str.setValue("Shake me later");
                    }
                });
        compositeDisposable.add(disposable);
    }


    private Single<AnswerFromBall> loadAnswerFromBallRx() {
        return ApiFactory.getApiService().loadAnswerFromBall8();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
