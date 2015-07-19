/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class will handle compress of fetched files.
 *
 * @author Mir Saman Tajbakhsh
 */
public class Compressor {

    /**
     * This method will compress all files of the {@code inputDirectory} and
     * stores into the {@code outputDirectory} based on specified compress type.
     *
     * @param inputDirectory The path of input directory.
     * @param outputDirectory The path of output directory.
     * @param compressorType The compressor type. Currently works with
     * {@code ZIP}.
     */
    public static void Compress(String inputDirectory, String outputDirectory, Variables.CompressType compressorType) {

        if (compressorType == Variables.CompressType.NONE) {
            return; //Do not compress
            //This code occurs when user specifies a folder limit but declared no compression.
        }
        
        String outputFileName = null;
        HashSet<File> inputFiles = new HashSet<>();

        //<editor-fold defaultstate="collapsed" desc="Pre Process">
        FileFilter ff = null;

        //Input
        File input = new File(inputDirectory);

        ff = new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.length() > 0 && (file.getName().endsWith("htm") || (file.getName().endsWith("html")))) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        for (File f : input.listFiles(ff)) {
            inputFiles.add(f);
        }

        //Output
        File output = new File(outputDirectory);
        if (!output.exists()) {
            output.mkdirs();
        }

        ff = new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isFile() && file.getName().endsWith("zip") && file.length() > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        int outputName = output.listFiles(ff).length;

        outputFileName = outputDirectory + (++outputName) + ".";

        switch (compressorType) {
            case GZIP:
                outputFileName += "gz";
                break;
            case RAR:
                outputFileName += "rar";
                break;
            case TAR:
                outputFileName += "tar";
                break;
            case ZIP:
                outputFileName += "zip";
                break;
        }
//</editor-fold>

        //Compress
        if (Variables.debug) {
            Variables.logger.Log(Compressor.class, Variables.LogType.Info, "Starting compression [" + outputFileName + "]");
            if (Variables.vv) {
                Variables.logger.Log(Compressor.class, Variables.LogType.Info, "Total files to compress: " + inputFiles.size());
            }
        }

        switch (compressorType) {

            case ZIP:
                byte[] buffer = new byte[1024];

                try {

                    FileOutputStream fos = new FileOutputStream(outputFileName);
                    ZipOutputStream zos = new ZipOutputStream(fos);

                    for (File file : inputFiles) {

                        if (Variables.debug && Variables.vv) {
                            Variables.logger.Log(Compressor.class, Variables.LogType.Info, file + " -> added to -> [ " + outputFileName + " ]");
                        }
                        ZipEntry ze = new ZipEntry(file.getName());
                        zos.putNextEntry(ze);

                        FileInputStream in = new FileInputStream(file);

                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }

                        in.close();

                        if (Variables.deleteAfterCompress) {
                            file.delete();
                        } else {
                            Variables.logger.Log(Compressor.class, Variables.LogType.Warning, "Undeleting files may create multiple compressed files with duplicate data.");
                        }
                    }

                    zos.closeEntry();
                    zos.close();
                } catch (IOException ex) {
                    Variables.logger.Log(Compressor.class, Variables.LogType.Error, "Error in compressing " + outputFileName + ". Details:\r\n" + ex.getMessage());
                }
                break;

            case GZIP:
            case RAR:
            case TAR:
                throw new IllegalArgumentException("Only ZIP format is supported in this version.");
        }

        if (Variables.debug) {
            Variables.logger.Log(Compressor.class, Variables.LogType.Info, "[+] Compression finished! [" + outputFileName + "]");
            if (Variables.vv) {
                Variables.logger.Log(Compressor.class, Variables.LogType.Info, "Size after compression: " + Methods.filesizeToHumanReadable((new File(outputFileName)).length(), false));
            }
        }
    }

}
