package com.quizzy.util;

import java.awt.BasicStroke;
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
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

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

        g2.setColor(new java.awt.Color(255, 255, 255, 255));
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

    /**
     * Creates a tinted icon ImageView for table action buttons (edit/delete).
     * Replaces the icon strokes with the specified vibrant target color.
     */
    public static ImageView createTintedActionIcon(String iconFileName, Color targetColor) {
        Image original = loadIcon(iconFileName);
        if (original == null) {
            return null;
        }

        int w = (int) original.getWidth();
        int h = (int) original.getHeight();
        if (w <= 0 || h <= 0) {
            ImageView iv = new ImageView(original);
            iv.setFitWidth(16);
            iv.setFitHeight(16);
            return iv;
        }

        PixelReader reader = original.getPixelReader();
        if (reader == null) {
            ImageView iv = new ImageView(original);
            iv.setFitWidth(16);
            iv.setFitHeight(16);
            return iv;
        }

        // Check if image has transparency or is fully opaque
        boolean hasTransparency = false;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (reader.getColor(x, y).getOpacity() < 0.9) {
                    hasTransparency = true;
                    break;
                }
            }
            if (hasTransparency) {
                break;
            }
        }

        WritableImage tinted = new WritableImage(w, h);
        PixelWriter writer = tinted.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                if (hasTransparency) {
                    double opacity = c.getOpacity();
                    if (opacity > 0.05) {
                        writer.setColor(x, y, new Color(
                                targetColor.getRed(),
                                targetColor.getGreen(),
                                targetColor.getBlue(),
                                Math.min(1.0, opacity * 1.5)
                        ));
                    } else {
                        writer.setColor(x, y, Color.TRANSPARENT);
                    }
                } else {
                    // Opaque image with white background: convert darkness to alpha
                    double brightness = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                    double strokeAlpha = 1.0 - brightness;
                    if (strokeAlpha > 0.1) {
                        writer.setColor(x, y, new Color(
                                targetColor.getRed(),
                                targetColor.getGreen(),
                                targetColor.getBlue(),
                                Math.min(1.0, strokeAlpha * 1.5)
                        ));
                    } else {
                        writer.setColor(x, y, Color.TRANSPARENT);
                    }
                }
            }
        }

        ImageView imageView = new ImageView(tinted);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    /**
     * Creates a styled Edit (update) action button with vibrant blue pen icon.
     */
    public static Button createEditActionButton() {
        Button btn = new Button();
        ImageView iv = createTintedActionIcon("pen_icon.png", Color.web("#2563eb"));
        if (iv == null) {
            iv = createTintedActionIcon("pen-update.png", Color.web("#2563eb"));
        }
        if (iv != null) {
            btn.setGraphic(iv);
        } else {
            btn.setText("✏");
        }
        btn.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #bfdbfe; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #93c5fd; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #bfdbfe; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 8; -fx-cursor: hand;"));
        return btn;
    }

    /**
     * Creates a styled Delete action button with vibrant red trash icon.
     */
    public static Button createDeleteActionButton() {
        Button btn = new Button();
        ImageView iv = createTintedActionIcon("trash_icon.png", Color.web("#dc2626"));
        if (iv == null) {
            iv = createTintedActionIcon("trash-delete.png", Color.web("#dc2626"));
        }
        if (iv != null) {
            btn.setGraphic(iv);
        } else {
            btn.setText("🗑");
        }
        btn.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #fecaca; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #fee2e2; -fx-border-color: #fca5a5; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #fecaca; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 8; -fx-cursor: hand;"));
        return btn;
    }

    /**
     * Creates a styled Detail/View action button with detail_icon.png.
     */
    public static Button createDetailActionButton() {
        Button btn = new Button();
        ImageView iv = createTintedActionIcon("detail_icon.png", Color.web("#4f46e5"));
        if (iv == null) {
            iv = createTintedActionIcon("detail-icon.png", Color.web("#4f46e5"));
        }
        if (iv != null) {
            btn.setGraphic(iv);
        } else {
            btn.setText("›");
            btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4f46e5;");
        }
        btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #eef2ff; -fx-border-color: #c7d2fe; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5 8; -fx-cursor: hand;"));
        return btn;
    }
}
