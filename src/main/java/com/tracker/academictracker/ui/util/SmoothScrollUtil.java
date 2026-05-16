package com.tracker.academictracker.ui.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

public final class SmoothScrollUtil {
    private static final String APPLIED_KEY = "academicTracker.smoothScroll.applied";
    private static final String TARGET_KEY = "academicTracker.smoothScroll.target";
    private static final String TIMELINE_KEY = "academicTracker.smoothScroll.timeline";
    private static final double SCROLL_MULTIPLIER = 1.35;
    private static final double FALLBACK_SCROLL_STEP = 0.0025;
    private static final Duration ANIMATION_DURATION = Duration.millis(170);

    private SmoothScrollUtil() {
    }

    public static void applyTo(Node root) {
        if (root == null) {
            return;
        }

        if (root instanceof ScrollPane scrollPane) {
            applyTo(scrollPane);
            applyTo(scrollPane.getContent());
        }

        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyTo(child);
            }
        }
    }

    public static void applyTo(ScrollPane scrollPane) {
        if (scrollPane == null) {
            return;
        }

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);

        if (!scrollPane.getStyleClass().contains("modern-scroll-pane")) {
            scrollPane.getStyleClass().add("modern-scroll-pane");
        }

        if (Boolean.TRUE.equals(scrollPane.getProperties().get(APPLIED_KEY))) {
            return;
        }

        scrollPane.getProperties().put(APPLIED_KEY, Boolean.TRUE);
        scrollPane.getProperties().put(TARGET_KEY, scrollPane.getVvalue());

        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (scrollPane.getProperties().get(TIMELINE_KEY) == null) {
                scrollPane.getProperties().put(TARGET_KEY, newValue.doubleValue());
            }
        });

        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> smoothScroll(scrollPane, event));
    }

    private static void smoothScroll(ScrollPane scrollPane, ScrollEvent event) {
        if (event.getDeltaY() == 0.0) {
            return;
        }

        double target = currentTarget(scrollPane);
        target = clamp(target - scrollAmount(scrollPane, event));
        scrollPane.getProperties().put(TARGET_KEY, target);

        Timeline previous = (Timeline) scrollPane.getProperties().get(TIMELINE_KEY);
        if (previous != null) {
            previous.stop();
        }

        Timeline timeline = new Timeline(new KeyFrame(
                ANIMATION_DURATION,
                new KeyValue(scrollPane.vvalueProperty(), target, Interpolator.EASE_BOTH)
        ));
        timeline.setOnFinished(finished -> {
            scrollPane.getProperties().remove(TIMELINE_KEY);
            scrollPane.getProperties().put(TARGET_KEY, scrollPane.getVvalue());
        });
        scrollPane.getProperties().put(TIMELINE_KEY, timeline);
        timeline.play();

        event.consume();
    }

    private static double currentTarget(ScrollPane scrollPane) {
        Object target = scrollPane.getProperties().get(TARGET_KEY);
        return target instanceof Number number ? number.doubleValue() : scrollPane.getVvalue();
    }

    private static double scrollAmount(ScrollPane scrollPane, ScrollEvent event) {
        Node content = scrollPane.getContent();
        if (content == null) {
            return event.getDeltaY() * FALLBACK_SCROLL_STEP;
        }

        Bounds contentBounds = content.getLayoutBounds();
        double scrollableHeight = contentBounds.getHeight() - scrollPane.getViewportBounds().getHeight();
        if (scrollableHeight <= 0.0) {
            return event.getDeltaY() * FALLBACK_SCROLL_STEP;
        }

        return event.getDeltaY() / scrollableHeight * SCROLL_MULTIPLIER;
    }

    private static double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}
