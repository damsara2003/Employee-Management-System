package util;

import model.ReportData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ReportExporter {
    private ReportExporter() {}

    public static void exportCsv(ReportData data, File file) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writeCsvRow(writer, data.getHeaders());
            for (Object[] row : data.getRows()) writeCsvRow(writer, row);
        }
    }

    private static void writeCsvRow(Writer writer, Object[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) writer.write(',');
            String value = values[i] == null ? "" : values[i].toString();
            writer.write('"');
            writer.write(value.replace("\"", "\"\""));
            writer.write('"');
        }
        writer.write(System.lineSeparator());
    }

    public static void exportPdf(ReportData data, File file) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 14 Tf\n40 800 Td\n");
        content.append(text(data.getTitle())).append(" Tj\n/F1 8 Tf\n0 -18 Td\n");

        String header = String.join(" | ", data.getHeaders());
        content.append(text(trim(header, 110))).append(" Tj\n0 -14 Td\n");
        content.append("0.5 w\n");

        int count = 0;
        for (Object[] row : data.getRows()) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                if (i > 0) line.append(" | ");
                line.append(row[i] == null ? "" : row[i]);
            }
            content.append(text(trim(line.toString(), 110))).append(" Tj\n0 -12 Td\n");
            count++;
            if (count >= 52) break;
        }
        content.append("ET");

        byte[] stream = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        String pdf = buildPdf(stream);
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(pdf.getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    private static String buildPdf(byte[] stream) {
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        int[] offsets = new int[6];
        offsets[1] = pdf.length();
        pdf.append("1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n");
        offsets[2] = pdf.length();
        pdf.append("2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n");
        offsets[3] = pdf.length();
        pdf.append("3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>endobj\n");
        offsets[4] = pdf.length();
        pdf.append("4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n");
        offsets[5] = pdf.length();
        pdf.append("5 0 obj<< /Length ").append(stream.length).append(" >>stream\n");
        pdf.append(new String(stream, StandardCharsets.ISO_8859_1));
        pdf.append("\nendstream endobj\n");
        int xref = pdf.length();
        pdf.append("xref\n0 6\n0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) pdf.append(String.format("%010d 00000 n \n", offsets[i]));
        pdf.append("trailer<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF");
        return pdf.toString();
    }

    private static String text(String s) {
        return "(" + s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)") + ")";
    }

    private static String trim(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    public static String timestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }
}
