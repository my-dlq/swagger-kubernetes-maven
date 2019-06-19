package club.mydlq.springboothelloword;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcController {

    @GetMapping("/")
    public String root(){
        return "redirect:doc.html";
    }
}
