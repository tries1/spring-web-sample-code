package com.example.springwebsamplecode;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PdfPreviewController {

    @GetMapping("pdf-preview")
    public String pdfPreview() {
        return "pdf-preview";
    }

    @ResponseBody
    @GetMapping("pdf/preview")
    public void pdfPreview(HttpServletResponse res) throws Exception {
        String pdfName = "twilight.pdf";
        File f = new File("/Users/glenn/Downloads/" + pdfName);

        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "inline; filename=" + f.getName());
        res.setContentLength(Long.valueOf(f.length()).intValue());

        try (
                OutputStream os = res.getOutputStream();
                FileInputStream fis = new FileInputStream(f);
                BufferedInputStream bis = new BufferedInputStream(fis)
        ) {
            final byte data[] = new byte[1024];
            int count;
            while ((count = bis.read(data, 0, 1024)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();

        } catch (IOException e) {
            log.error("{}", e);
        }
    }
}
