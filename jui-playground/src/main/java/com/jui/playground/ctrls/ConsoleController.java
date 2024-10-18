package com.jui.playground.ctrls;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ConsoleController {

    @MessageMapping("/console")
    @SendTo("/topic/output")
    public String sendOutput(String output) {
        return output;
    }
}
