package soundrecorder;

/*
 *      _______                       _____   _____ _____
 *     |__   __|                     |  __ \ / ____|  __ \
 *        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
 *        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/
 *        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |
 *        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|
 *
 * -------------------------------------------------------------
 *
 * TarsosDSP is developed by Joren Six at IPEM, University Ghent
 *
 * -------------------------------------------------------------
 *
 *  Info: http://0110.be/tag/TarsosDSP
 *  Github: https://github.com/JorenSix/TarsosDSP
 *  Releases: http://0110.be/releases/TarsosDSP/
 *
 *  TarsosDSP includes modified source code by various authors,
 *  for credits and info, see README.
 *
 */


import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;

//import com.danielkim.soundrecorder.AudioEvent;


public class MFCC implements AudioProcessor {

    private int amountOfCepstrumCoef; //Number of MFCCs per frame
    protected int amountOfMelFilters; //Number of mel filters (SPHINX-III uses 40)
    protected float lowerFilterFreq; //lower limit of filter (or 64 Hz?)
    protected float upperFilterFreq; //upper limit of filter (or half of sampling freq.?)

    float[] audioFloatBuffer;
    //Er zijn evenveel mfccs als er frames zijn!?
    //Per frame zijn er dan CEPSTRA coëficienten
    private float[] mfcc;

    int centerFrequencies[];

    private FFT fft;
    private int samplesPerFrame;
    private float sampleRate;

    public MFCC(int samplesPerFrame, int sampleRate){
        this(samplesPerFrame, sampleRate, 30, 30, 133.3334f, ((float)sampleRate)/2f);
        calculateFilterBanks();
    }

    public MFCC(int samplesPerFrame, float sampleRate, int amountOfCepstrumCoef, int amountOfMelFilters, float lowerFilterFreq, float upperFilterFreq) {
        this.samplesPerFrame = samplesPerFrame;
        this.sampleRate = sampleRate;
        this.amountOfCepstrumCoef = amountOfCepstrumCoef;
        this.amountOfMelFilters = amountOfMelFilters;
        //this.fft = new FFT(samplesPerFrame, new HammingWindow());
        this.fft = new FFT(samplesPerFrame);
        this.lowerFilterFreq = Math.max(lowerFilterFreq, 25);
        this.upperFilterFreq = Math.min(upperFilterFreq, sampleRate / 2);

        calculateFilterBanks();

    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        //Debug.waitForDebugger();
        audioFloatBuffer = audioEvent.getFloatBuffer().clone();
//		for (int i = 0; i< audioFloatBuffer.length; i++){
//			System.out.print(Math.round(audioFloatBuffer[i]*100) + " - ");
//		}
        System.out.println();
        // Magnitude Spectrum
        float bin[] = magnitudeSpectrum(audioFloatBuffer);
        // get Mel Filterbank
        float fbank[] = melFilter(bin, centerFrequencies);
        // Non-linear transformation
        float f[] = nonLinearTransformation(fbank);
        // Cepstral coefficients
        mfcc = cepCoefficients(f);
        return true;
    }

    @Override
    public void processingFinished() {

    }

    /**
     * computes the magnitude spectrum of the input frame<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param frame Input frame signal
     * @return Magnitude Spectrum array
     */
    public float[] magnitudeSpectrum(float frame[]){
        //Debug.waitForDebugger();
        float magSpectrum[] = new float[frame.length];

        // calculate FFT for current frame

        fft.forwardTransform(frame);//add window,haming

        // calculate magnitude spectrum
        for (int k = 0; k < frame.length/2; k++){
            magSpectrum[frame.length/2+k] = fft.modulus(frame, frame.length/2-1-k)/frame.length*2;
            magSpectrum[frame.length/2-1-k] = magSpectrum[frame.length/2+k];
        }

        return magSpectrum;
    }

    /**
     * calculates the FFT bin indices<br> calls: none<br> called by:
     * featureExtraction
     *
     */

    public final void calculateFilterBanks() {
        centerFrequencies = new int[amountOfMelFilters + 2];

        centerFrequencies[0] = (int) Math.floor(lowerFilterFreq / sampleRate * (samplesPerFrame+1));
        centerFrequencies[centerFrequencies.length - 1] = (int) (samplesPerFrame / 2);

        double mel[] = new double[2];
        mel[0] = freqToMel(lowerFilterFreq);
        mel[1] = freqToMel(upperFilterFreq);

        double factor = (double)((mel[1] - mel[0]) / (amountOfMelFilters + 1));
        //Calculates te centerfrequencies.
        for (int i = 1; i <= amountOfMelFilters; i++) {
            double fc = (inverseMel(mel[0] + factor * i) / sampleRate) * (samplesPerFrame+1);
            centerFrequencies[i] = (int) Math.floor(fc);
        }
        centerFrequencies[11]=18;//Just for test,remember to remove
        int testPoint=1;
    }


