package advprog.weather.bot.controller;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.neovisionaries.i18n.CountryCode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@LineMessageHandler
public class WeatherController {

    private static final Logger LOGGER = Logger.getLogger(WeatherController.class.getName());
    private Set<String> slashWeather = new HashSet<>();
    private final String baseUrl = "api.openweathermap.org/data/2.5/weather?";
    private final String apiKey = "appid=187bd7d86855cb7ee05515d77863583d";


    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        LOGGER.fine(String.format("TextMessageContent(timestamp='%s',content='%s')",
                event.getTimestamp(), event.getMessage()));

        String content = event.getMessage().getText();
        Source source = event.getSource();
        if (!(source instanceof GroupSource)) {
            if (content.equals("/weather")) {
                slashWeather.add(source.getSenderId());
                return new TextMessage("Please submit a location straightaway "
                        + "with Line's 'Share location' feature below. ☟");
            } else if (content.toLowerCase().contains("/configure_weather")) {

            }

        } else {
            if (content.toLowerCase().contains("cuaca di")) {
                ArrayList<String> inputSplit = new ArrayList<>();
                inputSplit.addAll(Arrays.asList(content.toLowerCase().split(" ")));
                int indexKeyWord = inputSplit.indexOf("cuaca");
                String city = inputSplit.get(indexKeyWord + 2);
                String url = baseUrl + apiKey + "&q=" + city;
                ArrayList<String> requiredDatas = fetchDataApiRequest(url);
                return new TextMessage(textResponseFormatter(requiredDatas));
            }
        }

        String replyText = content.replace("/echo", "");
        return new TextMessage(replyText.substring(1));
    }

    @EventMapping
    public TextMessage handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
        LOGGER.fine(String.format("LocationMessageContent(timestamp='%s',content='%s')",
                event.getTimestamp(), event.getMessage().getTitle()));
        LocationMessageContent content = event.getMessage();
        Source source = event.getSource();
        if (!(source instanceof GroupSource) & slashWeather.contains(source.getSenderId())) {
            slashWeather.remove(source.getSenderId());
            String latitude = Double.toString(content.getLatitude());
            String longitude = Double.toString(content.getLongitude());
            String url = baseUrl + apiKey + "&lat=" + latitude + "&lon=" + longitude;
            ArrayList<String> requiredDatas = fetchDataApiRequest(url);
            return new TextMessage(textResponseFormatter(requiredDatas));
        }
        return null;
    }

    private ArrayList<String> fetchDataApiRequest(String url) {
        ArrayList<String> data = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        String stringifiedJson = restTemplate.getForObject(url, String.class, header);
        try {
            JSONObject parsedJson = new JSONObject(stringifiedJson);
            data.add(parsedJson.getString("name"));         // City name
            String countryCode = parsedJson.getJSONObject("sys").getString("country");
            data.add(CountryCode.getByCode(countryCode).getName()); // Country name
            JSONObject weatherObject = parsedJson.getJSONArray("weather").getJSONObject(0);
            data.add(weatherObject.getString("main"));      // Main weather condition
            data.add(parsedJson.getJSONObject("wind").getString("speed"));   // Wind Speed (m/s)
            data.add(parsedJson.getJSONObject("main").getString("temp"));   // Temperature (K)
            data.add(parsedJson.getJSONObject("main").getString("humidity"));   // Humidity
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String textResponseFormatter(ArrayList<String> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Weather at your position (");
        sb.append(datas.get(0)).append(", ");
        sb.append(datas.get(1)).append("):\n");
        sb.append(datas.get(2)).append(" *icon\n");
        sb.append("Wind speed : ");
        sb.append(datas.get(3)).append(" meter/sec\n");
        sb.append("Temperature : ");
        sb.append(datas.get(4)).append(" Kelvin\n");
        sb.append("Humidity : ");
        sb.append(datas.get(5)).append("%");
        return sb.toString();
    }
}
