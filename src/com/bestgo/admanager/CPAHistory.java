package com.bestgo.admanager;

import com.bestgo.admanager.utils.Utils;

import java.io.*;
import java.util.HashMap;

public class CPAHistory {
    public static class CPAItem {
        public String countryCode;
        public double revenue;
        public long installed;
        public double rpi;// revenue / installed
        public double cpa;
    }

    public static HashMap<String, HashMap<String, CPAItem>> historyMaps = new HashMap<>();

    public static void loadFromFile(String filePath) {
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] values = line.split(",");
                if (values.length == 6) {
                    String appId = values[0];
                    String countryCode = values[1];
                    HashMap<String, CPAItem> history = historyMaps.get(appId);
                    if (history == null) {
                        history = new HashMap<>();
                        historyMaps.put(appId, history);
                    }
                    CPAItem item = history.get(countryCode);
                    if (item == null) {
                        item = new CPAItem();
                        history.put(countryCode, item);
                    }
                    item.countryCode = countryCode;
                    item.revenue = Utils.parseDouble(values[2], 0);
                    item.installed = Utils.parseInt(values[3], 0);
                    item.rpi = Utils.parseDouble(values[4], 0);
                    item.cpa = Utils.parseDouble(values[5], 0);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
        }
    }
}
