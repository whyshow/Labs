package club.ccit.network;

public class NetworkConfig {
    private static String BaseUrl;
    private static boolean isDebug;

    public static String getBaseUrl() {
        return BaseUrl == null ? "" : BaseUrl.trim();

    }

    public static void setBaseUrl(String baseUrl) {
        BaseUrl = baseUrl == null ? "" : baseUrl;
    }

    public static boolean isIsDebug() {
        return isDebug;

    }

    public static void setIsDebug(boolean isDebug) {
        NetworkConfig.isDebug = isDebug;
    }
}
