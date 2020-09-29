import java.io.*;
import java.net.*;

public class FipsFinder {
    public static void main(String[] args) throws IOException {
        String baseUrl = "https://geo.fcc.gov/api/census/area?";
        String latitude = "lat=37.75&";
        String longitude = "lon=-77.85";
        baseUrl += latitude+longitude;
        String fips = "";
        URL url = new URL(baseUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        for(int i = 0;i<response.length();i++){
            if(response.substring(i,i+11).equals("county_fips")){
                fips = response.substring(i+14,i+19);
                break;
            }
        }
        //System.out.println(response.toString());
        System.out.println(fips);
    }
}
