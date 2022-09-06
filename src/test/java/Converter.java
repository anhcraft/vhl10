import dev.anhcraft.neep.NeepConfig;
import dev.anhcraft.neep.errors.NeepWriterException;
import dev.anhcraft.neep.struct.container.NeepSection;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    private String stringify(Cell x){
        return x.getCellType() == CellType.NUMERIC ? String.valueOf(x.getNumericCellValue()) : x.getStringCellValue().trim();
    }

    @Test
    public void convert(){
        File source = new File("vhvl.xlsx");
        File target = new File("review.neep");
        try {
            XSSFWorkbook wb = new XSSFWorkbook(source);
            XSSFSheet sheet = wb.getSheetAt(0);
            NeepConfig config = NeepConfig.create();
            List<NeepSection> list = new ArrayList<>();
            for (Row i : sheet) {
                NeepConfig sub = NeepConfig.create();
                String s = i.getCell(0).getStringCellValue().trim();
                if(s.isEmpty()) break;
                sub.set("question", s);
                sub.set("choices", new String[]{
                        stringify(i.getCell(1)),
                        stringify(i.getCell(2)),
                        stringify(i.getCell(3)),
                        stringify(i.getCell(4))
                });
                sub.set("answer", (int) (i.getCell(5).getNumericCellValue() - 1));
                list.add(sub.getRoot());
            }
            config.set("multi_choices", list);
            config.save(target);
        } catch (IOException | InvalidFormatException | NeepWriterException e) {
            e.printStackTrace();
        }
    }
}