    /**
     * the output of mel filtering is subjected to a logarithm function (natural logarithm)<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param fbank Output of mel filtering
     * @return Natural log of the output of mel filtering
     */
    public float[] nonLinearTransformation(float fbank[]){
        float f[] = new float[fbank.length];
        final float FLOOR = -50;

        for (int i = 0; i < fbank.length; i++){
            f[i] = (float) Math.log10(fbank[i]);

            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) f[i] = FLOOR;
        }

        return f;
    }

    /**
     * Calculate the output of the mel filter<br> calls: none called by:
     * featureExtraction
     * @param bin The bins.
     * @param centerFrequencies  The frequency centers.
     * @return Output of mel filter.
     */
    public float[] melFilter(float bin[], int centerFrequencies[]) {
        float temp[] = new float[amountOfMelFilters + 2];

        for (int k = 1; k <= amountOfMelFilters; k++) {
            float num1 = 0, num2 = 0;

            //float den = (centerFrequencies[k] - centerFrequencies[k - 1] + 1);
            float den = centerFrequencies[k] - centerFrequencies[k - 1];

            /*
            for (int i = centerFrequencies[k - 1]; i <= centerFrequencies[k]; i++) {
                num1 += bin[i] * (i - centerFrequencies[k - 1] + 1);
            }*/

            for (int i = centerFrequencies[k - 1]; i < centerFrequencies[k]; i++) {
                num1 += bin[i] * (i - centerFrequencies[k - 1] );
            }


            num1 /= den;

            //den = (centerFrequencies[k + 1] - centerFrequencies[k] + 1);
            den = centerFrequencies[k + 1] - centerFrequencies[k];
            /*

            for (int i = centerFrequencies[k] + 1; i <= centerFrequencies[k + 1]; i++) {
                num2 += bin[i] * (1 - ((i - centerFrequencies[k]) / den));
            }
            */
            for (int i = centerFrequencies[k]; i < centerFrequencies[k + 1]; i++) {
                num2 += bin[i] * ( centerFrequencies[k+1]-i);
            }
            num2 /= den;
            temp[k] = num1 + num2;
        }

        float fbank[] = new float[amountOfMelFilters];

        for (int i = 0; i < amountOfMelFilters; i++) {
            fbank[i] = temp[i + 1];
        }

        return fbank;
    }


    /**
     * Cepstral coefficients are calculated from the output of the Non-linear Transformation method<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param f Output of the Non-linear Transformation method
     * @return Cepstral Coefficients
     */
    public float[] cepCoefficients(float f[]){
        float cepc[] = new float[amountOfCepstrumCoef];
        double f_[] = new double[amountOfCepstrumCoef];
        f_[0]=1/ Math.sqrt((double)f.length);
        for(int i = 1;i<amountOfCepstrumCoef;i++)
            f_[i]= Math.sqrt(2/(double)f.length);
        for (int i = 0; i < cepc.length; i++){
            double temp_j=0;
            for (int j = 0; j < f.length; j++){
              temp_j+=f[j] * Math.cos(Math.PI *i*(j+0.5)/(double)f.length);
            }
            cepc[i]= (float) (f_[i]*temp_j);
        }

        return cepc;
    }

//    /**
//     * calculates center frequency<br>
//     * calls: none<br>
//     * called by: featureExtraction
//     * @param i Index of mel filters
//     * @return Center Frequency
//     */
//    private static float centerFreq(int i,float samplingRate){
//        double mel[] = new double[2];
//        mel[0] = freqToMel(lowerFilterFreq);
//        mel[1] = freqToMel(samplingRate / 2);
//
//        // take inverse mel of:
//        double temp = mel[0] + ((mel[1] - mel[0]) / (amountOfMelFilters + 1)) * i;
//        return inverseMel(temp);
//    }

    /**
     * convert frequency to mel-frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param freq Frequency
     * @return Mel-Frequency
     */
    protected static double freqToMel(double freq){
        return (double) (2595 * log10(1 + freq / 700));
    }

    /**
     * calculates the inverse of Mel Frequency<br>
     * calls: none<br>
     * called by: featureExtraction
     */
    private static double inverseMel(double x) {
        return (double) (700 * (Math.pow(10, x / 2595) - 1));
    }

    /**
     * calculates logarithm with base 10<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param value Number to take the log of
     * @return base 10 logarithm of the input values
     */
    protected static double log10(double value){
        return (double) (Math.log(value) / Math.log(10));
    }

    public float[] getMFCC() {
        return mfcc.clone();
    }

    public int[] getCenterFrequencies() {
        return centerFrequencies;
    }
}
