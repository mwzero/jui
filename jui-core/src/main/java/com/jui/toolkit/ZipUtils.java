package com.jui.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	public static void zipDirectory(File folderToZip, String parentFolder, ZipOutputStream zipOut) throws IOException {
		
        for (File file : folderToZip.listFiles()) {
        	
            if (file.isDirectory()) {
            	
                zipDirectory(file, parentFolder + file.getName() + "/", zipOut);
                
            } else {
                
            	try (FileInputStream fis = new FileInputStream(file)) {

            		ZipEntry zipEntry = new ZipEntry(parentFolder + file.getName());
                    zipOut.putNextEntry(zipEntry);

                    // Scrivi il contenuto del file nel file zip
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        }
    }

    public static void zipFiles(String sourceDirPath, String zipFilePath) throws IOException {
        File sourceDir = new File(sourceDirPath);
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            // Chiama la funzione ricorsiva per comprimere la directory
            zipDirectory(sourceDir, "", zipOut);
        }
    }
}
