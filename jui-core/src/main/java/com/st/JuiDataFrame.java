package com.st;

import java.util.ArrayList;
import java.util.List;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JuiDataFrame  {

	//https://github.com/vmzakharov/dataframe-ec/tree/master
	DataFrame df;

	public JuiDataFrame(DataFrame df) {
		
		this.df = df;
		
	}
	
	public List<String> getHtmlCols() {
		
		List<String> cols = new ArrayList<>();
		for ( var col : df.getColumns() ) cols.add(col.getName());
		
		return cols;
		
	}
	
	
	public List<List<String>> getHtmlRows() {

		List<List<String>> rows = new ArrayList<>();

		//data.getDf().getColumns().get(0).getName()
		for ( int irow=0; irow < df.rowCount(); irow++ ) {
			
			List<String> row = new ArrayList<>();
			
			for ( int icol=0; icol< df.getColumns().size(); icol++ ) {
				row.add(df.getObject(irow,icol)  + "");
			}
			rows.add(row);
        }
		
		return rows;
		
	}


}
