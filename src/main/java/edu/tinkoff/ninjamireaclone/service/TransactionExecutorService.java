package edu.tinkoff.ninjamireaclone.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TransactionExecutorService {

    @Transactional
    public <R> R execute(Supplier<R> func) {
        return func.get();
    }
}
