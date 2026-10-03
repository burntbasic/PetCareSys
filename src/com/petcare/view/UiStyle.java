package com.petcare.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/**
 * Shared look for every panel and dialog, so the palette and table styling
 * live in one place.
 */
public final class UiStyle {

    public static final String FONT = "Segoe UI";
    public static final Color ACCENT = new Color(0, 121, 107);
    public static final Color ACCENT_DISABLED = new Color(176, 190, 197);
    public static final Color TEXT_DARK = new Color(33, 37, 41);
    public static final Color TEXT_MUTED = new Color(108, 117, 125);
    public static final Color LINE = new Color(222, 226, 230);
    public static final Color ERROR = new Color(220, 53, 69);

    private UiStyle() {
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font(FONT, Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setGridColor(LINE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(224, 242, 241));
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(FONT, Font.BOLD, 13));
        header.setBackground(new Color(236, 241, 240));
        header.setForeground(TEXT_DARK);
        header.setReorderingAllowed(false);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }
}
