package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {

    ClassLoader cl = FilesParsingTest.class.getClassLoader();

    @Test
    void zipParseTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("Archive.zip");
                ZipInputStream zis = new ZipInputStream(resource);
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf") && !entry.getName().contains("MACOSX")) {
                    PDF content = new PDF(zis);
                    assertThat(content.text).contains("This is a small demonstration");
                }
                if (entry.getName().contains(".xlsx") && !entry.getName().contains("MACOSX")) {
                    XLS content = new XLS(zis);
                    assertThat(content.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue().contains("Dulce"));
                }
                if (entry.getName().contains(".csv") && !entry.getName().contains("MACOSX")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = reader.readAll();
                    assertThat(content.get(1)[0]).contains("booker12");
                }
            }
        }
    }

    @Test
    void jacksonParseTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(cl.getResourceAsStream("example.json"), Person.class);

        assertThat(person.getId()).isEqualTo(1);
        assertThat(person.getName()).isEqualTo("Yong Mook Kim");
        assertThat(person.getPhones().length).isEqualTo(2);
        assertThat(person.getPhones()[0]).isEqualTo("111-111-1234");
    }
}