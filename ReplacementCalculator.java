import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.table.DefaultTableModel;

public class ReplacementCalculator {

    private DefaultTableModel tableModel1;
    private DefaultTableModel tableModel2;
    private JFrame frame;

    public ReplacementCalculator() {

        frame = new JFrame("Replacement Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        

        String[] options = { "One Machine", "Two Machines" };
        int choice = JOptionPane.showOptionDialog(frame, "Select number of machines", "Machine Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            OneMachineGUI();
        } else if (choice == 1) {
            TwoMachinesGUI();
        }

    }

    private void OneMachineGUI() {

        JPanel input = new JPanel();
        input.setLayout(new GridLayout(8, 2));
        input.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel capitalLabel = new JLabel("Capital Cost:");
        JLabel resaleLabel = new JLabel("Resale Value:");
        JLabel yearsLabel = new JLabel("Number of Years:");
        JLabel maintenanceLabel = new JLabel("Yearly Maintenance Cost:");
        JTextField capitalField = new JTextField();
        JTextField resaleField = new JTextField();
        JTextField yearsField = new JTextField();
        JTextField maintenanceField = new JTextField();

        JCheckBox constResaleValue = new JCheckBox("Is the Resale Value constant?");

        constResaleValue.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    resaleField.setEnabled(false);
                } else {
                    resaleField.setEnabled(true);
                }
            }
        });

        JCheckBox interestCheck = new JCheckBox("Does the value of money changes with time?");
        JLabel interestLabel = new JLabel("Interest Rate:");
        JTextField interestField = new JTextField();
        interestLabel.setVisible(false);
        interestField.setVisible(false);

        interestCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    interestField.setVisible(true);
                    interestLabel.setVisible(true);
                } else {
                    interestField.setVisible(false);
                    interestLabel.setVisible(false);
                }
            }
        });

        JButton submit = new JButton("Submit");

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int capitalCost = Integer.parseInt(capitalField.getText());
                    int years = Integer.parseInt(yearsField.getText());

                    String[] maintenanceCostStr = maintenanceField.getText().split(",");
                    int[] maintenanceCost = new int[maintenanceCostStr.length];
                    for (int i = 0; i < maintenanceCostStr.length; i++) {
                        maintenanceCost[i] = Integer.parseInt(maintenanceCostStr[i].trim());
                    }

                    int[] resaleValue;
                    if (constResaleValue.isSelected()) {
                        resaleValue = new int[years];
                        for (int i = 0; i < years; i++) {
                            resaleValue[i] = Integer.parseInt(resaleField.getText());
                        }
                    } else {
                        String[] resaleStr = resaleField.getText().split(",");
                        resaleValue = new int[resaleStr.length];
                        for (int i = 0; i < resaleStr.length; i++) {
                            resaleValue[i] = Integer.parseInt(resaleStr[i].trim());
                        }
                    }

                    if (interestCheck.isSelected() && !interestField.getText().isEmpty()) {
                        double interestRate = Double.parseDouble(interestField.getText());
                        double v = 1 / (1 + (interestRate / 100.0));
                        calculateWithInterest(capitalCost, years, resaleValue, maintenanceCost, v);
                    } else {
                        calculateWithNoInterest(capitalCost, years, resaleValue, maintenanceCost);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            private void calculateWithInterest(int capitalCost, int years, int[] resaleValue, int[] maintenanceCost,
                    double v) {
                double num, den = 0, result, lowest = 0;
                double cummMaintenanceCost = 0;
                int lowestYear = 1;
                num = capitalCost;
                for (int i = 0; i < maintenanceCost.length; i++) {
                    cummMaintenanceCost = cummMaintenanceCost + maintenanceCost[i];
                    num = num + (maintenanceCost[i] * Math.pow(v, i));
                    den = den + Math.pow(v, i);
                    result = num - (resaleValue[i] * Math.pow(v, i + 1));
                    result = result / den;
                    tableModel1.addRow(new Object[] { i + 1, resaleValue[i], maintenanceCost[i], cummMaintenanceCost,
                            num - (resaleValue[i] * Math.pow(v, i + 1)), result });
                    if (i == 0) {
                        lowest = result;
                        lowestYear = i + 1;
                    } else if (i > 0) {
                        if (lowest > result) {
                            lowest = result;
                            lowestYear = i + 1;
                        }
                    }
                }
                JOptionPane.showMessageDialog(frame, "The lowest year is " + lowestYear + " with a value of " + lowest,
                        "Appropriate year to replace", JOptionPane.INFORMATION_MESSAGE);
            }

            private void calculateWithNoInterest(int capitalCost, int years, int[] resaleValue, int[] maintenanceCost) {
                double result, lowest = 0;
                int lowestYear = 1;
                result = capitalCost;
                for (int i = 0; i < maintenanceCost.length; i++) {
                    result = result + maintenanceCost[i];
                    tableModel1.addRow(new Object[] { i + 1, resaleValue[i], maintenanceCost[i], (result - capitalCost),
                            (result - resaleValue[i]), (result - resaleValue[i]) / (i + 1) });
                    if (i == 0) {
                        lowest = ((result - resaleValue[i]) / (i + 1));
                        lowestYear = i + 1;
                    } else if (i > 0) {
                        if (lowest > ((result - resaleValue[i]) / (i + 1))) {
                            lowest = ((result - resaleValue[i]) / (i + 1));
                            lowestYear = i + 1;
                        }
                    }
                }
                JOptionPane.showMessageDialog(frame, "The lowest year is " + lowestYear + " with a value of " + lowest,
                        "Appropriate year to replace", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        input.add(capitalLabel);
        input.add(capitalField);
        input.add(resaleLabel);
        input.add(resaleField);
        input.add(constResaleValue);
        input.add(new JLabel());
        input.add(yearsLabel);
        input.add(yearsField);
        input.add(maintenanceLabel);
        input.add(maintenanceField);
        input.add(interestCheck);
        input.add(new JLabel());
        input.add(interestLabel);
        input.add(interestField);
        input.add(new JLabel());
        input.add(submit);

        String[] columns = { "Year", "Resale Value", "Maintenance Cost", "Cummulative Maintenance Cost", "Total Cost",
                "Annual Maintenance Cost" };
        tableModel1 = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel1);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.add(input, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.setSize(1100, 750);
        frame.setVisible(true);
    }

    private void TwoMachinesGUI() {
        JPanel input = new JPanel();
        input.setLayout(new GridLayout(7, 4));
        input.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel capitalLabel1 = new JLabel("Capital Cost of Machine A:");
        JLabel resaleLabel1 = new JLabel("Resale Value of Machine A:");
        JLabel yearsLabel1 = new JLabel("Number of Years of Machine A:");
        JLabel maintenanceLabel1 = new JLabel("Maintenance Cost of Machine A:");
        JTextField capitalField1 = new JTextField();
        JTextField resaleField1 = new JTextField();
        JTextField yearsField1 = new JTextField();
        JTextField maintenanceField1 = new JTextField();
        JLabel capitalLabel2 = new JLabel("Capital Cost of Machine B:");
        JLabel resaleLabel2 = new JLabel("Resale Value of Machine B:");
        JLabel yearsLabel2 = new JLabel("Number of Years of Machine B:");
        JLabel maintenanceLabel2 = new JLabel("Maintenance Cost  of Machine B:");
        JTextField capitalField2 = new JTextField();
        JTextField resaleField2 = new JTextField();
        JTextField yearsField2 = new JTextField();
        JTextField maintenanceField2 = new JTextField();

        JCheckBox constResaleValue1 = new JCheckBox("Is the Resale Value constant");
        JCheckBox constResaleValue2 = new JCheckBox("Is the Resale Value constant");

        constResaleValue1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    resaleField1.setEnabled(false);
                } else {
                    resaleField1.setEnabled(true);
                }
            }
        });
        constResaleValue2.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    resaleField2.setEnabled(false);
                } else {
                    resaleField2.setEnabled(true);
                }
            }
        });
        JButton submit = new JButton("Submit");

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int capitalCost1 = Integer.parseInt(capitalField1.getText());
                    int years1 = Integer.parseInt(yearsField1.getText());
                    int capitalCost2 = Integer.parseInt(capitalField2.getText());
                    int years2 = Integer.parseInt(yearsField2.getText());

                    String[] maintenanceCostStr1 = maintenanceField1.getText().split(",");
                    int[] maintenanceCost1 = new int[maintenanceCostStr1.length];
                    for (int i = 0; i < maintenanceCostStr1.length; i++) {
                        maintenanceCost1[i] = Integer.parseInt(maintenanceCostStr1[i].trim());
                    }
                    String[] maintenanceCostStr2 = maintenanceField2.getText().split(",");
                    int[] maintenanceCost2 = new int[maintenanceCostStr2.length];
                    for (int i = 0; i < maintenanceCostStr2.length; i++) {
                        maintenanceCost2[i] = Integer.parseInt(maintenanceCostStr2[i].trim());
                    }

                    int[] resaleValue1;
                    if (constResaleValue1.isSelected()) {
                        resaleValue1 = new int[years1];
                        for (int i = 0; i < years1; i++) {
                            resaleValue1[i] = Integer.parseInt(resaleField1.getText());
                        }
                    } else {
                        String[] resaleStr1 = resaleField1.getText().split(",");
                        resaleValue1 = new int[resaleStr1.length];
                        for (int i = 0; i < resaleStr1.length; i++) {
                            resaleValue1[i] = Integer.parseInt(resaleStr1[i].trim());
                        }
                    }
                    int[] resaleValue2;
                    if (constResaleValue2.isSelected()) {
                        resaleValue2 = new int[years2];
                        for (int i = 0; i < years2; i++) {
                            resaleValue2[i] = Integer.parseInt(resaleField2.getText());
                        }
                    } else {
                        String[] resaleStr2 = resaleField2.getText().split(",");
                        resaleValue2 = new int[resaleStr2.length];
                        for (int i = 0; i < resaleStr2.length; i++) {
                            resaleValue2[i] = Integer.parseInt(resaleStr2[i].trim());
                        }
                    }
                    double lowest1 = -1, lowest2 = -1;
                    lowest1 = calculateWithNoInterest(capitalCost1, years1, resaleValue1, maintenanceCost1, tableModel1,
                            lowest1);
                    lowest2 = calculateWithNoInterest(capitalCost2, years2, resaleValue2, maintenanceCost2, tableModel2,
                            lowest2);

                    if (lowest1 > lowest2) {
                        calculateYear(lowest2, maintenanceCost1);
                    } else if (lowest1 < lowest2) {
                        JOptionPane.showMessageDialog(null, "No need for replacement");
                    } else if (lowest1 == lowest2) {
                        JOptionPane.showMessageDialog(null, "Same value");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }

            private void calculateYear(double lowest, int[] maintenanceCost) {
                for (int i = 0; i < maintenanceCost.length; i++) {
                    if (lowest < maintenanceCost[i]) {
                        JOptionPane.showMessageDialog(null,
                                "The running cost of machine A in year " + (i + 1)
                                        + " is more than the lowest average running cost per year of machine B '"
                                        + lowest + "'");
                        return;
                    }
                }
            }

            public double calculateWithNoInterest(int capitalCost, int years, int[] resaleValue, int[] maintenanceCost,
                    DefaultTableModel tableModel, double lowest) {
                double result;
                result = capitalCost;
                for (int i = 0; i < maintenanceCost.length; i++) {
                    result = result + maintenanceCost[i];
                    tableModel.addRow(new Object[] { i + 1, (result - resaleValue[i]) / (i + 1) });
                    if (i == 0) {
                        lowest = ((result - resaleValue[i]) / (i + 1));
                    } else if (i > 0) {
                        if (lowest > ((result - resaleValue[i]) / (i + 1))) {
                            lowest = (result - resaleValue[i]) / (i + 1);
                        }
                    }
                }
                return lowest;
            }
        });

        input.add(capitalLabel1);
        input.add(capitalField1);
        input.add(capitalLabel2);
        input.add(capitalField2);
        input.add(resaleLabel1);
        input.add(resaleField1);
        input.add(resaleLabel2);
        input.add(resaleField2);
        input.add(constResaleValue1);
        input.add(new JLabel());
        input.add(constResaleValue2);
        input.add(new JLabel());
        input.add(yearsLabel1);
        input.add(yearsField1);
        input.add(yearsLabel2);
        input.add(yearsField2);
        input.add(maintenanceLabel1);
        input.add(maintenanceField1);
        input.add(maintenanceLabel2);
        input.add(maintenanceField2);
        input.add(new JLabel());
        input.add(new JLabel());
        input.add(new JLabel());
        input.add(new JLabel());
        input.add(new JLabel());
        input.add(new JLabel());
        input.add(new JLabel());
        input.add(submit);

        String[] columns = { "Year", "Annual Maintenance Cost" };
        tableModel1 = new DefaultTableModel(columns, 0);
        JTable table1 = new JTable(tableModel1);
        tableModel2 = new DefaultTableModel(columns, 0);
        JTable table2 = new JTable(tableModel2);
        JScrollPane tableScrollPane1 = new JScrollPane(table1);
        JScrollPane tableScrollPane2 = new JScrollPane(table2);

        JPanel table = new JPanel();
        table.setLayout(new GridLayout(1, 2));
        table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.add(tableScrollPane1);
        table.add(tableScrollPane2);

        frame.add(input, BorderLayout.NORTH);
        frame.add(table, BorderLayout.CENTER);
        frame.setSize(850, 750);
        frame.setVisible(true);

    }
    public static void main(String[] args) {
        new ReplacementCalculator();
    }
}