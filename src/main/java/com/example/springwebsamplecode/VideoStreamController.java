package com.example.springwebsamplecode;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.View;

@Controller
public class VideoStreamController {

    @GetMapping("/vidoe/{fileName}")
    public View video(
            ModelMap modelMap
            , @PathVariable String fileName) {

        modelMap.addAttribute("filePath", "" + fileName);
        return new StreamView();
    }
}
