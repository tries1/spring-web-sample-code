package com.example.springwebsamplecode;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

import java.io.File;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamView extends AbstractView {
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filePath = Optional.ofNullable(model.get("filePath"))
                .map(Object::toString)
                .orElse("");

        long rangeStart = 0;
        long rangeEnd = 0;
        boolean isPart = false;

        try (RandomAccessFile raf = new RandomAccessFile(new File(filePath), "r")) {
            long movieSize = raf.length();
            String range = request.getHeader("range");
            log.info("request range : {}", range);

            if (StringUtils.isEmpty(range) == false) {
                if (range.endsWith("-")) {
                    range = range + (movieSize - 1);
                }

                int idxm = range.trim().indexOf("-");
                rangeStart = Long.parseLong(range.substring(6, idxm));
                rangeEnd = Long.parseLong(range.substring(idxm + 1));
                if (rangeStart > 0) {
                    isPart = true;
                }
            } else {
                rangeStart = 0L;
                rangeEnd = movieSize - 1;
            }

            long partSize = rangeEnd - rangeStart + 1;
            log.info("accepted range, rangeStart : {}, rangeEnd : {}, partSize : {}, isPart : {},", rangeStart, rangeEnd, partSize, isPart);

            response.reset();
            response.setStatus(isPart ? 206 : 200);
            response.setContentType("video/mp4");

            response.setHeader("Content-Range", String.format("bytes %d-d/d", rangeStart, rangeEnd, movieSize));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(partSize));

            OutputStream out = response.getOutputStream();
            raf.seek(rangeStart);

            int bufferSize = 8 * 1024;
            byte[] buf = new byte[bufferSize];
            do {
                int block = partSize > bufferSize ? bufferSize : (int) partSize;
                int len = raf.read(buf, 0, block);
                out.write(buf, 0, len);
                partSize -= block;
            } while (partSize > 0);
            log.info("send %d, %d, %d", movieSize, rangeStart, rangeEnd);
        } catch (RuntimeException e) {
            log.error("error : {}", e.getMessage());
        }
    }
}
