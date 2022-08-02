package com.ericsson.event.translator;

import io.cloudevents.CloudEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/translate")
public class EventTranslatorController {

    @RequestMapping("/")
    public String hello() {
        return "Hello EventTranslatorController";
    }

    @RequestMapping(value = "/eiffel", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToEiffelEvent(@RequestBody CloudEvent inputEvent) {
        return ResponseEntity.ok().build();
    }
}
