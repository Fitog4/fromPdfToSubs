package org.fitog.app;

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        // extract text from pdf
        PDFParser parser;
        PDDocument pdDoc = null;
        PDFTextStripper pdfStripper;

        String parsedText = null;
        String fileName = "/Users/ftognacci/sandboxes/Perso/resources/god of carnage script.pdf";
        File file = new File(fileName);
        try {
            parser = new PDFParser(new RandomAccessBufferedFileInputStream(file));
            parser.parse();
            pdDoc = parser.getPDDocument();
            pdfStripper = new PDFTextStripper();
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        // cleanup text
        List<String> originalLines = Arrays.asList(parsedText.split("\n"));
        List<String> linesToBePrinted = new ArrayList<>();

        for (int i = 29; i < originalLines.size(); i++) {
            if (i > 1661) {
                continue;
            }
            if (originalLines.get(i).matches("\\s") || originalLines.get(i).matches("^\\[\\d+\\].*")) {
                continue;
            }
            linesToBePrinted.add(originalLines.get(i));
        }

        for (int i = 0; i < linesToBePrinted.size(); i++) {
            String lineToBePrinted = linesToBePrinted.get(i);
            String newRepliquePointer = isNewReplique(lineToBePrinted) ? "---> " : "";
            System.out.println(i + "\t" + newRepliquePointer + lineToBePrinted);
        }
    }

    private static boolean isNewReplique(String line) {
        return line.matches("^[A-Z][A-Z][A-Z][A-Z].*");
    }
}
