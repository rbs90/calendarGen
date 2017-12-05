import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rbs on 21.11.16.
 */
public class BottomGenerator {

    private JsonArray feiertage = null;
    ArrayList<Date> feiertags_dates = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    
    public BottomGenerator(int year, String input, boolean upload, boolean ffw_dates) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println("Lese Feiertage...");
        try {
            URL url = new URL("http://www.spiketime.de/feiertagapi/feiertage/SN/" + year);
            Scanner scanner = new Scanner(url.openStream());

            String line = "";
            if(scanner.hasNextLine()){
                line = scanner.nextLine();
            }

            Gson gson = new Gson();
            feiertage = gson.fromJson(line, JsonArray.class);
            for(int i = 0; i < feiertage.size(); i++){
                String day = feiertage.get(i).getAsJsonObject().get("Datum").getAsString().split("T")[0];
                String name = feiertage.get(i).getAsJsonObject().get("Feiertag").getAsJsonObject().get("Name").getAsString();
                System.out.println(day + ": " + name);
                Date feiertag_date = simpleDateFormat.parse(day);
                Calendar cal = GregorianCalendar.getInstance(TimeZone.getDefault());
                cal.setTime(feiertag_date);
                cal.add(Calendar.HOUR, 12);
                feiertags_dates.add(cal.getTime());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Lese Bild-Titel");
        try {
            Scanner scanner = new Scanner(new File(input + "/text.txt"));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                titles.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        GregorianCalendar cal = new GregorianCalendar(2018, 0, 1, 12, 0, 0);

        //A3 Format ratio:
        float a3 = 297f/420f;
        int paper_width = 4000;
        int paper_height = (int) (paper_width * a3);
/*
        File soureDir = new File("source/");
        File[] photos = soureDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".JPG");
            }
        });
*/
        int distance = (4000 - 120)/32;

        for(int m = -1; m < 12; m++) {

            System.out.println("Erstelle Monat " + (m + 1));

            BufferedImage bufferedImage = new BufferedImage(paper_width, paper_height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();

            int bottom_height = 360;
            if(m == -1) // title image
                bottom_height = 0;

            int image_end = paper_height - bottom_height;
            try {
                File month_image = new File( input + "/" + (m+1) + ".jpg");
                if(!month_image.exists()) {
                    month_image = new File(input + "/" + (m+1) + ".png");
                    if(!month_image.exists()) {
                        System.out.println("Bild für Monat " + (m + 1) + " nicht gefunden!");
                        return;
                    }
                }

                BufferedImage read = ImageIO.read(month_image);
                int height = read.getHeight();
                int width = read.getWidth();
                //double ist = width / (height * 1.0);
                //double soll = (paper_width * 1.0) / paper_height;
                double width_scale = paper_width / (width * 1.0f);
                double img_width = paper_width;
                double img_height = height * width_scale;

                if(img_height < image_end) {
                    System.out.println("Warnung: Bild wird links/rechts beschnitten!");
                    img_height = image_end;
                    double height_scale = image_end / (height * 1.0f);
                    img_width = width * height_scale;
                }

                //System.out.println(width + "x" + height + " --> Ist: " + ist + " Soll: " + soll + " --> Anpassung: " + correction + "->" + (paper_width -(paper_width * correction)) +  "px");
                System.out.println(width + "x" + height + " > " + width_scale + " -->> y=" + img_height);

                graphics.drawImage(read, 0, 0, (int) img_width, (int) img_height, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(m >= 0){
                graphics.setColor(Color.BLACK);
                graphics.clearRect(0, paper_height - bottom_height, paper_width, bottom_height);
                graphics.drawRect(0, paper_height - bottom_height, paper_width, bottom_height);

                graphics.setColor(Color.WHITE);
                //Bildtitel
                graphics.setFont(new Font("Arial Black", Font.BOLD, 30));
                graphics.drawString(titles.get(m), paper_width - graphics.getFontMetrics().stringWidth(titles.get(m)) - 40, paper_height - bottom_height + 55);

                //Monatstitel
                graphics.setFont(new Font("Arial Black", Font.BOLD, 70));
                graphics.drawString(getMonth(m), 150, image_end + 120);

                //Monat:
                cal.set(Calendar.MONTH, m);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                int weekday = cal.get(Calendar.DAY_OF_WEEK);
                for (int i = 1; i <= days; i++) {
                    cal.set(Calendar.DAY_OF_MONTH, i);
                    boolean feiertag = false;
                    //System.out.print(cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + ".");
                    for (Date date : feiertags_dates)
                        if (cal.getTime().getTime() == date.getTime()) {
                            feiertag = true;
                            //System.out.print(" Feiertag");
                        }

                    if(weekday == GregorianCalendar.SUNDAY || feiertag)
                        graphics.setColor(Color.red);
                    else if(ffw_dates && cal.get(Calendar.WEEK_OF_YEAR) % 2 == 0 && cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
                        graphics.setColor(Color.decode("#7c7cff"));
                        //System.out.print(" Fw");
                    } else {
                        graphics.setColor(Color.white);
                    }

                    //System.out.println();

                    graphics.setFont(new Font("Arial Black", Font.BOLD, 30));
                    graphics.drawString(getWeekDayShort(weekday), 40 + i * distance + 2, image_end + 220); //added 2px
                    graphics.setFont(new Font("Arial Black", Font.BOLD, 50));
                    graphics.drawString("" + i, 40 + i * distance, image_end + 290);
                    weekday = weekday + 1;
                    if (weekday == 8) weekday = 1;
                }
            }

            try {
                String outputDir = input + "/generated";
                File cal_out_dir = new File(outputDir);
                if(! cal_out_dir.exists()){
                    cal_out_dir.mkdirs();
                }
                ImageIO.write(bufferedImage, "png", new File(outputDir + "/" + (m+1 < 10? "0":"") + (m+1) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Images Created, Generating High Qual PDF");
        genPDF(input);
    }

    private String getMonth(int month) {
        switch(month){
            case 0: return "Januar";
            case 1: return "Februar";
            case 2: return "März";
            case 3: return "April";
            case 4: return "Mai";
            case 5: return "Juni";
            case 6: return "Juli";
            case 7: return "August";
            case 8: return "September";
            case 9: return "Oktober";
            case 10: return "November";
            case 11: return "Dezember";
            default: return "";
        }
    }

    public static void main(String args[]) throws IOException, org.apache.commons.cli.ParseException {
        boolean upload = false;
        String input = "";
        boolean ffw_dates = false;

        Options options = new Options();
        options.addOption("y", "year", true, "the year the calendar should use");
        options.addOption("u", "upload", false, "upload to rbs90.de");
        options.addOption("f", "ffw", false, "add ffw dates");
        options.addOption("i", "input", true, "input folder");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("upload"))
            upload = true;

        if(cmd.hasOption("upload"))
            ffw_dates = true;

        if(cmd.hasOption("input")) {
            input = cmd.getOptionValue("input");
        } else {
            System.err.println("Kein Eingansordner angegeben (--input <Ordner>)");
            System.exit(-1);
        }

        int year = 0;

        if(cmd.hasOption("year")) {
            year = Integer.parseInt(cmd.getOptionValue("year"));
        } else {
            System.err.println("Kein Jahr angegeben (--year <Jahr>)");
            System.exit(-2);
        }

        new BottomGenerator(year, input, upload, ffw_dates);
    }

    private String getWeekDayShort(int day){
        switch (day){
            case GregorianCalendar.SUNDAY: return "So";
            case GregorianCalendar.MONDAY: return "Mo";
            case GregorianCalendar.TUESDAY: return "Di";
            case GregorianCalendar.WEDNESDAY: return "Mi";
            case GregorianCalendar.THURSDAY: return "Do";
            case GregorianCalendar.FRIDAY: return "Fr";
            case GregorianCalendar.SATURDAY: return "Sa";
            default: return "";
        }
    }

    private void genPDF(String output_dir){
        String[] cmd = new String[]{"/bin/sh", "topdf.sh", output_dir};
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmd);
            Process proc = builder.start();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
