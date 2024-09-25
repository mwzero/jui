package com.st;

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
	
	


}
