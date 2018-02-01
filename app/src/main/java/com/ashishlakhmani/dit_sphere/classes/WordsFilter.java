package com.ashishlakhmani.dit_sphere.classes;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordsFilter {

    private Set<String> readData(InputStreamReader fr) {

        Set<String> set = new HashSet<>();

        try {
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                set.add(line.trim());
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return set;


    }


    public String filteredString(String message, InputStreamReader fr) {

        Set<String> set = readData(fr);
        char[] initialArray = message.toCharArray();

        for (String val : set) {

            int index;
            if ((index = message.toLowerCase().indexOf(val)) != -1) {

                for (int j = index; j < index + val.length(); j++) {
                    initialArray[j] = '*';
                }
            }
        }

        message = new String(initialArray);

        char[] arr = message.toCharArray();
        StringBuilder builder = new StringBuilder("");
        List<Integer> posList = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != ' ' &&
                    arr[i] != '.' &&
                    arr[i] != ',' &&
                    arr[i] != ':' &&
                    arr[i] != ';' &&
                    arr[i] != '\'' &&
                    arr[i] != '\"' &&
                    arr[i] != '^' &&
                    arr[i] != '-' &&
                    arr[i] != '_' &&
                    arr[i] != '=' &&
                    arr[i] != '+' &&
                    arr[i] != '~' &&
                    arr[i] != '`' &&
                    arr[i] != '/' &&
                    arr[i] != '\\' &&
                    arr[i] != '(' &&
                    arr[i] != ')' &&
                    arr[i] != '*') {
                builder.append(arr[i]);
                posList.add(i);
            }
        }

        for (String val : set) {
            int index;
            if ((index = builder.toString().toLowerCase().indexOf(val)) != -1) {

                for (int i = index; i < index + val.length(); i++) {
                    arr[posList.get(i)] = '*';
                }
            }
        }

        return new String(arr);
    }

}
