package piyush.shop.com.salesorder;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by hp on 24-Jul-17.
 */
public class DocumentWriter {

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);

//    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//            Font.NORMAL, BaseColor.RED);

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.NORMAL);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static Font medBold = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);

    private static Font largeBold = new Font(Font.FontFamily.TIMES_ROMAN, 22,
            Font.BOLD);

    private void addMetaData(Document document) {
        document.addTitle("Purchase Order Report By Piyush Jain");
        document.addSubject("Purchase Order");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Piyush Jain");
        document.addCreator("Piyush Jain");
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addTitlePage(Document document, String mSupplierName)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.add(new Paragraph("Supplier Name: " + mSupplierName + "", catFont));
        addEmptyLine(preface, 1);
        addEmptyLine(preface, 3);
        document.add(preface);
    }

    private void createTable(Document document, List<Product> productList)
            throws Exception {

        PdfPTable table = new PdfPTable(6);

        Font font = new Font(medBold);
        font.setColor(new BaseColor(255, 255, 255, 255));

        PdfPCell c1 = new PdfPCell(new Phrase("Product Name", font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBackgroundColor(new BaseColor(0, 0, 0, 255));
        c1.setBorderColor(new BaseColor(255, 255, 255, 255));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Description", font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBackgroundColor(new BaseColor(0, 0, 0, 255));
        c1.setBorderColor(new BaseColor(255, 255, 255, 255));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Order Quantity", font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBackgroundColor(new BaseColor(0, 0, 0, 255));
        c1.setBorderColor(new BaseColor(255, 255, 255, 255));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Cost Price", font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBackgroundColor(new BaseColor(0, 0, 0, 255));
        c1.setBorderColor(new BaseColor(255, 255, 255, 255));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("MRP", font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBackgroundColor(new BaseColor(0, 0, 0, 255));
        c1.setBorderColor(new BaseColor(255, 255, 255, 255));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Total Amount", font));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBackgroundColor(new BaseColor(0, 0, 0, 255));
        c1.setBorderColor(new BaseColor(255, 255, 255, 255));
        table.addCell(c1);

        table.setHeaderRows(1);

        font = new Font(subFont);
        font.setColor(new BaseColor(0, 0, 0, 255));

        float finalAmount = 0;

        for (int i = 0; i < productList.size(); i++) {

            Product product = productList.get(i);
            String productName = product.getName();

            c1 = new PdfPCell(new Phrase(productName, font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c1);

            String productDescription = product.getDescription();
            c1 = new PdfPCell(new Phrase(productDescription, font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c1);

            int orderQuantity = product.getGeneratedOrderQuantity();
            c1 = new PdfPCell(new Phrase(orderQuantity + "", font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(product.getCostPrice() + "", font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c1);

            float mrp = product.getMRP();
            String sMRP = "-";

            if (mrp > 0) {
                sMRP = String.valueOf(mrp);
            }

            c1 = new PdfPCell(new Phrase(product.getMRP() + "", font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c1);

            float totalAmount = product.getGeneratedOrderQuantity() * product.getCostPrice();

            c1 = new PdfPCell(new Phrase(totalAmount + "", font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c1);

            finalAmount += totalAmount;
        }

        document.add(table);

        String finalAmountString = "Total : " + finalAmount;

        font = new Font(largeBold);
        font.setColor(new BaseColor(0, 0, 0, 255));

        Paragraph paragraph = new Paragraph(new Phrase(finalAmountString, font));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    public void dumpSupplierPurchaseOrderIntoPdf(String mSupplierName, List<Product> productList) {
        try {
            Document document = new Document();

            (new SOUtility()).createPurchaseOrderDirectory();

            PdfWriter.getInstance(document, new FileOutputStream(SOUtility.BASE_FILE_PATH + "purchaseOrder/" + mSupplierName + ".pdf"));
            document.open();
            addMetaData(document);
            addTitlePage(document, mSupplierName);
            createTable(document, productList);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
