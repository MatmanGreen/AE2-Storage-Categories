package de.gruen.ae2categories;

/** Keeps a header gesture away from vanilla item dragging/release handling. */
public interface HeaderInteraction {
    boolean categories$headerGesture();
    boolean categories$releaseHeader(int button);
}
