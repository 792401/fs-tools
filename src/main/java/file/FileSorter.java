package file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSorter {
    public static void main(String[] args) {
        String sourceDirectory = "path/to/source_directory";
        String destinationDirectory = "path/to/destination_directory";

        createDirectory(destinationDirectory + "/Videos");
        createDirectory(destinationDirectory + "/Photos");

        searchAndSortFiles(new File(sourceDirectory), destinationDirectory);
    }

    private static void searchAndSortFiles(File sourceDir, String destinationDir) {
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively search subdirectories
                    searchAndSortFiles(file, destinationDir);
                } else {
                    String fileExtension = getFileExtension(file);
                    if (isVideoFile(fileExtension)) {
                        moveFile(file, destinationDir + "/Videos");
                    } else if (isPhotoFile(fileExtension)) {
                        moveFile(file, destinationDir + "/Photos");
                    }
                }
            }
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    private static boolean isVideoFile(String fileExtension) {
        return fileExtension.equals("mp4") || fileExtension.equals("mov") || fileExtension.equals("avi");
    }

    private static boolean isPhotoFile(String fileExtension) {
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png");
    }

    private static void moveFile(File sourceFile, String destinationDirectory) {
        try {
            Path sourcePath = sourceFile.toPath();
            Path destinationPath = getDestinationFilePath(sourceFile, destinationDirectory);
            Files.createDirectories(destinationPath.getParent());
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved: " + sourcePath + " -> " + destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getDestinationFilePath(File file, String destinationDirectory) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MMM");
        String formattedDate = dateFormat.format(getFileCreationDate(file));
        String fileName = file.getName();
        return Path.of(destinationDirectory + "/" + formattedDate + "/" + fileName);
    }

    private static Date getFileCreationDate(File file) {
        try {
            Path path = file.toPath();
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    private static void createDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Created directory: " + directory);
            } else {
                System.out.println("Failed to create directory: " + directory);
            }
        }
    }
}
