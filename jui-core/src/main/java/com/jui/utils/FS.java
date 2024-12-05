package com.jui.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lombok.extern.java.Log;

@Log
public class FS {
	
	public static Path getFilePath(String endpoint, Map<String, String> options) throws IOException, URISyntaxException {
		
		if ( endpoint.startsWith("http") )  {
			
			URL url = new URL(endpoint);
			String endpointPath = url.getPath();
			String fileName = endpointPath.substring(endpointPath.lastIndexOf("/") + 1);
			Path tempDir = Files.createTempDirectory("csv_temp");
			Path tempFile = tempDir.resolve(fileName);
			
	        try (InputStream in = url.openStream()) {
	        	Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
	        }
	        
	        return tempFile;
		} 
		
		Path pathFile;
		if ( isClassLoading(options)) {
			
			InputStream resourceStream = FS.class.getClassLoader().getResourceAsStream(endpoint);
	        if (resourceStream == null) {
	            throw new IOException("Resource not found: " + endpoint);
	        }

	        // Scrive la risorsa in un file temporaneo
	        Path tempDir = Files.createTempDirectory("resource_temp");
	        Path tempFile = tempDir.resolve(endpoint.substring(endpoint.lastIndexOf("/") + 1));
	        try (OutputStream out = Files.newOutputStream(tempFile)) {
	            byte[] buffer = new byte[8192];
	            int bytesRead;
	            while ((bytesRead = resourceStream.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	        }

	        return tempFile;
	    	
		} else {
			
			pathFile = Paths.get(endpoint);
		}
		
		log.info("loading file:" + pathFile.toString());
		return pathFile;
		
	}
	public static Reader getFile(String endpoint, Map<String, String> options) throws IOException, URISyntaxException {
		
		Path pathFile = getFilePath(endpoint, options);
	        
		if (endpoint.endsWith(".zip")) {
			
			InputStream fis = Files.newInputStream(pathFile);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry entry = zis.getNextEntry();
            
            return new InputStreamReader(zis);
        }
		else if (endpoint.endsWith(".gz")) {
			
			InputStream fis = Files.newInputStream(pathFile);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            return new InputStreamReader(gzis);
        }
		else {
			
			InputStream fis = Files.newInputStream(pathFile);
	        return new InputStreamReader(fis);
	        
		}
	}
	
	private static boolean isClassLoading(Map<String, String> options) {
		
		if ( options.containsKey("classLoading") ) 
			return Boolean.parseBoolean(options.get("classLoading") );
		
		return false;
	}
	
	public static File TempFileWriter (Reader reader) throws IOException {
		
        Path tempFile = Files.createTempFile("JUI_" + UUID.randomUUID().toString(), ".jui");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardOpenOption.WRITE)) {
            char[] buffer = new char[1024];
            int numCharsRead;
            while ((numCharsRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, numCharsRead);
            }
        }
        
		return tempFile.toFile();
    }
	
	private static void zipDirectory(File folderToZip, String parentFolder, ZipOutputStream zipOut) throws IOException {
		
        for (File file : folderToZip.listFiles()) {
        	
            if (file.isDirectory()) {
            	
                zipDirectory(file, parentFolder + file.getName() + "/", zipOut);
                
            } else {
                
            	try (FileInputStream fis = new FileInputStream(file)) {

            		ZipEntry zipEntry = new ZipEntry(parentFolder + file.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[2048];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        }
    }

    public static void zipFiles(String sourceDirPath, String zipFilePath) throws IOException {
    	
    	log.fine("Zipping files from [%s] to [%s]".formatted(sourceDirPath, zipFilePath));
        File sourceDir = new File(sourceDirPath);
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            zipDirectory(sourceDir, "", zipOut);
            zipOut.flush();
            zipOut.close();
        }
    }
	
}
