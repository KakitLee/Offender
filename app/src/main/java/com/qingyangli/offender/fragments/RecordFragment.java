package com.qingyangli.offender.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.project.zhi.tigerapp.MainActivity_;
import com.project.zhi.tigerapp.ProfileActivity_;
import com.qingyangli.offender.AudioDispatcher;
import com.qingyangli.offender.DBHelper;
import com.qingyangli.offender.GaussianMixture;
import com.qingyangli.offender.Matrix;
import com.qingyangli.offender.PointList;
import com.project.zhi.tigerapp.R;
import com.qingyangli.offender.RecordingMfccService;
import com.qingyangli.offender.RecordingMfccService.LocalBinder;
import com.qingyangli.offender.ShortMFCC;
import com.melnykov.fab.FloatingActionButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TimerTask;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;

import static android.media.audiofx.NoiseSuppressor.isAvailable;

//import be.tarsos.dsp.AudioDispatcher;
//import be.tarsos.dsp.mfcc.MFCC;
//import com.danielkim.soundrecorder.MFCC;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int position;

    //Recording controls
    private FloatingActionButton mRecordButton = null;
    //private Button mPauseButton = null;

    private TextView mRecordingPrompt;
    private TextView mLikelihoodPrompt;
    private TextView mLikelihoodValuePrompt;
    private TextView mMatchedNamePrompt;
    private TextView mMatchedNameValuePrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    //private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    //long timeWhenPaused = 0; //stores time when user clicks pause button
