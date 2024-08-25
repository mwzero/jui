package com.jui.st;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SimpleTable {
	
	List<SimpleColumn> columns;
	List<SimpleRow> rows;

}
