package com.st;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import com.jui.utils.FS;

import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Builder
@Getter
@Setter
public class ST {
	
	@Singular
	Map<String, String> options;
	
	public static JuiDataFrame read_csv(String endpoint) throws IOException, URISyntaxException {
		
		ST st = ST.builder()
				.option("classLoading",  "true")
				.build();
			
		return st.csv(FS.getFile(endpoint, st.options), ",", null);
	}
	
	private JuiDataFrame csv(File csvFile, String delimiter, String csvName) throws IOException {

		CsvDataSet ds = new CsvDataSet(csvFile.toString(), csvName);
		return new JuiDataFrame(ds.loadAsDataFrame());
	}
	
	
}
