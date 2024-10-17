
package com.jui.playground.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExampleFile {
	
    private String name;
    private String fileName;

    public ExampleFile(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }


}
    