public class ArrayAnalyzer {
    public static boolean isMountainArray(int[] array) {
        if (array == null || array.length < 3) {
            return false;
        }

        int peakIndex = 0;
        while (array[peakIndex] < array[peakIndex + 1]) {
            ++peakIndex;
        }

        for (int i = peakIndex; i < array.length - 1; ++i) {
            if (array[i] <= array[i + 1]) {
                return false;
            }
        }

        return true;
    }
}
