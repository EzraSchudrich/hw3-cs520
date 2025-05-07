package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;

public class ExpenseTrackerView extends JFrame {

  private JTable transactionsTable;
  private JButton addTransactionBtn;
  private JFormattedTextField amountField;
  private JTextField categoryField;
  private DefaultTableModel model;

  private JTextField categoryFilterField;
  private JButton categoryFilterBtn;

  private JTextField amountFilterField;
  private JButton amountFilterBtn;

  private JButton clearFilterBtn;

  private JTextField indexField;
  private JButton undoTransactionBtn;

  private List<Transaction> displayedTransactions = new ArrayList<>(); // ✅ Moved here

  public ExpenseTrackerView() {
    setTitle("Expense Tracker");
    setSize(600, 400);

    String[] columnNames = { "serial", "Amount", "Category", "Date" };
    this.model = new DefaultTableModel(columnNames, 0);

    transactionsTable = new JTable(model);
    addTransactionBtn = new JButton("Add Transaction");

    JLabel amountLabel = new JLabel("Amount:");
    NumberFormat format = NumberFormat.getNumberInstance();
    amountField = new JFormattedTextField(format);
    amountField.setColumns(10);

    JLabel categoryLabel = new JLabel("Category:");
    categoryField = new JTextField(10);

    JLabel categoryFilterLabel = new JLabel("Filter by Category:");
    categoryFilterField = new JTextField(10);
    categoryFilterBtn = new JButton("Filter by Category");

    JLabel amountFilterLabel = new JLabel("Filter by Amount:");
    amountFilterField = new JTextField(10);
    amountFilterBtn = new JButton("Filter by Amount");

    clearFilterBtn = new JButton("Clear Filter");

    JLabel undoLabel = new JLabel("Undo #:");
    indexField = new JTextField(3);
    undoTransactionBtn = new JButton("Undo Transaction");

    JPanel inputPanel = new JPanel();
    inputPanel.add(amountLabel);
    inputPanel.add(amountField);
    inputPanel.add(categoryLabel);
    inputPanel.add(categoryField);
    inputPanel.add(addTransactionBtn);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(amountFilterBtn);
    buttonPanel.add(categoryFilterBtn);
    buttonPanel.add(clearFilterBtn);

    JPanel undoPanel = new JPanel();
    undoPanel.add(undoLabel);
    undoPanel.add(indexField);
    undoPanel.add(undoTransactionBtn);

    add(inputPanel, BorderLayout.NORTH);
    add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    add(undoPanel, BorderLayout.AFTER_LINE_ENDS);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public DefaultTableModel getTableModel() {
    return model;
  }

  public JTable getTransactionsTable() {
    return transactionsTable;
  }

  public double getAmountField() {
    if (amountField.getText().isEmpty()) {
      return 0;
    } else {
      return Double.parseDouble(amountField.getText());
    }
  }

  public void setAmountField(JFormattedTextField amountField) {
    this.amountField = amountField;
  }

  public String getCategoryField() {
    return categoryField.getText();
  }

  public void setCategoryField(JTextField categoryField) {
    this.categoryField = categoryField;
  }

  public void addApplyCategoryFilterListener(ActionListener listener) {
    categoryFilterBtn.addActionListener(listener);
  }

  public String getCategoryFilterInput() {
    return JOptionPane.showInputDialog(this, "Enter Category Filter:");
  }

  public void addApplyAmountFilterListener(ActionListener listener) {
    amountFilterBtn.addActionListener(listener);
  }

  public double getAmountFilterInput() {
    String input = JOptionPane.showInputDialog(this, "Enter Amount Filter:");
    try {
      return Double.parseDouble(input);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  public void addClearFilterListener(ActionListener listener) {
    clearFilterBtn.addActionListener(listener);
  }

  public void refreshTable(List<Transaction> transactions) {
    model.setRowCount(0);
    this.displayedTransactions = transactions; // ✅ Track displayed transactions

    int rowNum = model.getRowCount();
    double totalCost = 0;

    for (Transaction t : transactions) {
      totalCost += t.getAmount();
    }

    for (Transaction t : transactions) {
      model.addRow(new Object[] { ++rowNum, t.getAmount(), t.getCategory(), t.getTimestamp() });
    }

    model.addRow(new Object[] { "Total", null, null, totalCost });
    transactionsTable.updateUI();
  }

  public JButton getAddTransactionBtn() {
    return addTransactionBtn;
  }

  public void displayFilteredTransactions(List<Transaction> filteredTransactions) {
    refreshTable(filteredTransactions);
  }

  public List<Transaction> getDisplayedTransactions() {
    return displayedTransactions;
  }

  public JButton getRemoveButton() {
    return undoTransactionBtn;
  }

  /**
   * Get raw index input (1-based serial) from the user.
   */
  public String getIndexInput() {
    return indexField.getText();
  }

}
