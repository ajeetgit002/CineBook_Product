package com.cinebook.service.ServiceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class QrCodeService {

    public String generateQrCode(
            String bookingCode
    ) throws Exception {

        Files.createDirectories(
                Path.of("tickets")
        );

        String filePath =
                "tickets/" + bookingCode + ".png";

        BitMatrix matrix =
                new QRCodeWriter().encode(
                        bookingCode,
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                );

        Path path =
                FileSystems.getDefault()
                        .getPath(filePath);

        MatrixToImageWriter.writeToPath(
                matrix,
                "PNG",
                path
        );

        return filePath;
    }
}