package soundrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;

public class RecordingMfccService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private String TAG = "MFCCService";

    //Tarsos Parameters//
    private AudioDispatcher dispatcher = null;
    final double endTime = 20.0;

    static int mfccIndex = 0;
    ArrayList<float[]> mfccList;

    LocalBroadcastManager broadcaster;
    static final public String COPA_RESULT = "com.example.tarsosaudioproject.RecordingMfccService.REQUEST_PROCESSED";
    static final public String COPA_MESSAGE = "UINotification";

    Handler handler;
    String uiMessage = "";

    //MFCC attributes
    final int samplesPerFrame = 160;
    final int sampleRate = 8000;
    final int amountOfCepstrumCoef = 14; //actually 18 but energy column would be discarded,change to 14
    //final int amountOfCepstrumCoef = 13;
    int amountOfMelFilters = 27;
    float lowerFilterFreq = 133.3334f;
    float upperFilterFreq = ((float) sampleRate) / 2f;
    String pcmFilePath= Environment.getExternalStorageDirectory() +"/voice8K16bitmono.pcm";
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
    public RecordingMfccService() {
        Log.d(TAG, "constructor done");

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");

        //handler= new Handler();
        broadcaster = LocalBroadcastManager.getInstance(this);


    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public RecordingMfccService getService() {
            Log.d(TAG, "getService done");

            // Return this instance of LocalService so clients can call public methods
            return RecordingMfccService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind done");

        //initilizeMic

        return mBinder;
    }


    public void initDispatcher() {
        Log.d(TAG, "initDispatcher done");
        mfccList = new ArrayList<float[]>();

        //sampleRate, audioBufferSize, int bufferOverlap
        //Florian suggested to use 16kHz as sample rate
        //dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(16000, 1024, 0); //(22050,1024,0);
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(pcmFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AudioDispatcher dispatcher = new AudioDispatcher(new UniversalAudioInputStream(inStream,
                new TarsosDSPAudioFormat(sampleRate, bufferSize, 1, true, false)), bufferSize, bufferSize/2);
        // audiorecord starts recording


    }

    public boolean isDispatcherNull() {
        if (dispatcher == null)
            return true;
        else
            return false;
    }

    public void stopDispatcher() {
        dispatcher.stop();
        dispatcher = null;

    }

    //after getting feedback from owner Joren Six in email
    public void startMfccExtraction() {


        //MFCC( samplesPerFrame, sampleRate ) //typical samplesperframe are power of 2 & Samples per frame = (sample rate)/FPS
        //Florian suggested to use 16kHz as sample rate and 512 for frame size
        final MFCC mfccObj = new MFCC(samplesPerFrame, sampleRate, amountOfCepstrumCoef, amountOfMelFilters, lowerFilterFreq, upperFilterFreq); //(1024,22050);

  		/*AudioProcessors are responsible for actual digital signal processing. AudioProcessors are meant to be chained
  		e.g. execute an effect and then play the sound.
  		The chain of audio processor can be interrupted by returning false in the process methods.
  		*/
        dispatcher.addAudioProcessor(mfccObj);
        //handlePitchDetection();
        dispatcher.addAudioProcessor(new AudioProcessor() {

            @Override
            public void processingFinished() {
                // TODO Auto-generated method stub
                //Notify the AudioProcessor that no more data is available and processing has finished


            }

            @Override
            public boolean process(AudioEvent audioEvent) {
                // TODO Auto-generated method stub
                //process the audio event. do the actual signal processing on an (optionally) overlapping buffer

                //fetchng MFCC array and removing the 0th index because its energy coefficient and florian asked to discard
                float[] mfccOutput = mfccObj.getMFCC();
                mfccOutput = Arrays.copyOfRange(mfccOutput, 1, mfccOutput.length);
               // mfccOutput = Arrays.copyOfRange(mfccOutput, 0, mfccOutput.length);

                //Storing in global arraylist so that i can easily transform it into csv
                mfccList.add(mfccOutput);
                Log.i("MFCC-test", String.valueOf(Arrays.toString(mfccOutput)));


                return true;
            }
        });
        //its better to use thread vs asynctask here. ref : http://stackoverflow.com/a/18480297/1016544
        new Thread(dispatcher, "Audio Dispatcher").start();
    }
    public ArrayList<float[]> getMfccList() {
        return mfccList;
    }
}

