import numpy as np
from scipy import signal
from scipy.signal import filtfilt
from scipy.interpolate import interp1d
import os
os.environ["PATH"] = ":".join([p for p in os.environ["PATH"].split(":")
                               if os.access(p, os.R_OK | os.X_OK)])
import matplotlib.pyplot as plt
from android.os import Environment


class HeartStats:
    def detect_peaks(signal, mov_avg):
        window = []
        peaklist = []
        for (i, datapoint), roll in zip(enumerate(signal), mov_avg):
            if (datapoint <= roll) and (len(window) <= 1):
                continue
            elif (datapoint > roll):
                window.append(datapoint)
            else:
                beatposition = i - len(window) + np.argmax(window)
                peaklist.append(beatposition)
                window = []
        return peaklist, [signal[x] for x in peaklist]

    def peaks_arr(fs, mt, ms, sign, w=np.array([.5, 1., 1.5, 2.]), time_lower_bound=0.5):
        peaks = np.arange(len(ms))  # initializing peaks index
        # making peak detection using 4 different sliding windows, their width is in fs units
        for i in w:  # widths
            mt_new, mov_avg = HeartStats.m_avg(mt, ms, int(fs * i))
            len_filler = np.zeros((len(ms) - len(mov_avg)) // 2) + np.mean(
                ms)  # used to make mov_avg the same size as sign
            mov_avg = np.insert(mov_avg, 0, len_filler)
            mov_avg = np.append(mov_avg, len_filler)

            peaklist, sign_peak = HeartStats.detect_peaks(ms, mov_avg)
            peaks = np.intersect1d(peaks,
                                   peaklist)  # keeping only peaks detected with all 4 different windows

        # peaks' checking: rejecting lower peaks where RR distance is too small
        final_peaks = [] # definitive peaks positions container
        last_peak = -1  # control parameter to avoid undesired peaks still in final_peaks
        for p in peaks:
            if p <= last_peak:
                continue
            # peaks evaluated t once, only 1 of them will be kept in final_peaks
            evaluated_peaks = [g for g in peaks if p <= g <= fs * time_lower_bound + p ]
            last_peak = max(evaluated_peaks)
            final_peaks.append(evaluated_peaks[np.argmax([sign[x] for x in evaluated_peaks])])

        final_peaks = np.array(np.unique(final_peaks))  # to avoid repetition of identical elements
        if final_peaks[0] == 0:
            final_peaks = final_peaks[1:]

        # computation of quality coefficient
        grad = np.gradient(ms)  # gradient of signal
        checker = np.multiply(grad[:-1], grad[1:])  # equals to grad_i * grad_i+1
        # "+2" is just to avoid negative numbers
        quality = np.var([ms[x] for x in final_peaks]) * (
                len(checker[checker < 0]) - 2 * len(final_peaks) + 2) / len(checker)

        ## Calculating rate
        time_peaks = np.array(mt)[final_peaks.astype(int)]

        # time between consecutive R peaks (in milliseconds)
        time_RR = np.diff(time_peaks, 1, 0, ) * 1e3

        # Removing RR intervals which missed a peak or detected additional peaks
        ibi = np.mean(time_RR)
        time_RR = np.array([t for t in time_RR if 0.5*ibi < t < 1.35*ibi])
        ibi = np.mean(time_RR)  # mean Inter Beat Interval
        rate = 60000 / ibi  # mean bpm
        return time_RR, rate, quality

    m_avg = lambda t, x, w: (np.asarray([t[i] for i in range(w, len(x) - w)]),
                             np.convolve(x, np.ones((2 * w + 1,)) / (2 * w + 1),
                                         mode='valid'))

    def cheby_filter(data, fs, kind='B', low_f=0.5, high_f=4, order=4):
        fn = 0.5 * fs
        if kind=='L':
            b,a = signal.cheby2(order, 20, low_f/fn, 'lowpass')
        elif kind=='H':
            b,a = signal.cheby2(order, 20, high_f/fn, 'highpass')
        else:
            b,a = signal.cheby2(order, 20, np.array([low_f, high_f])/fn, 'bandpass')
        return filtfilt(b, a, data)

    def processing_data(data, time):
        fs = len(time)/(max(time)-min(time))
        data = HeartStats.cheby_filter(data, fs)
        w_size = int(fs * .5)  # width of moving window
        mt, ms = HeartStats.m_avg(time, data, w_size)  # computation of moving average
        sign = (data[w_size: -w_size] - ms)
        analytical_signal = np.abs(signal.hilbert(sign))
        w_size = int(fs)
        mt_new, mov_avg = HeartStats.m_avg(mt, analytical_signal, w_size)
        signal_pure = sign[w_size: -w_size] / mov_avg
        return mt_new, signal_pure, HeartStats.peaks_arr(fs, mt_new, signal_pure, sign)

    def HR_stats_helper(data1, data2, time):
        mt, signal_pure, result = HeartStats.processing_data(data1, time)
        mt2, signal_pure2, result2 = HeartStats.processing_data(data2, time)
        if result2[2] < result[2]:
            signal_pure = signal_pure2
            result = result2
            mt = mt2
        RR, bpm, quality = result
        RR_diff = np.abs(np.diff(RR, 1, 0))  # time variation between consecutive RR intervals
        ibi = np.mean(RR)  # mean Inter Beat Interval
        bpm = 60000 / ibi  # mean bpm
        sdnn = np.std(RR)  # Take standard deviation of all R-R intervals
        rmssd = np.sqrt(np.mean(
            RR_diff ** 2))  # Take the square root of the mean of the list of squared differences

        result = [bpm, sdnn, 0.0, quality]
        return result, signal_pure, mt

    def HR_stats(self, data1, data2, time):
        time = np.array(time) - time[0]
        time = (1.0*time)
        time = time/1000
        f1 = interp1d(time, data1, kind='linear')
        f2 = interp1d(time, data2, kind='linear')

        time_int = np.linspace(0, max(time), num=3000)

        result, signal_pure, mt = HeartStats.HR_stats_helper(f1(time_int), f1(time_int), time_int)
        fname = str(Environment.getExternalStorageDirectory())
        fname += "/Pictures/ppg.jpg"

        signal_pure = list(signal_pure)
        mt = list(mt)

        a = int(len(mt)/2)
        plt.figure(figsize=(6,3))
        plt.subplot(211)
        plt.plot(mt[0:a], signal_pure[0:a], 'r-')
        plt.xlabel('Time (s)')
        plt.yticks([])
        plt.subplot(212)
        plt.plot(mt[a:2*a], signal_pure[a:2*a], 'r-')
        plt.xlabel('Time (s)')
        plt.yticks([])
        plt.savefig(fname, format='png', bbox_inches = "tight")
        return result