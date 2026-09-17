package com.jui.playground.exec;

import java.io.IOException;

@FunctionalInterface
public interface OutputListener {
    void println(String line) throws IOException;
}
