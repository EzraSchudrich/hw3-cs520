package controller;

import view.ExpenseTrackerView;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.util.List;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;
import model.Filter.CategoryFilter;
import model.Filter.AmountFilter;

import java.io.FileWriter;
import java.io.IOException;

public class ExpenseTrackerController {
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  /**
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
    // undo transaction listener
    view.getRemoveButton().addActionListener(e -> {

      String input = view.getIndexInput().trim();
      int idx;
      try {
        idx = Integer.parseInt(input) - 1; // 0 is first index
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(view,
            "Please enter a valid transaction number.",
            "Invalid Input",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      try {
        model.removeTransactionByIndex(idx);
      } catch (IndexOutOfBoundsException ex) {
        JOptionPane.showMessageDialog(view,
            "No transaction at number: " + (idx + 1),
            "Out of Range",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      refresh();
    });
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }

    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    view.getTableModel().addRow(new Object[] { t.getAmount(), t.getCategory(), t.getTimestamp() });
    refresh();
    return true;
  }

  public void applyFilter() {
    List<Transaction> filteredTransactions;
    // If no filter is specified, show all transactions.
    if (filter == null) {
      filteredTransactions = model.getTransactions();
    }
    // If a filter is specified, show only the transactions accepted by that filter.
    else {
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      filteredTransactions = filter.filter(transactions);
    }
    view.displayFilteredTransactions(filteredTransactions);
  }

    public boolean pushtoCSVfile(String CSV_target_file) {
    if(!InputValidation.isValidCSVFile(CSV_target_file)) {
      return false;
    }

    List<Transaction> transactions = model.getTransactions();
    String CSV_FILE_TYPE = ".csv";
    String target_file = CSV_target_file + CSV_FILE_TYPE;
    
    try (FileWriter writer = new FileWriter(target_file)) {
        // Write headers (column names)
        writer.append("Amount,Category,Date\n"); //do i need the serial?

        // // Write a sample row
        // writer.append("2023-10-01,25.50,Food,Lunch\n");
      for (Transaction t : transactions) {
        writer.append(t.getAmount() + "," + t.getCategory() + "," + t.getTimestamp() + "\n");
      }

        System.out.println("CSV file created: " + target_file);
    } catch (IOException e) {
        e.printStackTrace();
    }

    return true;
  }
}
