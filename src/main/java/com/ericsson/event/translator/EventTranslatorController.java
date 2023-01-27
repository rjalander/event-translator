package com.ericsson.event.translator;


import com.ericsson.event.translator.cdevent.CDEventsTranslator;
import com.ericsson.event.translator.eiffel.EiffelEventsTranslator;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping(value = "/translate")
public class EventTranslatorController {

    @Autowired
    CDEventsTranslator cdEventTranslator;
    @Autowired
    EiffelEventsTranslator eiffelEventsTranslator;

    @RequestMapping("/")
    public String hello() {
        return "Hello EventTranslatorController";
    }

    @RequestMapping(value = "/eiffel", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToEiffelEvent(@RequestBody CloudEvent inputEvent) {
        boolean result = eiffelEventsTranslator.translateToEiffelEvent(inputEvent);
        if(!result){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/cdevent", method = RequestMethod.POST)
    public ResponseEntity<Void> translateToCDEvent(@RequestBody String eiffelEventJson) {
        log.info("IN translateToCDEvent received eiffelEventJson {} ", eiffelEventJson);
        try {
            cdEventTranslator.translateToCDEvent(eiffelEventJson);
        } catch (Exception e) {
            log.error("Exception occurred while translateToEiffelEvent {} ", e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}
