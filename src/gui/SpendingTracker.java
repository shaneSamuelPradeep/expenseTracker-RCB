package gui;

import java.awt.Color;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import db.DbConnect;
import java.text.SimpleDateFormat;

public class SpendingTracker extends javax.swing.JFrame {

    public SpendingTracker() {
        initComponents();
        displayOptions("Category");
        d.setSelectableDateRange(null, new java.util.Date());
        getEntries();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        d.setDate(new java.util.Date());
    }

    private void displayOptions(String type) {
        try {
            category.removeAllItems();
            String query = type.equals("Category") ? "select * from category_info" : "select * from person_info";
            ResultSet rs = DbConnect.st.executeQuery(query);
            while (rs.next()) {
                category.addItem(rs.getString(type.equals("Category") ? "category" : "person"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private int getLimit(String month, int year) {
        try {
            String query = "SELECT limit_value FROM limits WHERE month = ? AND year = ?";
            PreparedStatement pst = DbConnect.c.prepareStatement(query);
            pst.setString(1, month);
            pst.setInt(2, year);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("limit_value");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return Integer.MAX_VALUE; // Return a very high value if no limit is set
    }

    private void getEntries() {
        try {
            javax.swing.table.DefaultTableModel dtm = (javax.swing.table.DefaultTableModel) table.getModel();
            int rc = dtm.getRowCount();
            while (rc-- != 0) {
                dtm.removeRow(0);
            }
            java.time.LocalDate cd = java.time.LocalDate.now();
            java.time.LocalDate bd = cd.minusDays(20);

            String selectedExpenseType = expenseType.getSelectedItem().toString();
            String query = selectedExpenseType.equals("Category")
                    ? "SELECT sid AS id, category AS type, sdate AS date, amount FROM spendings ORDER BY sdate DESC"
                    : "SELECT pid AS id, person AS type, pdate AS date, amount FROM person_spendings ORDER BY pdate DESC";

            ResultSet rs = DbConnect.st.executeQuery(query);
            int total = 0;
            int monthlyTotal = 0;
            int yearlyTotal = 0;
            String exceededMonth = null;
            int exceededYear = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                Date date = rs.getDate("date");
                int amount = rs.getInt("amount");
                dtm.addRow(new Object[] { id, type, date, amount });
                total += amount;

                java.time.LocalDate entryDate = date.toLocalDate();
                if (entryDate.getMonth() == cd.getMonth() && entryDate.getYear() == cd.getYear()) {
                    monthlyTotal += amount;
                }
                if (entryDate.getYear() == cd.getYear()) {
                    yearlyTotal += amount;
                }
            }

            int monthlyLimit = getLimit(cd.getMonth().toString(), cd.getYear());
            int yearlyLimit = getLimit(null, cd.getYear());

            totalAmount.setText(total + "€");

            if (monthlyTotal > monthlyLimit) {
                exceededMonth = cd.getMonth().toString();
                exceededYear = cd.getYear();
            } else if (yearlyTotal > yearlyLimit) {
                exceededYear = cd.getYear();
            }

            if (exceededMonth != null || exceededYear != 0) {
                totalAmount.setForeground(Color.RED);
                int exceededAmount = Math.max(monthlyTotal - monthlyLimit, yearlyTotal - yearlyLimit);
                totalAmount.setText(total + "€ (Exceeded by " + exceededAmount + "€ for "
                        + (exceededMonth != null ? exceededMonth + " " : "") + exceededYear + ")");
            } else {
                totalAmount.setForeground(Color.BLACK);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        d = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        a = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        expenseType = new javax.swing.JComboBox<>();
        category = new javax.swing.JComboBox<>();
        person = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        totalAmount = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jLabel7 = new javax.swing.JLabel();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SpendingTracker");

        jPanel1.setBackground(new java.awt.Color(102, 165, 173));

        jPanel2.setBackground(new java.awt.Color(30, 71, 92));

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Add New Expense");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addContainerGap()));
        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Amount:");

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Date:");

        a.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                aKeyTyped(evt);
            }
        });

        expenseType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Category", "Person" }));
        expenseType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expenseTypeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Expense Type:");

        person.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Person" }));

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Person:");

        jButton1.setText("Add New Category");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setText("Add New Person");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Add Recurring Expense");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openReccuringExpenseDialog();
            }
        });

        jButton7.setText("Set Limit");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSetLimitDialog();
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 0));
        jButton2.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jButton2.setText("ADD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setText("Refresh");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 46,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(d, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(d, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)

                                                .addComponent(jLabel2)

                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(a, javax.swing.GroupLayout.PREFERRED_SIZE, 78,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel4)
                                                .addComponent(expenseType, javax.swing.GroupLayout.PREFERRED_SIZE, 123,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))

                                        .addComponent(jButton4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addComponent(jButton7)

                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)

