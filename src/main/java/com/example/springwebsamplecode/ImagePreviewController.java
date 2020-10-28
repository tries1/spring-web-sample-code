package com.example.springwebsamplecode;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class ImagePreviewController {

    @GetMapping("image-preview")
    public String imagePreview() {
        return "image-preview";
    }

    @ResponseBody
    @GetMapping("image/preview")
    public void imagePreview(
            @RequestParam String name
            , HttpServletResponse res) throws Exception {
        String imageName = (name.equals("sample")) ? "dummy_image.png" : "sample_image.jpg";
        File f = new File("/Users/glenn_mac/Downloads/" + imageName);

        res.setContentType("image");
        //res.setContentLength(f.length());

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
