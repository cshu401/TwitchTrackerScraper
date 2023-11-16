import Domain.Streamer;
import Utils.JPAUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.*;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {

    public static void exportToExcel() {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        try {
            // Retrieve all Domain.Streamer objects from the database
            TypedQuery<Streamer> query = em.createQuery("SELECT s FROM Domain.Streamer s", Streamer.class);
            List<Streamer> streamerList = query.getResultList();

            // Create a workbook and a sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Streamers");

            // Create a header row
            String[] columns = {"ID", "Name URL", "Last Scraped", "Minutes Streamed", "Average Viewers", "Peak Viewers", "Hours Watched", "Followers Per Hour", "Followers", /* More columns for Domain.Streams data if needed */};
            createHeaderRow(sheet, columns);

            // Populate the sheet with data
            int rowNum = 1;
            for (Streamer streamer : streamerList) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(streamer.getId());
                row.createCell(1).setCellValue(streamer.getNameUrl());
                row.createCell(2).setCellValue(streamer.getLastScraped() != null ? streamer.getLastScraped().toString() : "N/A"); // Check for null
                row.createCell(3).setCellValue(streamer.getMinutesStreamed());
                row.createCell(4).setCellValue(streamer.getAverageViewers());
                row.createCell(5).setCellValue(streamer.getPeakViewers());
                row.createCell(6).setCellValue(streamer.getHoursWatched());
                row.createCell(7).setCellValue(streamer.getFollowersPerHour());
                row.createCell(8).setCellValue(streamer.getFollowers());
                // More cells for Domain.Streams data if needed, e.g., number of streams, average duration, etc.
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream("Streamers.xlsx")) {
                workbook.write(fileOut);
            }

            // Close the workbook
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void createHeaderRow(Sheet sheet, String[] columns) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
    }

}