                                                .addComponent(jButton5)
                                                .addComponent(jButton6))

                                        .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 123,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)

                                        .addGroup(jPanel1Layout.createSequentialGroup()

                                                .addComponent(person, javax.swing.GroupLayout.PREFERRED_SIZE, 123,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jButton2)
                                .addContainerGap()));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel3)
                                                .addComponent(d, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(a, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(expenseType, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(1, 1, 1)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)

                                                        .addComponent(person, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)

                                                        .addComponent(jButton1)
                                                        .addComponent(jButton4)
                                                        .addComponent(jButton5)
                                                        .addComponent(jButton6)
                                                        .addComponent(jButton7))
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(d, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 11, Short.MAX_VALUE))));

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] { a, d, jLabel2 });

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel5.setText("Recent Spendings:");

        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {

                },
                new String[] {
                        "ID", expenseType.getSelectedItem().toString().equals("Category") ? "Category" : "Person",
                        "Date", "Amount"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        jButton3.setBackground(new java.awt.Color(255, 51, 51));
        jButton3.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jButton3.setText("Remove");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));

        totalAmount.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        totalAmount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalAmount.setText("0");

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel6.setText("Total Amount");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 101,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 403,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(128, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(totalAmount))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        jMenu1.setText("Main");

        jMenuItem1.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
                java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem1.setText("Filter By Date or Category");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
                java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem5.setText("Filter By Person");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenu2.setText("More");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenu2.add(jMenuItem4).setText("About Us");
        jMenu1.add(jMenu2);
        jMenuBar1.add(jMenu1);
        jMenu1.add(jSeparator1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem3.setText("Exit App");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 152,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton3)))
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jButton3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));

        pack();
        setLocationRelativeTo(null);
        populatePersonDropdown();
    }

    private void expenseTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if (expenseType.getSelectedItem().toString().equals("Category")) {
            category.setVisible(true);
            jLabel4.setVisible(true); // Assuming jLabel4 is the label for category
            person.setVisible(false);
            jLabel7.setVisible(false); // Assuming jLabel7 is the label for person
            table.getColumnModel().getColumn(1).setHeaderValue("Category");
        } else {
            category.setVisible(false);
            jLabel4.setVisible(false); // Assuming jLabel4 is the label for category
            person.setVisible(true);
            jLabel7.setVisible(true); // Assuming jLabel7 is the label for person
            table.getColumnModel().getColumn(1).setHeaderValue("Person");
        }
        table.getTableHeader().repaint();
        getEntries();
    }

    private void openReccuringExpenseDialog() {
        ReccuringExpense recurringExpense = new ReccuringExpense();
        recurringExpense.openReccuringExpenseDialog(this);
    }

    private void openSetLimitDialog() {
        SetLimit setLimit = new SetLimit();
        setLimit.openSetLimitDialog(this);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        new Category().setVisible(true);
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed
        displayOptions("Category");
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        String selectedExpenseType = expenseType.getSelectedItem().toString();
        String amountText = a.getText();
        int amountValue = Integer.parseInt(amountText);
        java.util.Date date = d.getDate();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        try {
            if (selectedExpenseType.equals("Category")) {
                String selectedCategory = category.getSelectedItem().toString();
                DbConnect.st.executeUpdate("INSERT INTO spendings (category, sdate, amount) VALUES ('"
                        + selectedCategory + "', '" + sqlDate + "', " + amountValue + ")");
                JOptionPane.showMessageDialog(null, "Amount added to spendings successfully!");
            } else if (selectedExpenseType.equals("Person")) {
                String selectedPerson = person.getSelectedItem().toString();
                DbConnect.st.executeUpdate("INSERT INTO person_spendings (person, pdate, amount) VALUES ('"
                        + selectedPerson + "', '" + sqlDate + "', " + amountValue + ")");
                JOptionPane.showMessageDialog(null, "Amount added to person_spendings successfully!");
            } else {
                JOptionPane.showMessageDialog(null,
                        "Please Fill all details!");
            }
            getEntries();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }// GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        new Person().setVisible(true);
    }

    public void populatePersonDropdown() {
        try {
            ResultSet rs = DbConnect.st.executeQuery("select * from person_info");
            while (rs.next()) {
                person.addItem(rs.getString("person"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void aKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_aKeyTyped
        char ch = evt.getKeyChar();
        if (!Character.isDigit(ch)) {
            evt.consume();
        }
    }// GEN-LAST:event_aKeyTyped

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        int ri = table.getSelectedRow();
        if (ri != -1) {
            int r = JOptionPane.showConfirmDialog(null,
                    "Do you really want to delete?", "Deletion Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                int id = (int) table.getValueAt(ri, 0);
                try {
                    DbConnect.st.executeUpdate(
                            "delete from spendings where sid=" + id);
                    JOptionPane.showMessageDialog(null,
                            "Successfully Deleted!");
                    getEntries();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
    }// GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem2ActionPerformed
        new Category().setVisible(true);
    }// GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem1ActionPerformed
        new ViewSpending().setVisible(true);
    }// GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem3ActionPerformed
        System.exit(0);
    }// GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItem4ActionPerformed
        JOptionPane.showMessageDialog(null,
                "Design & Develop BY TEAM RCB");
    }// GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {
        new filterByUser().setVisible(true);
    }

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SpendingTracker.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SpendingTracker.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SpendingTracker.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpendingTracker.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SpendingTracker().setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JTextField a;
    private javax.swing.JComboBox<String> expenseType;
    private javax.swing.JComboBox<String> category;
    private javax.swing.JComboBox<String> person;
    private com.toedter.calendar.JDateChooser d;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTable table;
    private javax.swing.JLabel totalAmount;

}
