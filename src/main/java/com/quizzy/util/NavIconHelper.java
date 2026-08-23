package com.quizzy.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NavIconHelper {

    private static final String ICON_BASE_PATH = "/com/quizzy/icons/";

    static {
        ensureTopicIconExists();
    }

    /**
     * Ensures topic_icon.png exists on disk/target so it can be loaded reliably.
     */
    private static void ensureTopicIconExists() {
        try {
            if (NavIconHelper.class.getResource(ICON_BASE_PATH + "topic_icon.png") == null) {
                BufferedImage img = generateTopicIconImage();
                // Attempt to write to src/main/resources and target/classes if available
                File srcDir = new File("src/main/resources/com/quizzy/icons");
                if (srcDir.exists()) {
                    File outFile = new File(srcDir, "topic_icon.png");
                    if (!outFile.exists()) {
                        ImageIO.write(img, "png", outFile);
                    }
                }
                File targetDir = new File("target/classes/com/quizzy/icons");
                if (targetDir.exists()) {
                    File targetFile = new File(targetDir, "topic_icon.png");
                    if (!targetFile.exists()) {
                        ImageIO.write(img, "png", targetFile);
                    }
                }
            }
        } catch (Exception e) {
            // Non-critical fallback
        }
    }

    private static BufferedImage generateTopicIconImage() {
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.setColor(new Color(255, 255, 255, 255));
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Folder tab
        g2.draw(new RoundRectangle2D.Float(4f, 6f, 10f, 6f, 3f, 3f));
        // Folder body
        g2.draw(new RoundRectangle2D.Float(4f, 9f, 24f, 16f, 4f, 4f));

        g2.dispose();
        return image;
    }

    /**
     * Loads or creates an icon Image for navigation items.
     */
    public static Image loadIcon(String iconFileName) {
        try {
            InputStream is = NavIconHelper.class.getResourceAsStream(ICON_BASE_PATH + iconFileName);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception ignored) {
        }

        // Fallback for topic_icon if not yet loaded from resources
        if ("topic_icon.png".equalsIgnoreCase(iconFileName)) {
            try {
                BufferedImage bi = generateTopicIconImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", baos);
                return new Image(new ByteArrayInputStream(baos.toByteArray()));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Creates a styled ImageView with appropriate color adjustment.
     */
    public static ImageView createNavIcon(String iconFileName, boolean isActive) {
        Image img = loadIcon(iconFileName);
        if (img == null) {
            return null;
        }

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(18);
        imageView.setFitHeight(18);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        ColorAdjust colorAdjust = new ColorAdjust();
        if (isActive) {
            colorAdjust.setBrightness(0.0);
        } else {
            colorAdjust.setBrightness(-0.65);
        }
        imageView.setEffect(colorAdjust);

        return imageView;
    }

    /**
     * Configures a navigation button with icon, text, and reactive hover/active styling.
     */
    public static void setupNavButton(Button button, String title, String iconFileName, boolean isActive) {
        button.setText(title);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setGraphicTextGap(12);

        ImageView iconView = createNavIcon(iconFileName, isActive);
        if (iconView != null) {
            button.setGraphic(iconView);

            ColorAdjust normalAdjust = new ColorAdjust();
            normalAdjust.setBrightness(-0.65);

            ColorAdjust hoverAdjust = new ColorAdjust();
            hoverAdjust.setBrightness(-0.9);

            button.hoverProperty().addListener((obs, oldVal, isHovered) -> {
                if (!button.getStyleClass().contains("nav-button-active")) {
                    iconView.setEffect(isHovered ? hoverAdjust : normalAdjust);
                }
            });
        }

        if (isActive) {
            button.getStyleClass().setAll("nav-button-active");
        } else {
            button.getStyleClass().setAll("nav-button");
        }
    }

    /**
     * Updates active state and icon brightness for a navigation button.
     */
    public static void setActiveState(Button button, boolean isActive) {
        if (isActive) {
            button.getStyleClass().setAll("nav-button-active");
            if (button.getGraphic() instanceof ImageView iv) {
                ColorAdjust activeAdjust = new ColorAdjust();
                activeAdjust.setBrightness(0.0);
                iv.setEffect(activeAdjust);
            }
        } else {
            button.getStyleClass().setAll("nav-button");
            if (button.getGraphic() instanceof ImageView iv) {
                ColorAdjust normalAdjust = new ColorAdjust();
                normalAdjust.setBrightness(-0.65);
                iv.setEffect(normalAdjust);
            }
        }
    }
}
