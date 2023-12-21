package com.gladurbad.nova.util.plot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Heatmap extends JFrame {
    private final int width = 1000;
    private final int height = 1000;

    private Heatmap(double[][] points) {
        setTitle("Heatmap");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel plotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                plotPixels(g, points);
            }
        };

        getContentPane().add(plotPanel);
    }

    private void plotPixels(Graphics g, double[][] points) {
        int size = points.length;

        g.setColor(Color.BLUE);

        int scalar = 1000 / size;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double fraction = points[x][y];

                g.setColor(interpolateColor(Color.BLACK, Color.YELLOW, fraction));
                g.fillRect(x * scalar, y * scalar, scalar, scalar);
            }
        }
    }

    private Color interpolateColor(Color startColor, Color endColor, double fraction) {
        int red = (int) (startColor.getRed() + fraction * (endColor.getRed() - startColor.getRed()));
        int green = (int) (startColor.getGreen() + fraction * (endColor.getGreen() - startColor.getGreen()));
        int blue = (int) (startColor.getBlue() + fraction * (endColor.getBlue() - startColor.getBlue()));

        return new Color(red, green, blue);
    }


    public static void plot(double[][] points) {
        SwingUtilities.invokeLater(() -> {
            Heatmap example = new Heatmap(points);
            example.setVisible(true);
        });
    }
}