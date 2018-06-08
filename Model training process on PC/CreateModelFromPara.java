package com.qingyangli.offender;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateModelFromPara {
    public int CompononetsNum=32;
    public ArrayList<float[]> mfccList;
    public String traindWeightsStorageDir="D:\\uni\\project\\code\\Speaker-Recognition-System-using-GMM-master" +
            "\\Speaker-Recognition-System-using-GMM-master\\timit\\mfcc\\andriod_mfcc\\train_weights";
    public String traindMeansStorageDir="D:\\uni\\project\\code\\Speaker-Recognition-System-using-GMM-master" +
            "\\Speaker-Recognition-System-using-GMM-master\\timit\\mfcc\\andriod_mfcc\\train_means";
    public String traindCovsStorageDir="D:\\uni\\project\\code\\Speaker-Recognition-System-using-GMM-master" +
            "\\Speaker-Recognition-System-using-GMM-master\\timit\\mfcc\\andriod_mfcc\\train_covs";
    public String modelStoragePath="D:\\uni\\project\\code\\Speaker-Recognition-System-using-GMM-master" +
            "\\Speaker-Recognition-System-using-GMM-master\\timit\\mfcc\\andriod_mfcc\\model";
    public static String georgeMFCCPath="D:\\uni\\project\\code\\Speaker-Recognition-System-using-GMM-master" +
            "\\Speaker-Recognition-System-using-GMM-master\\timit\\mfcc\\andriod_mfcc\\mfcc\\George_andriod_MFCC.txt";
    public static String georgeModelPath="D:\\uni\\project\\code\\Speaker-Recognition-System-using-GMM-master" +
            "\\Speaker-Recognition-System-using-GMM-master\\timit\\mfcc\\andriod_mfcc\\model\\George_andriod_MFCC_model.txt";
    public int featureLength=13;
    public String[] modelName;
    public int modelCount;
    final static int FeatureLength = 13;
    File folder = new File(traindWeightsStorageDir);


    public static void main(String[] args) throws FileNotFoundException {
        /*
        CreateModelFromPara cmdp=new CreateModelFromPara();
        cmdp.modelNameInFolder(cmdp.folder);
        for(int i=0;i<cmdp.modelCount;i++){
            cmdp.CreateModel(cmdp.modelName[i]);
        }*/
        System.out.printf("%f",CreateModelFromPara.GeorgeResult());

    }
    public  void modelNameInFolder(final File folder) {
        modelCount=0;
        File[] files = folder.listFiles();
        modelName= new String[files.length];
        for (int i = 0; i < files.length; i++)
        {
            modelName[modelCount]=new String(files[i].getName());
            modelCount++;
        }
    }
    public void CreateModel(String trainedFileName) throws FileNotFoundException {
        double[] weights = new double[CompononetsNum];
        Matrix[] means = new Matrix[CompononetsNum];
        Matrix[] covs = new Matrix[CompononetsNum];
        String traindWeightsStoragePath = traindWeightsStorageDir + File.separator + trainedFileName;
        String traindMeansStoragePath = traindMeansStorageDir + File.separator + trainedFileName;
        String traindCovsStoragePath = traindCovsStorageDir + File.separator + trainedFileName;
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

        for (int i = 0; i < CompononetsNum; i++) {
            double[][] tempComponent = new double[featureLength][1];
            for (int j = 0; j < featureLength; j++) {
                if (srcMeans.hasNextDouble()) {
                    tempComponent[j][0] = srcMeans.nextDouble();
                }
            }
            means[i] = new Matrix(tempComponent);
        }
        for (int i = 0; i < CompononetsNum; i++) {
            double[][] tempComponent = new double[featureLength][featureLength];
            for (int j = 0; j < featureLength; j++) {
                for (int k = 0; k < featureLength; k++) {
                    if (srcCovs.hasNextDouble()) {
                        tempComponent[j][k] = srcCovs.nextDouble();
                    }
                }
            }
            covs[i] = new Matrix(tempComponent);
        }
        //construct gmm model
        GaussianMixture gmmModel = new GaussianMixture(weights, means, covs);
        gmmModel.writeGMM(modelStoragePath+'/'+trainedFileName);
    }
    static public double GeorgeResult(){
        PointList mfccFeature = new PointList(FeatureLength);
        GaussianMixture gmmModel=GaussianMixture.readGMM(georgeModelPath);
        double[] mfccFeatureDouble= new double[FeatureLength];
        Scanner scan = new Scanner(georgeMFCCPath);
        if(scan.hasNextDouble())
        {
            for(int i=0;i<FeatureLength;i++)
            {
                mfccFeatureDouble[i]=scan.nextDouble();
            }
            mfccFeature.add(mfccFeatureDouble);
        }
        double likelihood=Math.exp(gmmModel.getLogLikelihood(mfccFeature));
        return  likelihood;

    }

}
