package com.example.demowithtests.util.http;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class HttpRequestSender {

//    not ready!!!!!!!!!!!!!

    public static void main(String[] args) throws Exception {

        String urlStr = "http://localhost:8087/api/users/mass-test-update/";
        String username = "admin";
        String password = "password";
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeaderValue = "Basic " + new String(encodedAuth);


//        ExecutorService executor = Executors.newFixedThreadPool(10); // Создаем пул потоков

        for (int i = 0; i < 1000; i++) {
//            final int j = i;
//            executor.execute(() -> {
//                try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", authHeaderValue);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write("updated data");
            writer.flush();
            writer.close();

            int responseCode = conn.getResponseCode();
            // обработка ответа
//                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    String inputLine;
//                    StringBuffer response = new StringBuffer();
//
//                    while ((inputLine = in.readLine()) != null) {
//                        response.append(inputLine);
//                    }
//                    in.close();


//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

//            executor.execute(() -> {
//                try {
//                    URL url = new URL(urlStr);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("PATCH");
//                    conn.setRequestProperty("Content-Type", "application/json");
//                    conn.setDoOutput(true);
//
//                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
//                    writer.write("updated data");
//                    writer.flush();
//                    writer.close();
//
//                    int responseCode = conn.getResponseCode();
//                    // обработка ответа
////                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
////                    String inputLine;
////                    StringBuffer response = new StringBuffer();
////
////                    while ((inputLine = in.readLine()) != null) {
////                        response.append(inputLine);
////                    }
////                    in.close();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }

//            executor.shutdown();
//            while (!executor.isTerminated()) {
//            }
//            System.out.println("Все задачи выполнены");
        }
    }

}
