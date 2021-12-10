package schumi178.javaprograms.cheatbuster.gui;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultScreen {
    @FXML
    private Stage stage;
    @FXML
    private HBox resultSpace;
    @FXML
    private Label finalRatingLabel;
    @FXML
    private ProgressBar finalRatingBar;
    @FXML
    private VBox vBox;
    @FXML
    private VBox styleVBox;

    @FXML
    private VBox resultVBox;

    private final List<SingleResult> controllers = new ArrayList<>();

    private int rating;

    private static Font reportCodeFont;
    private static Font ratingFontBad;
    private static Font ratingFontAverage;
    private static Font ratingFontGood;

    private String codeLeft;
    private String codeRight;

    private String fileNameLeft;
    private String fileNameRight;

    public HBox getResultSpace() {
        return resultSpace;
    }

    public void addController(SingleResult result) {
        controllers.add(result);
    }

    public void setFinalRating(int rating) {
        if(rating < 0)
            this.rating = 0;
        else this.rating = Math.min(rating, 100);
        finalRatingLabel.setText(rating + "%");
        if(rating <= 25) {
            finalRatingLabel.setTextFill(Color.GREEN);
            styleVBox.getStylesheets().removeAll("fxml/progressBarRed.css", "fxml/progressBarYellow.css");
            styleVBox.getStylesheets().add("fxml/progressBarGreen.css");
            resultVBox.getStylesheets().removeAll("fxml/finalRatingBad.css", "fxml/finalRatingAverage.css");
            resultVBox.getStylesheets().add("fxml/finalResultGood.css");
        } else if(rating <= 75) {
            finalRatingLabel.setTextFill(Color.GOLD);
            styleVBox.getStylesheets().removeAll("fxml/progressBarRed.css", "fxml/progressBarGreen.css");
            styleVBox.getStylesheets().add("fxml/progressBarYellow.css");
            resultVBox.getStylesheets().removeAll("fxml/finalRatingBad.css", "fxml/finalRatingGood.css");
            resultVBox.getStylesheets().add("fxml/finalResultAverage.css");
        } else {
            finalRatingLabel.setTextFill(Color.RED);
            styleVBox.getStylesheets().removeAll("fxml/progressBarYellow.css", "fxml/progressBarGreen.css");
            styleVBox.getStylesheets().add("fxml/progressBarRed.css");
            resultVBox.getStylesheets().removeAll("fxml/finalRatingAverage.css", "fxml/finalRatingGood.css");
            resultVBox.getStylesheets().add("fxml/finalResultBad.css");
        }
    }

    public void setCodeInfo(String codeLeft, String fileNameLeft, String codeRight, String fileNameRight) {
        this.codeLeft = codeLeft;
        this.fileNameLeft = fileNameLeft;
        this.codeRight = codeRight;
        this.fileNameRight = fileNameRight;
    }

    @FXML
    private void onShown() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new KeyValue(finalRatingBar.progressProperty(), rating / 100.0)));
        timeline.play();
    }

    private void createPDF(File file) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            if(reportCodeFont == null) {
                reportCodeFont = FontFactory.getFont("/fonts/DejaVuSansMono.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 5f, Font.NORMAL, java.awt.Color.BLACK);
            }
            if(ratingFontBad == null) {
                ratingFontBad = FontFactory.getFont("/fonts/DejaVuSans-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 20f, Font.NORMAL, java.awt.Color.RED);
            }
            if(ratingFontAverage == null) {
                ratingFontAverage = FontFactory.getFont("/fonts/DejaVuSans-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 20f, Font.NORMAL, new java.awt.Color(170, 170, 0));
            }
            if(ratingFontGood == null) {
                ratingFontGood = FontFactory.getFont("/fonts/DejaVuSans-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 20f, Font.NORMAL, java.awt.Color.GREEN);
            }

            PdfPTable titleTable = new PdfPTable(2);
            titleTable.setWidthPercentage(30);
            titleTable.setWidths(new int[]{1, 1});

            Image image = Image.getInstance(Objects.requireNonNull(getClass().getResource("/icons/logo.png")));
            image.scaleAbsolute(64, 64);
            PdfPCell imageCell = new PdfPCell(image);
            imageCell.setBorder(Rectangle.NO_BORDER);
            titleTable.addCell(imageCell);

            Paragraph title = new Paragraph("Raport CheatBuster");
            title.setAlignment(Element.ALIGN_CENTER);
            PdfPCell titleCell = new PdfPCell(title);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleTable.addCell(titleCell);
            document.add(titleTable);
            document.add(new LineSeparator(1, 90, java.awt.Color.BLACK, Element.ALIGN_CENTER, -10));

            Paragraph comparedCode = new Paragraph("Porównywane pliki:");
            comparedCode.setSpacingBefore(15);
            document.add(comparedCode);

            PdfPTable codeTable = new PdfPTable(2);
            codeTable.setWidthPercentage(90);
            codeTable.setWidths(new int[] {1, 1});
            codeTable.setSpacingBefore(5);

            Paragraph fileName1 = new Paragraph(fileNameLeft);
            fileName1.setAlignment(Element.ALIGN_CENTER);
            PdfPCell fileName1Cell = new PdfPCell(fileName1);
            fileName1Cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            fileName1Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            codeTable.addCell(fileName1Cell);

            Paragraph fileName2 = new Paragraph(fileNameRight);
            fileName2.setAlignment(Element.ALIGN_CENTER);
            PdfPCell fileName2Cell = new PdfPCell(fileName2);
            fileName2Cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            fileName2Cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            codeTable.addCell(fileName2Cell);

            Paragraph code1 = new Paragraph(codeLeft, reportCodeFont);
            code1.setAlignment(Element.ALIGN_LEFT);
            PdfPCell code1Cell = new PdfPCell(code1);
            codeTable.addCell(code1Cell);

            Paragraph code2 = new Paragraph(codeRight, reportCodeFont);
            code2.setAlignment(Element.ALIGN_LEFT);
            PdfPCell code2Cell = new PdfPCell(code2);
            codeTable.addCell(code2Cell);

            codeTable.setSplitLate(false);
            document.add(codeTable);

            Paragraph result = new Paragraph("Wynik porównania:");
            result.setSpacingBefore(10);
            document.add(result);

            PdfPTable resultTable = new PdfPTable(controllers.size());
            resultTable.setWidths(new int[] {1, 1, 1, 1});
            resultTable.setSpacingBefore(10);

            for(SingleResult controller: controllers) {
                Paragraph listenerName = new Paragraph(controller.getListenerName());
                listenerName.setAlignment(Element.ALIGN_CENTER);
                PdfPCell listenerNameCell = new PdfPCell(listenerName);
                listenerNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                listenerNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                listenerNameCell.setBorder(Rectangle.NO_BORDER);
                resultTable.addCell(listenerNameCell);
            }
            for(SingleResult controller: controllers) {
                int rating = controller.getRating();
                Font nextFont = getRatingFont(rating);
                Paragraph ratingParagraph = new Paragraph("" + controller.getRating(), nextFont);
                ratingParagraph.setAlignment(Element.ALIGN_CENTER);
                PdfPCell ratingParagraphCell = new PdfPCell(ratingParagraph);
                ratingParagraphCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ratingParagraphCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                ratingParagraphCell.setBorder(Rectangle.NO_BORDER);
                resultTable.addCell(ratingParagraphCell);
            }
            for(SingleResult controller: controllers) {
                String resultText = controller.getResultText();
                Paragraph resultTextParagraph = new Paragraph(resultText);
                resultTextParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
                PdfPCell resultTextParagraphCell = new PdfPCell(resultTextParagraph);
                resultTable.addCell(resultTextParagraphCell);
            }
            document.add(resultTable);

            Paragraph finalResult = new Paragraph("Końcowy wynik: ");
            finalResult.add(new Paragraph("" + rating, getRatingFont(rating)));
            finalResult.setAlignment(Element.ALIGN_RIGHT);
            finalResult.setSpacingBefore(10);
            document.add(finalResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }

    private Font getRatingFont(int rating) {
        if(rating <= 25) {
            return ratingFontGood;
        } else if(rating <= 75) {
            return ratingFontAverage;
        } else {
            return ratingFontBad;
        }
    }

    @FXML
    private void saveResult() throws FileNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new ExtensionFilter("Plik PDF", "*.pdf"));
        File file = chooser.showSaveDialog(stage);
        if(file != null) {
            createPDF(file);
//            PrintWriter writer = new PrintWriter(file);
//            StringBuilder report = new StringBuilder();
//            writer.write("CheatBuster - raport\n");
//            for(SingleResult assessmentStage: controllers) {
//                writer.write(assessmentStage.getListenerName() + ": " + assessmentStage.getRating() + "%\n");
//                writer.write("Uwagi:\n");
//                writer.write(assessmentStage.getResultText() + "\n\n");
//            }
//
//            writer.write("Końcowa ocena: " + rating + "%");
//            writer.close();
        }
    }

    @FXML
    private void onClickOK() {
        stage.close();
    }

    public VBox getVBox() {
        return vBox;
    }

    public VBox getResultVBox() {
        return resultVBox;
    }
}
