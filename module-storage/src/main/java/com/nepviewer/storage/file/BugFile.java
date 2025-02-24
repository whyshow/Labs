package com.nepviewer.storage.file;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BugFile {

    /**
     * 写入 Map 到 JSON 文件
     * @param mapData
     */
    public static void writeMapToJsonFile(Context context, Map<String, String> mapData) {
        try {
            File file = new File(context.getExternalFilesDir("/data/bug/"), "bug.json");
            //使用 FileWriter 以追加模式打开文件
            FileWriter fileWriter = new FileWriter(file, true);
            // 将 Map 转换为 JSON 对象
            JSONObject jsonObject = new JSONObject(mapData);
            // 将 JSON 对象写入文件，并在前面添加换行符
            fileWriter.write(System.lineSeparator() + jsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
            System.out.println("Map 数据已成功写入 JSON 文件！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取 JSON 文件中的 Map
     * @return
     */
    public static Map<String, String> readMapFromJsonFile(Context context) {
        Map<String, String> mapData = new HashMap<>();
        try {
            File file = new File(context.getExternalFilesDir("/data/bug/"), "bug.json");
            // 读取 JSON 文件
            StringBuilder jsonStringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            bufferedReader.close();
            // 将 JSON 字符串转换为 JSON 对象
            JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());
            // 将 JSON 对象转换为 Map
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                mapData.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapData;
    }
}
