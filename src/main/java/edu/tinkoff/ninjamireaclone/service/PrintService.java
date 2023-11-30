package edu.tinkoff.ninjamireaclone.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PrintService {
    public void print() {
        log.info("Executed");
    }
}
