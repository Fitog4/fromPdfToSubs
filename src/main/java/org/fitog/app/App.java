package org.fitog.app;

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException {

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

        // cleanup and modify text
        parsedText = parsedText.replaceAll("\\([.\\n]+\\)", "");
        parsedText = parsedText.replaceAll("(ALAN|VERONICA|MICHAEL|ANNETTE)\\.\\s+", "\\[$1\\] ");

        parsedText = parsedText.replaceAll("ALAN", "ALAIN");
        parsedText = parsedText.replaceAll("Alan", "Alain");
        parsedText = parsedText.replaceAll("MICHAEL", "MICHEL");
        parsedText = parsedText.replaceAll("Michael", "Michel");
        parsedText = parsedText.replaceAll("VERONICA", "VERONIQUE");
        parsedText = parsedText.replaceAll("Veronica", "V\u00E9ronique");
        parsedText = parsedText.replaceAll("Ronnie", "V\u00E9ro");

        parsedText = parsedText.replaceAll("Raleigh", "Reille");
        parsedText = parsedText.replaceAll("Novak", "Houill\u00E9");
        parsedText = parsedText.replaceAll("Benjamin", "Ferdinand");
        parsedText = parsedText.replaceAll("Henry", "Bruno");

        parsedText = parsedText.replaceAll("Cobble Hill Park", "Aspirant-Dunant Square");
        parsedText = parsedText.replaceAll("Whitman Park", "Montsouris Park");
        parsedText = parsedText.replaceAll("Korean deli", "flower shop");
        parsedText = parsedText.replaceAll("Smith", "Mouton-Duvernet");
        parsedText = parsedText.replaceAll("today's Times", "today's Les Echos");
        parsedText = parsedText.replaceAll("today's Times", "today's Les Echos");
        parsedText = parsedText.replaceAll("([Cc]lafouti)", "$1s");
        parsedText = parsedText.replaceAll("Chrismastime", "Chrismas time");
        parsedText = parsedText.replaceAll("Florida", "the Midi region");
        parsedText = parsedText.replaceAll("F train", "metro");
        parsedText = parsedText.replaceAll("go traipsing around", "make the effort to come");
        parsedText = parsedText.replaceAll("Spartacus", "Ivanho\u00E9");
        parsedText = parsedText.replaceAll("Bobby Kopecki", "Didier Leglu");
        parsedText = parsedText.replaceAll("Charley's Aunt", "Monsieur de Pourceaugnac");
        parsedText = parsedText.replaceAll("Secaucus", "Saint-Denis-La-Pleine");
        parsedText = parsedText.replaceAll("Pepto-Bismol", "Primp\u00E9ran");
        parsedText = parsedText.replaceAll("BQE", "highway");
        parsedText = parsedText.replaceAll("New York", "Paris");
        parsedText = parsedText.replaceAll("peddling", "selling");
        parsedText = parsedText.replaceAll("coons", "black people");

        parsedText = parsedText.replaceAll("\\[..\\]\\s*", "");
        parsedText = parsedText.replaceAll("1", "I");
        // cercare di levare tutte le didascalie
        parsedText = parsedText.replaceAll("\\([^\\)]+[\\)\\n]", "");

        // process text
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

        // split into slides
        List<List<String>> slidesDTO = new ArrayList<>();
        slidesDTO.add(new ArrayList<String>());

        for (int i = 0; i < linesToBePrinted.size(); i++) {
            slidesDTO.get(slidesDTO.size() - 1).add(linesToBePrinted.get(i));
            if (slidesDTO.get(slidesDTO.size() - 1).size() < 5) {
                continue;
            }
            if (i < linesToBePrinted.size() - 1 && isNewReplique(linesToBePrinted.get(i + 1))) {
                slidesDTO.add(new ArrayList<String>());
            }
            if (slidesDTO.get(slidesDTO.size() - 1).size() > 7) {
                slidesDTO.add(new ArrayList<String>());
            }
        }
        List<String> slideTextList = new ArrayList<>();
        for (List<String> slideLines : slidesDTO) {
            slideTextList.add(String.join("\n", slideLines));
        }

        // visualize result
//        for (int i = 0; i < slidesDTO.size(); i++) {
//            System.out.println("_____________________________");
//            for (String line : slidesDTO.get(i)) {
//                System.out.println(line);
//            }
//            System.out.println("_____________________________");
//        }
        for (int i = 0; i < slideTextList.size(); i++) {
            System.out.println("_____________________________");
            System.out.println(slideTextList.get(i));
            System.out.println("_____________________________");
        }
        System.out.println("Number of slides: " + slidesDTO.size());

        // create ppt
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.BLANK);
        layout.getBackground().setFillColor(Color.BLACK);

        for (String slideText : slideTextList) {
            XSLFSlide slide = ppt.createSlide(layout);

            // fill in text
            XSLFTextBox textBox = slide.createTextBox();
            XSLFTextParagraph textParagraph = textBox.getTextParagraphs().get(0);
            textParagraph.setLineSpacing(160d);
            XSLFTextRun textRun = textParagraph.getTextRuns().get(0);
            textRun.setFontColor(Color.WHITE);
            textRun.setFontSize(24d);
            textRun.setText(slideText);
            textBox.setVerticalAlignment(VerticalAlignment.TOP);
            textBox.setAnchor(new Rectangle(0, 0, 720, 540));
        }

        // save ppt
        FileOutputStream out = new FileOutputStream("God of Carnage Subs.pptx");
        ppt.write(out);
        out.close();
    }

    private static boolean isNewReplique(String line) {
        return line.matches("^[A-Z][A-Z][A-Z][A-Z].*");
    }
}
