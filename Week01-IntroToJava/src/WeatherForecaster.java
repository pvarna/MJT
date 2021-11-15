public class WeatherForecaster {
    public static int[] getsWarmerIn(int[] temperatures) {
        int[] result = new int[temperatures.length];

        for (int i = 0; i < temperatures.length; ++i) {
            boolean found = false;
            for (int j = i+1; j < temperatures.length; ++j) {
                if (temperatures[j] > temperatures[i]) {
                    found = true;
                    result[i] = j - i;
                    break;
                }
            }
            if (!found) {
                temperatures[i] = 0;
            }
        }

        return result;
    }
}
