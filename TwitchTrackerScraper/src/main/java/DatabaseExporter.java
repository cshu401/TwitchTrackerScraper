import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.persistence.*;
import java.io.FileOutputStream;
import java.util.List;

public class DatabaseExporter {

    public void exportDatabaseToExcel(String excelFilePath) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YourPersistenceUnit");
        EntityManager em = emf.createEntityManager();

        // Retrieve all streamers from the database
        TypedQuery<Streamer> streamerQuery = em.createQuery("SELECT s FROM Streamer s", Streamer.class);
        List<Streamer> streamers = streamerQuery.getResultList();

        // Setting up the Excel workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Streamers");

        // Define headers for Streamer data
        String[] headers = new String[] {"Streamer ID", "Name URL", "Last Scraped", "Minutes Streamed", 
                                         "Average Viewers", "Peak Viewers", "Hours Watched", 
                                         "Followers Per Hour", "Followers"};

        // Create the header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;  // Start from the second row, since the first row is for headers
        for (Streamer streamer : streamers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(streamer.getId());
            row.createCell(1).setCellValue(streamer.getNameUrl());
            row.createCell(2).setCellValue(streamer.getLastScraped().toString());
            row.createCell(3).setCellValue(streamer.getMinutesStreamed());
            row.createCell(4).setCellValue(streamer.getAverageViewers());
            row.createCell(5).setCellValue(streamer.getPeakViewers());
            row.createCell(6).setCellValue(streamer.getHoursWatched());
            row.createCell(7).setCellValue(streamer.getFollowersPerHour());
            row.createCell(8).setCellValue(streamer.getFollowers());

            // If Streams are also needed, you can create a new sheet and populate it similarly
        }

        // Write the Excel file to disk
        try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close(); // Close the workbook resource
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        em.close(); // Close the EntityManager
        emf.close(); // Close the EntityManagerFactory
    }
}
