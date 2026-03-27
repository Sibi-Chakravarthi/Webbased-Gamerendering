package world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapLoader {
    
    public static int[][] loadMap(String filePath) {
        try {
            String rawJson = new String(Files.readAllBytes(Paths.get(filePath)));
            
            rawJson = rawJson.replaceAll("\\s+", "");
            
            if (rawJson.startsWith("[[") && rawJson.endsWith("]]")) {
                rawJson = rawJson.substring(2, rawJson.length() - 2);
            }

            String[] stringRows = rawJson.split("\\],\\[");

            int numRows = stringRows.length;
            int numCols = stringRows[0].split(",").length;

            int[][] map = new int[numRows][numCols];

            for (int i = 0; i < numRows; i++) {

                String[] stringCols = stringRows[i].split(",");

                for (int j = 0; j < numCols; j++) {
                    map[i][j] = Integer.parseInt(stringCols[j]);
                }
            }

            for (int x = 0; x < numRows; x++) {

                for (int y = 0; y < numCols; y++) {

                    if (x == 0 || x == numRows - 1 || y == 0 || y == numCols - 1) {
                        
                        if (map[x][y] != 2 && map[x][y] != 3) {
                            map[x][y] = 1;
                        }
                    }
                }
            }
            
            System.out.println("✅ Successfully loaded map of size: " + numRows + "x" + numCols);
            return map;

        } catch (IOException e) {

            System.out.println("❌ Could not find the file: " + filePath);
            e.printStackTrace();

        } catch (Exception e) {

            System.out.println("❌ Error parsing the map data!");
            e.printStackTrace();

        }
        
        return null;
    }
}