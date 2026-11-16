package de.gruen.ae2categories;

public interface CategoryView {
    String categories$header(int visibleSlot);
    int categories$itemCount();
    boolean categories$isCollapsed(String title);
    void categories$toggle(String title);
    void categories$invalidate();
}
