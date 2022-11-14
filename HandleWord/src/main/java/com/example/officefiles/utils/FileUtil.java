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

public class FileUtil {
    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));

    private static final Path RESOURCES_PATH = CURRENT_FOLDER.resolve(Paths.get("src/main/resources"));

    public static final Path UPLOAD_PATH = RESOURCES_PATH.resolve(CommonConstant.UPLOAD);

    public static final Path PREVIEW_PATH = RESOURCES_PATH.resolve(CommonConstant.PREVIEW);

    //save file to upload
    public static File convertMultipartToFile(MultipartFile file) throws IOException {
        Path path = UPLOAD_PATH.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        if (!Files.exists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
        }
        File convertFile = new File(String.valueOf(path));
        FileOutputStream fos = new FileOutputStream(convertFile);
        fos.write(file.getBytes());
        fos.close();
        return convertFile;
    }

    // Lấy ra file ở trong thư mục resources theo đường dẫn
    public static File getFileByPath(String pathFile) {
        Path path = RESOURCES_PATH.resolve(Paths.get(pathFile));
        return path.toFile();
    }

    // Get type từ file name
    public static String getTypeFile(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // Set name preview
    public static String setNamePreview(String fileName) {
        return "preview-" + fileName;
    }

    //save file to preview
    public static File saveFileToPreview(String fileName) {
        Path path = PREVIEW_PATH.resolve(fileName);
        return new File(String.valueOf(path));
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
