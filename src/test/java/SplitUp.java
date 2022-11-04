import org.junit.jupiter.api.Test;

import java.io.*;

public class SplitUp {

    public void setup() {
        /*
        - Renamed files

        -Added header "Salary" for columns:
            static readonly SALARY_INDEX_2015 = 106
            static readonly SALARY_INDEX_2018 = 53

         */
    }

    @Test
    public void createData() throws IOException {

        String[] fileNames = new String[]{
            "2011.csv"
        };
        /*
        fileNames = new String[]{
                "2018.csv"
        };*/
        for (int i = 0; i < fileNames.length; i++) {
            createChunks(fileNames[i]);
        }
    }

    // https://stackoverflow.com/q/19635844/8524651
    private void createChunks(String fileName) throws IOException {
        String realFileName = fileName.replaceAll("_", " ");
        System.err.println("" + fileName);
        InputStreamReader fileReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(realFileName));
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line="";
        int fileSize = 0;
        int fileCounter = 1;
        BufferedWriter fos = new BufferedWriter(new FileWriter(getChunkName(fileName, fileCounter),true));
        String headerLine = "";
        boolean first = true;
        double mb10 = 9.5 * 1024 * 1024;
        while((line = bufferedReader.readLine()) != null) {
            if (first) {
                headerLine = line;
                first = false;
            }
            if(fileSize + line.getBytes().length > mb10){
                fileCounter++;
                fos.flush();
                fos.close();
                fos = new BufferedWriter(new FileWriter(getChunkName(fileName, fileCounter),true));
                fos.write(headerLine+"\n");
                fos.write(line+"\n");
                fileSize = line.getBytes().length;
            }else{
                fos.write(line+"\n");
                fileSize += line.getBytes().length;
            }
        }
        fos.flush();
        fos.close();
        bufferedReader.close();
    }

    private String getChunkName(String fileName, int counter) {
        return fileName.replace(".csv", "") + "-chunk-" + counter + ".csv";
    }

}
