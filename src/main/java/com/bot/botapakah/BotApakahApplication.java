package com.bot.botapakah;
import ch.qos.logback.core.net.server.Client;
import com.bot.Getter.Getter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.tomcat.util.codec.binary.Base64;
import java.util.logging.Logger;

import static java.sql.DriverManager.println;

@SpringBootApplication
@LineMessageHandler
public class BotApakahApplication extends SpringBootServletInitializer {
    Client client;
    String data;
    String[] myData;
    String myDataTags;

    private static final Logger LOGGER = Logger.getLogger(BotApakahApplication.class.getName());


    @Autowired
    private LineMessagingClient lineMessagingClient;

    private static String AccessToken = "Access Token here";
    Getter getter = new Getter(AccessToken);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BotApakahApplication.class);
    }

    public static void main(String[] args) {

        SpringApplication.run(BotApakahApplication.class, args);
    }

//    private static byte[] fetchFromApi(String arg) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "cnoisYhDmE7nZtWJwc2Z0hkr9IPv6Sgw/YNrhyX92ztPeMcjnJQmr27TR2ebk8t00NJg4OBp0Y890hkN1zEJGE2tY94bZJn3l5IEOZXnKg/6nEI0uQWU3mdmOgm/R0Ml7FizuBiFOtz/DyMF/3uwfjAdB04t89/1O/w1cDnyilFU=");
//
//        HttpEntity<String> request = new HttpEntity<>(headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<byte[]> response = restTemplate.exchange(
//                "https://api.line.me/v2/bot/message/"+arg+"/content",
//                HttpMethod.GET,
//                request,
//                byte[].class
//        );
//
//        return response.getBody();
//    }

    public String nembakApiDefault(String input) throws IOException, JSONException {
        String credentialsToEncode = "acc_5923548cfcdb7aa" + ":" + "b847a86333f34d3d417f456283b9462d";
        String basicAuth = java.util.Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));
        String endpoint_url = "https://api.imagga.com/v1/colors";
        String image_url = input;

        String url = endpoint_url + "?url=" + image_url;
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", "Basic " + basicAuth);

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        System.out.println(jsonResponse);

        return getta(jsonResponse);

    }

    public String getta(String inputjson) throws JSONException {

        JSONObject json = new JSONObject(inputjson);
        String percentagefg = json.getJSONArray("results").getJSONObject(0).getJSONObject("info").getJSONArray("foreground_colors").getJSONObject(0).getString("percentage");
        String dominantfg = json.getJSONArray("results").getJSONObject(0).getJSONObject("info").getJSONArray("foreground_colors").getJSONObject(0).getString("closest_palette_color");
        String dominant = json.getJSONArray("results").getJSONObject(0).getJSONObject("info").getJSONArray("background_colors").getJSONObject(0).getString("closest_palette_color");
        String percentage = json.getJSONArray("results").getJSONObject(0).getJSONObject("info").getJSONArray("background_colors").getJSONObject(0).getString("percentage");
        String hasil = "Color paling banyak background: " + dominant + " dengan hasil : " + percentage + "%" + "\n" +"Color paling banyak foreground: " + dominantfg + " dengan hasil : " + percentagefg + "%";
        return hasil;
    }




//    public String nembakApi(String id) {
//
//
//        String basic = getBasicAuth("acc_5923548cfcdb7aa", "b847a86333f34d3d417f456283b9462d");
//        RestTemplate restTemplate = new RestTemplate();
//        //String url = id;
//        String url = "https://api.line.me/v2/bot/message/"+id+"/content";
//
//        UriComponentsBuilder uri = UriComponentsBuilder
//                .fromUriString("https://api.imagga.com/v1/colors")
//                .queryParam("url", url);
//        LOGGER.info(uri.toUriString());
//
//        HttpHeaders header = new HttpHeaders();
//        header.add("Authorization", basic);
//
//        HttpEntity<String> request = new HttpEntity<String>(String.valueOf(header));
//        LOGGER.info(String.valueOf(request));
//        // String hasil = json.getJSONArray("results").getJSONObject(0).getJSONArray("categories").getJSONObject(0).getString("name");
//        ResponseEntity<String> response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET, request, String.class);
//        LOGGER.info(response.getBody());
//        return response.getBody();
//        //jsonnode
//    }

    static String getBasicAuth(String username, String password) {
        String auth = username + ":" + password;
        String auth64 = new String(Base64.encodeBase64(auth.getBytes()));
        LOGGER.info("THIS IS");

        return "Basic " + auth64;
    }


        @JsonIgnoreProperties(ignoreUnknown = true)
    class Response {
        Result[] results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Result {
        Info info;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Info {
        Color[] background_colors;
        Color[] foreground_colors;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Color {
        String closest_palette_color_html_code;
        double percentage;
    }

    @EventMapping
    public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent) throws IOException, JSONException {
        String pesan = messageEvent.getMessage().getText();
        String jawaban = nembakApiDefault(pesan);
        String replyToken = messageEvent.getReplyToken();
        balasChatDenganRandomJawaban(replyToken,jawaban);

//        if(pesanSplit[0].equals("apakah")){
//            String jawaban = getRandomJawaban();
//            String replyToken = messageEvent.getReplyToken();
//            LOGGER.info("INFO:");
//            LOGGER.info(String.valueOf(messageEvent.getMessage()));
//            LOGGER.info(messageEvent.getMessage().getId());
//            balasChatDenganRandomJawaban(replyToken, jawaban);
//        }
    }
//    @EventMapping
//    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> imageEvent) {
//        String imageid = imageEvent.getMessage().getId();
//        String replyToken = imageEvent.getReplyToken();
//        String jawaban = nembakApi(imageid);
//        balasChatDenganRandomJawaban(replyToken,jawaban);
//    }

    private String getRandomJawaban(){
        String jawaban = "";
        int random = new Random().nextInt();
        if(random%2==0){
            jawaban = "Ya";
        } else{
            jawaban = "Nggak";
        }
        return jawaban;
    }


    private void balasChatDenganRandomJawaban(String replyToken, String jawaban){
        TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Ada error saat ingin membalas chat");
        }
    }

}