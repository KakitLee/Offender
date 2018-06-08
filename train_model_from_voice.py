from __future__ import division
import numpy as np
from pyAudioAnalysis import audioBasicIO
import Audio_Feature_Extraction as AFE
from sklearn import mixture
from sklearn.externals import joblib
import glob
import os
import new_mfcc
from shutil import copyfile
from scipy.fftpack import fft, ifft
from pyAudioAnalysis import audioFeatureExtraction


if __name__ == "__main__":

    window = 0.020
    #window_overlap = 0.010
    window_overlap = 0.010
    voiced_threshold_mul = 0.05
    voiced_threshold_range = 100
    n_mixtures = 32
    max_iterations = 200  # 75
    calc_deltas = False


    input_file_name="lqy_test_001_big.wav"
    speaker_name=input_file_name.replace('.wav', '')
    output_mfcc_file_name="mfcc//"+speaker_name+"_mfcc.txt"
    [Fs,x] = audioBasicIO.readAudioFile(input_file_name)
    #x = (x.astype(np.int32)/32767).astype(np.float32)
    features = new_mfcc.mfcc(x, Fs, 0.02, 0.01, 13, 27, 160, 133, 4000, 0, 0, False)
    with open(output_mfcc_file_name, 'wb') as f:
        for line in features:
            np.savetxt(f, line)

    try:
        gmm = mixture.BayesianGaussianMixture(n_components=n_mixtures, covariance_type='diag', max_iter=max_iterations).fit(
            features)
        np.savetxt('train_weights\\' + speaker_name + '.txt',gmm.weights_)
        mat_means = np.matrix(gmm.means_)
        with open('train_means\\' + speaker_name + '.txt', 'wb') as f:
            for line in mat_means:
                np.savetxt(f, line)
        with open('train_covs\\' + speaker_name + '.txt', 'wb') as f:
            mat_covs_all=[]
            for line in gmm.covariances_:
                mat_covs = np.diag(line)
                mat_covs_all.append(mat_covs)
            for line in mat_covs_all:
                np.savetxt(f, line)

    except:
        print "ERROR : Error while training model for file " + speaker_name
    try:
        joblib.dump(gmm, 'train_models\\' + speaker_name + '.pkl')
    except:
        print "ERROR : Error while saving model for " + speaker_name







