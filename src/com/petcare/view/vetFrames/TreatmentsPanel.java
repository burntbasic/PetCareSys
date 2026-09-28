package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Treatment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class TreatmentsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable treatmentsTable;
    private DefaultTableModel tableModel;
    private String currentFilter = "Pending";

    private VetController controller;
    private List<Treatment> treatments = new ArrayList<>();
    private User user;

    public TreatmentsPanel(User user) {

        this.user = user;
        controller = new VetController();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Treatments");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Manage your current and completed treatments");

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

        headerText.add(titleLabel);
        headerText.add(subtitleLabel);

        headerPanel.add(headerText, BorderLayout.WEST);

        JButton addTreatmentButton = new JButton("+ Add Treatment");
        headerPanel.add(addTreatmentButton, BorderLayout.EAST);
        addTreatmentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTreatmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddTreatmentDialog dialog = new AddTreatmentDialog(user);

                dialog.setVisible(true);

                if(dialog.isChanged()) {
                    loadTreatments();
                    filterTreatments(currentFilter);
                }
            }
        });

        add(headerPanel, BorderLayout.NORTH);

        // FILTERS
        JPanel filterPanel = new JPanel();

        JButton pendingButton = new JButton("Pending");
        pendingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	currentFilter = "Pending";
                filterTreatments("Pending");
            }
        });
        pendingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton completedButton = new JButton("Completed");
        completedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	currentFilter = "Completed";
                filterTreatments("Completed");
            }
        });
        completedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        

        filterPanel.add(pendingButton);
        filterPanel.add(completedButton);

        // TABLE
        String[] columns = {"Pet", "Owner", "Diagnosis", "Treatment", "Status", "Date", "Actions"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        treatmentsTable = new JTable(tableModel);
        treatmentsTable.setRowHeight(35);
        treatmentsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setText("<html><u>View</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);

                return label;
            }
        });
        treatmentsTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                int row = treatmentsTable.rowAtPoint(e.getPoint());
                int column = treatmentsTable.columnAtPoint(e.getPoint());

                if(row >= 0 && column == 6) {
                    treatmentsTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    treatmentsTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        
        treatmentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = treatmentsTable.rowAtPoint(e.getPoint());
                int column = treatmentsTable.columnAtPoint(e.getPoint());

                if(row >= 0 && column == 6) {

                    String petName = treatmentsTable.getValueAt(row, 0).toString();
                    String date = treatmentsTable.getValueAt(row, 5).toString();

                    for(Treatment treatment : treatments) {

                        String treatmentDate = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate());

                        if(treatment.getPetName().equals(petName) && treatmentDate.equals(date)) {

                            ViewTreatmentDialog dialog = new ViewTreatmentDialog(treatment);
                            dialog.setVisible(true);
                            
                            if(dialog.isChanged()) {
                                loadTreatments();
                                filterTreatments(currentFilter);
                            }
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(treatmentsTable);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        loadTreatments();
        filterTreatments(currentFilter);
    }

    private void loadTreatments() {

        tableModel.setRowCount(0);

        try {
            treatments = controller.getVetTreatments(user);

            for(Treatment treatment : treatments) {

                String date = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate());

                tableModel.addRow(new Object[] {
                        treatment.getPetName(),
                        treatment.getOwnerName(),
                        treatment.getDiagnosis(),
                        treatment.getTreatmentDescription(),
                        treatment.getStatus(),
                        date,
                        "View"
                });
            }

        } catch(SQLException e) {
            ErrorHandler.handleTableLoadError(e, treatmentsTable, tableModel);
        } catch(DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, treatmentsTable, tableModel);
        }
    }

    private void filterTreatments(String status) {

        tableModel.setRowCount(0);

        for(Treatment treatment : treatments) {

            if(treatment.getStatus() != null && treatment.getStatus().equalsIgnoreCase(status)) {

                String date = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate());

                tableModel.addRow(new Object[] {
                        treatment.getPetName(),
                        treatment.getOwnerName(),
                        treatment.getDiagnosis(),
                        treatment.getTreatmentDescription(),
                        treatment.getStatus(),
                        date,
                        "View"
                });
            }
        }
    }
}