////**********************************************************************************************
    RecordingMfccService mService;
    Intent intentBindService;
    boolean isBound = false;
    final static String TAG = "MFCCBindingActivity";
    BroadcastReceiver receiver;
    final String storageRootPath = String.valueOf(Environment.getExternalStorageDirectory());
    final static String Thesis_Tarsos_CSV_PATH = "Thesis/Tarsos/CSV";
    final static String Thesis_Tarsos_Logs_PATH = "Thesis/Tarsos/Logs";
    final File folderPara = new File(storageRootPath + "/Thesis/Tarsos/Covs");
    final File folderModel = new File(storageRootPath + "/Thesis/Tarsos/model");

    final static String csvFileName = "tarsos_mfcc.csv";
    //final String batteryFileName = "battery_data.txt";
    final static String trainedWeightsPath = "Thesis/Tarsos/Weights";
    final static String trainedMeansPath = "Thesis/Tarsos/Means";
    final static String trainedCovsPath = "Thesis/Tarsos/Covs";
    final static String trainedModelPath = "Thesis/Tarsos/model";
    //final static String trainedFileName = "fdnc0.txt";
    //final static String trainedFileName = "lqy_test_android_mfcc_model_001_big.txt";
    //final static String trainedFileName = "lqy_test_android_mfcc_model_002_big.txt";
    //final static String trainedFileName = "QingyangLi_andriod_MFCC_model.txt";
    final static int CompononetsNum = 32;
    final static int FeatureLength = 13;
    //********************************************************************************************
    //**record from microphone
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    //String pcmFilePath = Environment.getExternalStorageDirectory() + "/ShenYi.pcm";
    //String mfccFilePath = Environment.getExternalStorageDirectory() + "/ShenYi_andriod_MFCC.txt";
    //String wavFilePath = Environment.getExternalStorageDirectory() + "/ShenYi.wav";
    //String pcmFilePath=Environment.getExternalStorageDirectory() +"/ShunshunDuan.pcm";
    //String mfccFilePath=Environment.getExternalStorageDirectory() +"/ShunshunDuan_andriod_MFCC.txt";
    //String pcmFilePath=Environment.getExternalStorageDirectory() +"/lqy_test_001_big.pcm";
    //String mfccFilePath=Environment.getExternalStorageDirectory() +"/lqy_test_android_mfcc_001_big.txt";
    //String pcmFilePath=Environment.getExternalStorageDirectory() +"/lqy_test_001_little.pcm";
    //String mfccFilePath=Environment.getExternalStorageDirectory() +"/lqy_test_android_mfcc_001_little.txt";
    //String pcmFilePath=Environment.getExternalStorageDirectory() +"/current_lqy_test_001_little.pcm";
    //String mfccFilePath=Environment.getExternalStorageDirectory() +"/current_lqy_test_android_mfcc_001_little.txt";
    //String pcmFilePath=Environment.getExternalStorageDirectory() +"/current_lqy_test_002_big.pcm";
    //String mfccFilePath=Environment.getExternalStorageDirectory() +"/current_lqy_test_android_mfcc_002_big.txt";
    String pcmFilePath = Environment.getExternalStorageDirectory() + "/current.pcm";
    String mfccFilePath = Environment.getExternalStorageDirectory() + "/current.txt";
    String[] modelName;
    double[] modelLikelihood;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format
    int bufferSize = 160;
    int sampleSizeInBits = 16;
    ArrayList<float[]> mfccList;
    final int samplesPerFrame = 160;
    final int sampleRate = 8000;
    //final int amountOfCepstrumCoef = 14; //actually 18 but energy column would be discarded,change to 14
    final int amountOfCepstrumCoef = 13;
    int amountOfMelFilters = 27;
    float lowerFilterFreq = 133.3334f;
    float upperFilterFreq = ((float) sampleRate) / 2f;


    int modelCount = 0;


    private String mFileName = null;
    private String mFilePath = null;
    private DBHelper mDatabase;
    private TimerTask mIncrementTimerTask = null;
    private long mElapsedMillis = 0;
    private long mStartingTimeMillis = 0;
    //*/


    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = pcmFilePath;
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, BufferElements2Rec);

            //System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static RecordFragment newInstance(int position) {
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        /*
        try {
            simpleTest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);
        mLikelihoodPrompt = (TextView) recordView.findViewById(R.id.textLikelihood);
        mLikelihoodValuePrompt = (TextView) recordView.findViewById(R.id.textLikelihoodValue);
        mMatchedNamePrompt = (TextView) recordView.findViewById(R.id.textMatchedName);
        mMatchedNameValuePrompt = (TextView) recordView.findViewById(R.id.textMatchedNameValue);

        mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
        mRecordButton.setColorNormal(getResources().getColor(R.color.colorPrimary));
        mRecordButton.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });

        //mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
        //mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts

        /*
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(mPauseRecording);
                mPauseRecording = !mPauseRecording;
            }
        });
        */

        return recordView;
    }

    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start) {
        if (start) {
            // start recording
            mLikelihoodPrompt.setText("Likelihood:");
            mLikelihoodValuePrompt.setText("");
            mMatchedNamePrompt.setText("Most Matched Name:");
            mMatchedNameValuePrompt.setText("");
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), R.string.toast_recording_start, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }

            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });

            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

            //*start record from microphone
            startRecording();
            mStartingTimeMillis = System.currentTimeMillis();

        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            //timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            //tap the button to start recording
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //*start record from microphone
            stopRecording();
            //get the length of audio
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            mfccList = new ArrayList<float[]>();
            mDatabase = new DBHelper(getActivity().getApplicationContext());
            setFileNameAndPath();
            //transform the .pcm to .wav
            File f1 = new File(pcmFilePath); // The location of your PCM file
            File f2 = new File(mFilePath);
            try {
                rawToWave(f1, f2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getActivity(), getString(R.string.toast_recording_finish)
                    + " " + mFilePath, Toast.LENGTH_LONG).show();

            //remove notification
            if (mIncrementTimerTask != null) {
                mIncrementTimerTask.cancel();
                mIncrementTimerTask = null;
            }


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
                    new TarsosDSPAudioFormat(RECORDER_SAMPLERATE, sampleSizeInBits, 1, true, false))
                    , bufferSize, bufferSize / 2);
            // audiorecord starts recording
            //MFCC( samplesPerFrame, sampleRate ) //typical samplesperframe are power of 2 & Samples per frame = (sample rate)/FPS
            //Florian suggested to use 16kHz as sample rate and 512 for frame size
            final ShortMFCC mfccObj = new ShortMFCC(samplesPerFrame, sampleRate
                    , amountOfCepstrumCoef, amountOfMelFilters, lowerFilterFreq, upperFilterFreq); //(1024,22050);

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
                    Log.i("MFCC-done", "done!!!!!!!!!!!!!!!!!!!!!!!!!");

                }

                @Override
                public boolean process(AudioEvent audioEvent) {
                    // TODO Auto-generated method stub
                    //process the audio event. do the actual signal processing on an (optionally) overlapping buffer

                    //fetchng MFCC array and removing the 0th index because its energy coefficient and florian asked to discard
                    float[] mfccOutput = mfccObj.getMFCC();
                    //mfccOutput = Arrays.copyOfRange(mfccOutput, 1, mfccOutput.length);
                    mfccOutput = Arrays.copyOfRange(mfccOutput, 0, mfccOutput.length);

                    //Storing in global arraylist so that i can easily transform it into csv
                    mfccList.add(mfccOutput);
                    Log.i("MFCC-test", String.valueOf(Arrays.toString(mfccOutput)));


                    return true;
                }
            });
            //its better to use thread vs asynctask here. ref : http://stackoverflow.com/a/18480297/1016544
            //new Thread(dispatcher, "Audio Dispatcher").run();
            dispatcher.run();
            //Log.i("MFCC-length of list", String.valueOf(mfccList.size()));
            saveFloatArrayToFile(mfccList, mfccFilePath);

            //do speaker recognition
            try {
                modelNameInFolder(folderModel);
                modelLikelihood = new double[modelCount];
                for (int i = 0; i < modelCount; i++) {
                    modelLikelihood[i] = audioFeatureClassifyNew(mfccList, modelName[i]);
                }

                double max = -9999999;
                int index = -1;
                for (int i = 0; i < modelCount; i++) {
                    if (modelLikelihood[i] > max) {
                        max = modelLikelihood[i];
                        index = i;
                    }
                }
                String[] tempParts = modelName[index].split("_");
                String matchedModelName = tempParts[0];
                mLikelihoodValuePrompt.setText(String.valueOf(max));
                mMatchedNameValuePrompt.setText(matchedModelName);
                Log.i(TAG, "likelihood is:" + String.valueOf(modelLikelihood[index]));
                String id = "";

                try {
                    Log.i("Audiolength", String.valueOf(mElapsedMillis));
                    mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis, matchedModelName,max);
                    //mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception", e);
                }


                boolean isMatched = true;
                if (matchedModelName.toLowerCase().equalsIgnoreCase("george")) {
                    id = "ZHENG_George_BHU/0712/2018";
                } else if (matchedModelName.toLowerCase().equalsIgnoreCase("shenyi")) {
                    id = "YI_Eason_BHU/0712/2018";
                } else if (matchedModelName.toLowerCase().equalsIgnoreCase("qingyangli")) {
                    id = "LI_qingyang_BHU/0712/2018";
                } else if (matchedModelName.toLowerCase().equalsIgnoreCase("shunshunduan")) {
                    id = "DUAN_shunshun_BHU/0712/2018";
                } else {
                    isMatched = false;
                }
                if (isMatched) {
                    Intent newIntend = new Intent(this.getActivity(), MainActivity_.class);
                    newIntend.putExtra("voice", id);
                    startActivity(newIntend);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void saveFloatArrayToFile(ArrayList<float[]> mfccList, String vPath) {
        if (null == mfccList) {
            return;
        }
        if (null == vPath || vPath.equals("")) {
            return;
        }

        File file = new File(vPath);  //存放数组数据的文件
        //long t = System.currentTimeMillis();

        StringBuffer tBuffer = new StringBuffer();
//      int len = vArr.length;
//      System.out.println("len="+len);

        for (float[] list : mfccList) {
            for (float val : list) {
                //保留18位小数，这里可以改为其他值
                tBuffer.append(String.format("%.18f", val));
                tBuffer.append("\r\n");
            }
        }
        try {
            FileWriter out = new FileWriter(file);  //文件写入流
            out.write(tBuffer.toString());
            out.close();
        } catch (Exception e) {
            Log.i(TAG, "write error!");
        }

        //t = System.currentTimeMillis()- t;
        //System.out.println("t="+t);
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "onPause");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        // Bind to LocalService. Also in onResume so that it rebinds after coming back from homepressed
        //intentBindService = new Intent(getActivity(), RecordingMfccService.class);
        //getActivity().bindService(intentBindService, mConnection, Context.BIND_AUTO_CREATE);

        //LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), new IntentFilter(RecordingMfccService.COPA_RESULT));

    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "onServiceConnected");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            isBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "onServiceDisConnected");

            isBound = false;


        }
    };

    private void modelNameInFolder(final File folder) {
        modelCount = 0;
        File[] files = folder.listFiles();
        modelName = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            modelName[modelCount] = new String(files[i].getName());
            modelCount++;
        }
        /*
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                //String[] tempParts = fileEntry.getName().split("_");
                //modelName[modelCount]=tempParts[0];
                modelName[modelCount]=new String(fileEntry.getName());
                modelCount++;
            }
        }
        */
    }

    private double audioFeatureClassifyNew(ArrayList<float[]> mfccFeatureInput, String trainedFileName) throws IOException {
        String traindModelStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedModelPath + File.separator + trainedFileName;
        GaussianMixture gmmModel = GaussianMixture.readGMM(traindModelStoragePath);
        //mfcc feature for collected audio sample
        PointList mfccFeature = new PointList(FeatureLength);
        int pointListNum = mfccFeatureInput.size();
        for (int i = 0; i < pointListNum; i++) {
            float[] tempFeature = mfccFeatureInput.get(i);
            double[] tempFeatureTrans = new double[FeatureLength];
            for (int j = 0; j < FeatureLength; j++) {
                tempFeatureTrans[j] = (double) tempFeature[j];
            }
            mfccFeature.add(tempFeatureTrans);
        }
        double result = gmmModel.getLogLikelihood(mfccFeature);

        return result;

    }

    private double audioFeatureClassify(ArrayList<float[]> mfccFeatureInput, String trainedFileName) throws IOException {
        double[] weights = new double[CompononetsNum];
        Matrix[] means = new Matrix[CompononetsNum];
        Matrix[] covs = new Matrix[CompononetsNum];
        String traindWeightsStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedWeightsPath + File.separator + trainedFileName;
        String traindMeansStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedMeansPath + File.separator + trainedFileName;
        String traindCovsStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedCovsPath + File.separator + trainedFileName;
        FileReader trainedWeights = new FileReader(traindWeightsStoragePath);
        FileReader trainedMeans = new FileReader(traindMeansStoragePath);
        FileReader trainedCovs = new FileReader(traindCovsStoragePath);
        Scanner srcWeights = new Scanner(trainedWeights);
        Scanner srcMeans = new Scanner(trainedMeans);
        Scanner srcCovs = new Scanner(trainedCovs);
        //error handlling!!!!!
        //get weights, means and covs of one model for gmm
        for (int i = 0; i < CompononetsNum; i++) {
            if (srcWeights.hasNextDouble())
                weights[i] = srcWeights.nextDouble();
        }
        Log.i(TAG, "get_weights");
        for (int i = 0; i < CompononetsNum; i++) {
            double[][] tempComponent = new double[FeatureLength][1];
            for (int j = 0; j < FeatureLength; j++) {
                if (srcMeans.hasNextDouble()) {
                    tempComponent[j][0] = srcMeans.nextDouble();
                }
            }
            means[i] = new Matrix(tempComponent);
        }
        for (int i = 0; i < CompononetsNum; i++) {
            double[][] tempComponent = new double[FeatureLength][FeatureLength];
            for (int j = 0; j < FeatureLength; j++) {
                for (int k = 0; k < FeatureLength; k++) {
                    if (srcCovs.hasNextDouble()) {
                        tempComponent[j][k] = srcCovs.nextDouble();
                    }
                }
            }
            covs[i] = new Matrix(tempComponent);
        }
        //construct gmm model
        GaussianMixture gmmModel = new GaussianMixture(weights, means, covs);

        //mfcc feature for collected audio sample
        PointList mfccFeature = new PointList(FeatureLength);
        int pointListNum = mfccFeatureInput.size();
        for (int i = 0; i < pointListNum; i++) {
            float[] tempFeature = mfccFeatureInput.get(i);
            double[] tempFeatureTrans = new double[FeatureLength];
            for (int j = 0; j < FeatureLength; j++) {
                tempFeatureTrans[j] = (double) tempFeature[j];
            }
            mfccFeature.add(tempFeatureTrans);
        }
        double result = gmmModel.getLogLikelihood(mfccFeature);

        return result;

    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, 8000); // sample rate
            writeInt(output, RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public void setFileNameAndPath() {
        int count = 0;
        File f;

        do {
            count++;
            mFileName = getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count) + ".wav";
            //mFileName = getString(R.string.default_file_name)
            //      + "_" + (mDatabase.getCount() + count) + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/SoundRecorder/" + mFileName;

            f = new File(mFilePath);
        } while (f.exists() && !f.isDirectory());
    }
    /*
    private void simpleTest() throws IOException {
        double[] weights = new double[CompononetsNum];
        Matrix[] means = new Matrix[CompononetsNum];
        Matrix[] covs = new Matrix[CompononetsNum];
        String traindWeightsStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedWeightsPath + File.separator + trainedFileName;
        String traindMeansStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedMeansPath + File.separator + trainedFileName;
        String traindCovsStoragePath = Environment.getExternalStorageDirectory()
                + File.separator + trainedCovsPath + File.separator + trainedFileName;
        FileReader trainedWeights = new FileReader(traindWeightsStoragePath);
        FileReader trainedMeans = new FileReader(traindMeansStoragePath);
        FileReader trainedCovs = new FileReader(traindCovsStoragePath);
        Scanner srcWeights = new Scanner(trainedWeights);
        Scanner srcMeans = new Scanner(trainedMeans);
        Scanner srcCovs = new Scanner(trainedCovs);
        //error handlling!!!!!
        //get weights, means and covs of one model for gmm
        for (int i = 0; i < CompononetsNum; i++) {
            if (srcWeights.hasNextDouble())
                weights[i] = srcWeights.nextDouble();
        }
        Log.i(TAG, "get_weights");
        for (int i = 0; i < CompononetsNum; i++) {
            double[][] tempComponent = new double[FeatureLength][1];
            for (int j = 0; j < FeatureLength; j++) {
                if (srcMeans.hasNextDouble()) {
                    tempComponent[j][0] = srcMeans.nextDouble();
                }
            }
            means[i] = new Matrix(tempComponent);
        }
        for (int i = 0; i < CompononetsNum; i++) {
            double[][] tempComponent = new double[FeatureLength][FeatureLength];
            for (int j = 0; j < FeatureLength; j++) {
                for (int k = 0; k < FeatureLength; k++) {
                    if (srcCovs.hasNextDouble()) {
                        tempComponent[j][k] = srcCovs.nextDouble();
                    }
                }
            }
            covs[i] = new Matrix(tempComponent);
        }
        //construct gmm model
        GaussianMixture gmmModel = new GaussianMixture(weights, means, covs);
    }*/
}