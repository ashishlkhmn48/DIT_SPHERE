package com.ashishlakhmani.dit_sphere.classes;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordsFilter {

    private List<String> readData(InputStreamReader fr) {

        List<String> list = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                list.add(line.trim());
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return list;
    }


    public String filteredString(String message, InputStreamReader fr) {

        List<String> list = readData(fr);

        String temp = message.toLowerCase();
        char[] arr = message.toCharArray();

        for (String val : list) {
            if (temp.contains(val.toLowerCase())) {
                int index = temp.indexOf(val);
                StringBuilder sb = new StringBuilder("");

                for(int i = index ;i <index+val.length();i++){
                    arr[i] = '*';
                }
            }
        }
        return new String(arr);
    }

}
