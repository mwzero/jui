package com.jui.utils;

import java.util.EnumSet;
import java.util.Set;

/**
 * The {@code AsciiProgressBar} class provides a simple ASCII-based progress bar
 * for console applications. It allows customization of displayed indicators,
 * including percentage completion, processing speed, and estimated time of arrival (ETA).
 *
 * <p>Usage example:
 * <pre>{@code
 * Set<AsciiProgressBar.Indicator> indicators = EnumSet.of(
 *     AsciiProgressBar.Indicator.PERCENTAGE,
 *     AsciiProgressBar.Indicator.SPEED,
 *     AsciiProgressBar.Indicator.ETA
 * );
 * AsciiProgressBar progressBar = new AsciiProgressBar(50, indicators);
 *
 * for (int i = 0; i <= 100; i++) {
 *     progressBar.updateProgress(i / 100.0, speed, eta);
 *     Thread.sleep(100); // Simulate work
 * }
 * }</pre>
 *
 * @author Your Name
 * @version 1.0
 * @since 2024-11-09
 */
public class AsciiProgressBar {
	
    /**
     * Enumeration representing the available indicators for the progress bar.
     */
    public enum Indicator {
        PERCENTAGE,
        SPEED,
        ETA
    }

    private final int barLength;
    private final Set<Indicator> indicators;

    /**
     * Constructs an {@code AsciiProgressBar} with the specified length and indicators.
     *
     * @param barLength  the total length of the progress bar
     * @param indicators the set of indicators to display
     */
    public AsciiProgressBar(int barLength, Set<Indicator> indicators) {
        this.barLength = barLength;
        this.indicators = indicators;
    }

    /**
     * Updates the progress bar with the provided values for each indicator.
     *
     * @param progress the current progress as a value between 0.0 and 1.0
     * @param speed    the current speed as a percentage per second (optional)
     * @param eta      the estimated time remaining in seconds (optional)
     */
    public synchronized void updateProgress(double progress, Double speed) {
        // Ensure progress is within the range [0.0, 1.0]
        progress = Math.max(0.0, Math.min(1.0, progress));

        int filledLength = (int) (barLength * progress);
        StringBuilder bar = new StringBuilder("\r["); // \r returns to the beginning of the line

        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("=");
            } else if (i == filledLength && progress < 1.0) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("] ");

        // Append percentage indicator if specified
        if (indicators.contains(Indicator.PERCENTAGE)) {
            String percent = String.format("%3d%% ", (int) (progress * 100));
            bar.append(percent);
        }

        // Append speed indicator if specified and provided
        if (indicators.contains(Indicator.SPEED) && speed != null) {
            String speedStr = String.format("%3.1f%%/s", speed);
            bar.append("| Speed: ").append(speedStr);
        }

        // Calculate and append ETA if specified
        if (indicators.contains(Indicator.ETA)) {
            double eta = (progress > 0 && speed != null && speed > 0) ? (1.0 - progress) / (speed / 100) : Double.POSITIVE_INFINITY;
            String etaStr = Double.isInfinite(eta) ? "∞" : String.format("%5.1f", eta);
            bar.append(" | ETA: ").append(etaStr).append("s");
        }

        // Indicate completion if progress is 100%
        if (progress >= 1.0) {
            bar.append(" | Completed");
        }

        // Print the progress bar on the same line
        System.out.print(bar.toString());
        //System.out.flush();
    }


    /**
     * Demonstrates the usage of {@code AsciiProgressBar}.
     * <p><strong>Note:</strong> This program functions correctly only when executed from a console.</p>
     *
     * @param args command-line arguments (not used)
     * @throws InterruptedException if the thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        Set<Indicator> indicators = EnumSet.of(Indicator.PERCENTAGE, Indicator.SPEED, Indicator.ETA);
        AsciiProgressBar progressBar = new AsciiProgressBar(50, indicators);

        double totalWork = 100.0;
        double workDone = 0.0;
        long startTime = System.currentTimeMillis();

        while (workDone <= totalWork) {
            // Simulate work
            Thread.sleep(100);
            workDone += 1.0;

            // Calculate progress
            double progress = workDone / totalWork;

            // Calculate speed (percentage per second)
            long currentTime = System.currentTimeMillis();
            double elapsedTime = (currentTime - startTime) / 1000.0; // in seconds
            Double speed = elapsedTime > 0 ? (progress / elapsedTime) * 100 : null;

            // Update the progress bar
            progressBar.updateProgress(progress, speed);
        }
        System.out.println(); // Move to the next line after completion
    }
}
