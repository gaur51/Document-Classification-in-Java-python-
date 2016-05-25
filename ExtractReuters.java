package org.apache.lucene.benchmark.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.util.IOUtils;

/**
 * Split the Reuters SGML documents into Simple Text files containing: Title, Date, Dateline, Body
 */
public class ExtractReuters {
  private Path reutersDir;
  private Path outputDir;

  public ExtractReuters(Path reutersDir, Path outputDir) throws IOException {
    this.reutersDir = reutersDir;
    this.outputDir = outputDir;
    }

  public void extract() throws IOException {
    long count = 0;
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(reutersDir, "*.sgm")) {
      for (Path sgmFile : stream) {
        extractFile(sgmFile);
        count++;
      }
    }
    if (count == 0) {
      System.err.println("No .sgm files in " + reutersDir);
    }
  }

  Pattern EXTRACTION_PATTERN = Pattern
      .compile("<TITLE>(.*?)</TITLE>|<DATE>(.*?)</DATE>|<BODY>(.*?)</BODY>");

  private static String[] META_CHARS = { "&", "<", ">", "\"", "'" };

  private static String[] META_CHARS_SERIALIZATIONS = { "&amp;", "&lt;",
      "&gt;", "&quot;", "&apos;" };
  
  protected void extractFile(Path sgmFile) {
    try (BufferedReader reader = Files.newBufferedReader(sgmFile, StandardCharsets.UTF_8)) {
      StringBuilder buffer = new StringBuilder(1024);
      StringBuilder outBuffer = new StringBuilder(1024);

      String line = null;
      int docNumber = 0;
      while ((line = reader.readLine()) != null) {
        // when we see a closing reuters tag, flush the file

        if (line.indexOf("</REUTERS") == -1) {
          // Replace the SGM escape sequences

          buffer.append(line).append(' ');// accumulate the strings for now,
                                          // then apply regular expression to
                                          // get the pieces,
        } else {
          // Extract the relevant pieces and write to a file in the output dir
          Matcher matcher = EXTRACTION_PATTERN.matcher(buffer);
          while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
              if (matcher.group(i) != null) {
                outBuffer.append(matcher.group(i));
              }
            }
            outBuffer.append(System.lineSeparator()).append(System.lineSeparator());
          }
          String out = outBuffer.toString();
          for (int i = 0; i < META_CHARS_SERIALIZATIONS.length; i++) {
            out = out.replaceAll(META_CHARS_SERIALIZATIONS[i], META_CHARS[i]);
          }
          Path outFile = outputDir.resolve(sgmFile.getFileName() + "-" + (docNumber++) + ".txt");
          
          try (BufferedWriter writer = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8)) {
            writer.write(out);
          }
          outBuffer.setLength(0);
          buffer.setLength(0);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws Exception {

      String ReutersData="C:\\Users\\hp\\Desktop\\Data";//Change this path to the folder containing reuters Data on your machine
    Path reutersDir = Paths.get(ReutersData);
    if (!Files.exists(reutersDir)) {
      usage("Cannot find Path to Reuters SGM files ("+reutersDir+")");
      return;
    }
    
    String Output= "C:\\Users\\hp\\Desktop\\Output";//Change this path to the folder you wish to output txt files on your machine
    Path outputDir = Paths.get(Output);
    Files.createDirectories(outputDir);
    ExtractReuters extractor = new ExtractReuters(reutersDir, outputDir);
    extractor.extract();
    
  }

  private static void usage(String msg) {
    System.err.println("Usage: "+msg+" :: java -cp <...> org.apache.lucene.benchmark.utils.ExtractReuters <Path to Reuters SGM files> <Output Path>");
  }
  
}