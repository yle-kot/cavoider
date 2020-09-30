import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;

public class Extractor {
    public static void main(String[] args){
        List<String> lines = new ArrayList<String>();
        lines.add("{\n");
        String line = null;
        String coords = "";
        boolean isFIPS = false;
        boolean isCoords = false;
        boolean isLast = false;
        try {
            File json = new File("/home/jared/Desktop/cb_2018_us_county_500k/FIPS.json");
            File f1 = new File("/home/jared/Desktop/cb_2018_us_county_500k/cb_2018_us_county_500k.kml");
            FileReader fr = new FileReader(f1);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if(line.contains("St. Thomas")){
                    isLast = true;
                }
                if(line.contains("<SimpleData name=\"GEOID\">")) {
                    isFIPS = true;
                    line = line.substring(25,30);
                    line = "\"" + line + "\":";
                    line += "\n";
                    lines.add(line);
                }
                if(line.contains("<coordinates>") && isFIPS) {
                    isCoords = true;
                    line = line.replace("<coordinates>", "");
                    coords = "\"" + line;
                }
                if (line.contains("</") && isCoords) {
                    isFIPS = false;
                    isCoords = false;
                    line = line.replace("</coordinates>", "");
                    line = line.replace("</","");
                    coords += line + "\"";
                    if(isLast){
                        coords += "\n}";
                    }
                    else{
                        coords += ",\n";
                    }
                    lines.add(coords);
                }
                if(!line.contains("coordinates") && isCoords){
                    coords += line;
                }
            }
            fr.close();
            br.close();

            FileWriter fw = new FileWriter(json);
            BufferedWriter out = new BufferedWriter(fw);
            for(String s : lines)
                out.write(s);
            out.flush();
            fw.flush();
            out.close();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
