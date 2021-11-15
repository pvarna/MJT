public class PathUtils {
    public static String getCanonicalPath(String path) {
        String[] splitPath = path.split("/");

        String[] resultArray = new String[splitPath.length];
        int currentIndex = 0;
        for (int i = 0; i < splitPath.length; ++i) {
            if (splitPath[i].isBlank() || splitPath[i].equals(".")) {
                continue;
            }
            if (splitPath[i].equals("..")) {
                if (currentIndex > 0) {
                    --currentIndex;
                }
            } else {
                resultArray[currentIndex++] = splitPath[i];
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < currentIndex; ++i) {
            result.append("/").append(resultArray[i]);
        }
        if (currentIndex == 0) {
            result.append("/");
        }


        return result.toString();
    }
}
