package com.example.Assignment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TimeStoriesFetcher {

    @GetMapping("/getTimeStories")
    public List<Map<String, String>> getTimeStories() {
        List<Map<String, String>> storiesList = new ArrayList<>();
        try {
            
            String urlString = "https://time.com/"; 
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            String htmlContent = content.toString();

        
            String startMarker = "<ul class=\"featured-voices__list swipe-h\">";
            String endMarker = "</ul>";

            int startIndex = htmlContent.indexOf(startMarker);
            int endIndex = htmlContent.indexOf(endMarker, startIndex);

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                String extractedData = htmlContent.substring(startIndex + startMarker.length(), endIndex);

                String[] stories = extractedData.split("<li class=\"featured-voices__list-item\">");
                for (int i = 1; i < stories.length && i <= 6; i++) { 
                    String story = stories[i];

                    String titleStart = "<h3 class=\"featured-voices__list-item-headline display-block\">";
                    String titleEnd = "</h3>";
                    int titleStartIndex = story.indexOf(titleStart);
                    int titleEndIndex = story.indexOf(titleEnd, titleStartIndex);

                    String title = "";
                    if (titleStartIndex != -1 && titleEndIndex != -1) {
                        title = story.substring(titleStartIndex + titleStart.length(), titleEndIndex).trim();
                    }

                   
                    String linkStart = "<a href=\"";
                    String linkEnd = "\">";
                    int linkStartIndex = story.indexOf(linkStart);
                    int linkEndIndex = story.indexOf(linkEnd, linkStartIndex);

                    String link = "";
                    if (linkStartIndex != -1 && linkEndIndex != -1) {
                        link = story.substring(linkStartIndex + linkStart.length(), linkEndIndex).trim();
                    }

                    if (!title.isEmpty() && !link.isEmpty()) {
                        Map<String, String> storyData = new HashMap<>();
                        storyData.put("title", title);
                        storyData.put("link", "https://time.com" + link);
                        storiesList.add(storyData);
                    }
                }
            } else {
                System.out.println("Failed to find the specific part of the HTML.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storiesList;
    }
}
