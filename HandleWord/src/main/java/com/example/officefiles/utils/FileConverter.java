package com.example.officefiles.utils;

import com.example.officefiles.common.CommonConstant;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileConverter {
    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));

    public static File convertMultipartToFile(MultipartFile file) throws IOException {
        Path path = CURRENT_FOLDER.resolve(CommonConstant.staticPath).resolve(CommonConstant.uploadPath)
                .resolve(Objects.requireNonNull(file.getOriginalFilename()));
        if (!Files.exists(CURRENT_FOLDER.resolve(CommonConstant.staticPath).resolve(CommonConstant.uploadPath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(CommonConstant.staticPath).resolve(CommonConstant.uploadPath));
        }
        File convertFile = new File(String.valueOf(path));
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(file.getBytes());
        fos.close();
        return convertFile;
    }

// Zip file and convert XML to Json (Cách 1 -> bỏ)
    public static InputStream zipperAndGetDocumentFileWord(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry zipEntry = zipFile.getEntry("word/document.xml");
        return zipFile.getInputStream(zipEntry);
    }

    public static InputStream zipperAndGetAppFileWord(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry zipEntry = zipFile.getEntry("docProps/app.xml");
        return zipFile.getInputStream(zipEntry);
    }

    public static String convertXMLtoJsonString(InputStream is) throws IOException {
        String xmlString = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        JSONObject json = XML.toJSONObject(xmlString);
        return json.toString(4);
    }
}
