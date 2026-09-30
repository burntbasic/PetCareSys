package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.petcare.model.User;
import com.petcare.util.ReportGenerator;

public class PetOwnerReportsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel reportsPanel;
	private User user;

	public PetOwnerReportsPanel(User user) {
		
		this.user = user;

		setLayout(new BorderLayout(15, 15));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// HEADER

		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Reports");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("Generate pet care reports");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
		headerText.add(titleLabel);
		headerText.add(subtitleLabel);
		headerPanel.add(headerText, BorderLayout.WEST);

		add(headerPanel, BorderLayout.NORTH);

		// REPORT CARDS

		reportsPanel = new JPanel(new GridBagLayout());

		add(reportsPanel, BorderLayout.CENTER);

		addReportCards();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				addReportCards();
			}
		});
	}

	private void addReportCards() {

		reportsPanel.removeAll();

		int availableWidth = getWidth();
		int cardWidth = 320;
		int gap = 15;
		int columns = availableWidth / (cardWidth + gap);
		if (columns < 1) {
			columns = 1;
		}
		if (columns > 4) {
			columns = 4;
		}

		JPanel[] cards = {
				createReportCard(
						"Appointment Report",
						"View your upcoming and completed appointments.",
						"Generate Appointment Report",
						"reports/pet_owner/Appointment_Report.jrxml"						
				),
				createReportCard(
						"Pet Report",
						"View your pets and their registered information.",
						"Generate Pet Report",
						"reports/pet_owner/Pet_Report.jrxml"
				),
				createReportCard(
						"Treatment Report",
						"View treatments and medical history of your pets.",
						"Generate Treatment Report",
						"reports/pet_owner/Treatment_Report.jrxml"						
				),
				createReportCard(
						"Medical Record Report",
						"View the medical records and treatment history of your pets.",
						"Generate Medical Report",
						"reports/pet_owner/Medical_Report.jrxml"						
				)
		};

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, gap, gap);
		gbc.weightx = 0;
		gbc.weighty = 0;

		for (int i = 0; i < cards.length; i++) {

			gbc.gridx = i % columns;
			gbc.gridy = i / columns;
			reportsPanel.add(cards[i], gbc);
		}

		// FILL REMAINING SPACE

		GridBagConstraints fillerGbc = new GridBagConstraints();

		fillerGbc.gridx = columns;
		fillerGbc.gridy = 0;
		fillerGbc.gridheight = (cards.length + columns - 1) / columns;
		fillerGbc.weightx = 1.0;
		fillerGbc.weighty = 1.0;
		fillerGbc.fill = GridBagConstraints.BOTH;

		reportsPanel.add(new JPanel(), fillerGbc);
		reportsPanel.revalidate();
		reportsPanel.repaint();
	}

	private JPanel createReportCard(String title, String description, String buttonText, String reportPath) {

		JPanel card = new JPanel(new BorderLayout(10, 10));
		card.setPreferredSize(new Dimension(320, 150));
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 17));

		JLabel descriptionLabel = new JLabel("<html><div style='width:220px;'>" + description + "</div></html>");
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(titleLabel);
		textPanel.add(descriptionLabel);

		JButton generateButton = new JButton(buttonText);
		generateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportGenerator.showReport(reportPath, "OWNER_ID", user.getId());
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(generateButton);

		card.add(textPanel, BorderLayout.CENTER);
		card.add(buttonPanel, BorderLayout.SOUTH);

		return card;
	}
}
