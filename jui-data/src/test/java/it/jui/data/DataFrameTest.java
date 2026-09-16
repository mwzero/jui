package it.jui.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class DataFrameTest {

    @Test
    void selectActuallyKeepsSelectedData() {
        DataFrame frame = new DataFrame(
                List.of("name", "age"),
                List.of(List.of("Ada", 36), List.of("Alan", 41)));

        DataFrame selected = frame.select("age");
        assertEquals(List.of("age"), selected.headers());
        assertEquals(2, selected.rowCount());
        assertEquals(36, selected.get(0, 0));
    }

    @Test
    void csvJsonAndLimitWork() throws Exception {
        DataFrame csv = DataFrames.readCsvString("name,age\nAda,36\nAlan,41\n");
        assertEquals(2, csv.rowCount());
        assertEquals(1, csv.limit(1).rowCount());

        DataFrame json = DataFrames.readJson("[{\"name\":\"Ada\",\"age\":36},{\"name\":\"Alan\",\"age\":41}]");
        assertEquals(List.of("name", "age"), json.headers());
        assertEquals(2, json.toMaps().size());
    }
}
