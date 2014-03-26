/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.caitlin.stencilhelp.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.event.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class InterestCalculator2 extends StencilAppPanel implements ActionListener {
    JPanel panMain = new JPanel();
    BoxLayout boxLayout5;
    JPanel panTitle = new JPanel();
    JLabel jLabel1 = new JLabel();
    JPanel panWorkArea = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel panParameters = new JPanel();
    JPanel panOutput = new JPanel();
    BoxLayout boxLayout1;
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JPanel jPanel3 = new JPanel();
    JPanel jPanel4 = new JPanel();
    JPanel jPanel5 = new JPanel();
    JLabel jLabel2 = new JLabel();
    JTextField txtLoan = new JTextField();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JButton btnCalc = new JButton();
    FlowLayout flowLayout1 = new FlowLayout();
    FlowLayout flowLayout2 = new FlowLayout();
    FlowLayout flowLayout3 = new FlowLayout();
    FlowLayout flowLayout4 = new FlowLayout();
    BoxLayout boxLayout2;
    JPanel jPanel6 = new JPanel();
    JPanel jPanel7 = new JPanel();
    JLabel jLabel6 = new JLabel();
    FlowLayout flowLayout5 = new FlowLayout();
    JPanel jPanel8 = new JPanel();
    JLabel lblMonthly = new JLabel();
    FlowLayout flowLayout6 = new FlowLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JPanel panSchedule = new JPanel();
    BoxLayout boxLayout3;
    NumberFormat nf = NumberFormat.getCurrencyInstance();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenu jMenu2 = new JMenu();
    JMenu jMenu3 = new JMenu();
    JMenuItem jMenuItem1 = new JMenuItem();
    JMenuItem jMenuItem2 = new JMenuItem();
    JMenuItem jMenuItem3 = new JMenuItem();
    JMenuItem jMenuItem4 = new JMenuItem();
    JMenuItem jMenuItem5 = new JMenuItem();
    JMenu jMenu4 = new JMenu();
    JMenuItem jMenuItem6 = new JMenuItem();
    JSlider jSlider1 = new JSlider();
    JLabel lblRate = new JLabel();
    JComboBox cboLength = new JComboBox();
    FlowLayout flowLayout7 = new FlowLayout();

    JFrame frame = null;

    public InterestCalculator2(JFrame frame) {
    // added for show me
      super(frame);
        try {
          // added for show me
          this.frame = frame;
            jbInit();

          //added for show me
          addTableEntries();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        //InterestCalculator2 interestCalculator1 = new InterestCalculator2();
        //interestCalculator1.show();
        //interestCalculator1.pack();
        //interestCalculator1.setTitle("Mortgage Calculator");

        JFrame frame = new JFrame();
        InterestCalculator2 interestCalculator1 = new InterestCalculator2(frame);
        frame.getContentPane().add(interestCalculator1);
        frame.setTitle("Mortgage Calculator");

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
    private void jbInit() throws Exception {
        jLabel1.setForeground(java.awt.Color.black);
        jLabel2.setForeground(java.awt.Color.black);
        jLabel3.setForeground(java.awt.Color.black);
        jLabel4.setForeground(java.awt.Color.black);
        jLabel5.setForeground(java.awt.Color.black);
        jLabel6.setForeground(java.awt.Color.black);
        lblMonthly.setForeground(java.awt.Color.black);
        lblRate.setForeground(java.awt.Color.black);
        this.frame.setJMenuBar(jMenuBar1);
        panMain.setMaximumSize(new Dimension(640, 480));
        panMain.setMinimumSize(new Dimension(640, 480));
        panMain.setPreferredSize(new Dimension(640, 480));
        boxLayout5 = new BoxLayout(panMain,BoxLayout.Y_AXIS);
        panMain.setLayout(boxLayout5);
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24));
        jLabel1.setText("Simple Mortgage Calculator");
        panWorkArea.setLayout(borderLayout2);
        panParameters.setBorder(BorderFactory.createEtchedBorder());
        panParameters.setMinimumSize(new Dimension(260, 10));
        panParameters.setPreferredSize(new Dimension(260, 10));
        boxLayout1 = new BoxLayout(panParameters,BoxLayout.Y_AXIS);
        panParameters.setLayout(boxLayout1);
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Mortgage Terms");
        txtLoan.setMinimumSize(new Dimension(70, 21));
        txtLoan.setPreferredSize(new Dimension(70, 21));
        txtLoan.setText("100000");
        jLabel3.setText("Principal Loan Balance ($)");
        jLabel4.setText("Interest Rate");
        jLabel5.setText("Amortization Length");
        btnCalc.setText("Calculate Your Mortgage  ==)");
        btnCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnCalc_actionPerformed(e);
            }
        });
        jPanel1.setLayout(flowLayout1);
        jPanel5.setLayout(flowLayout2);
        jPanel3.setLayout(flowLayout3);
        jPanel4.setLayout(flowLayout4);
        flowLayout2.setAlignment(FlowLayout.LEFT);
        flowLayout4.setAlignment(FlowLayout.LEFT);
        flowLayout3.setAlignment(FlowLayout.LEFT);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        boxLayout2 = new BoxLayout(panOutput,BoxLayout.Y_AXIS);
        panOutput.setLayout(boxLayout2);
        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel6.setText("Payment Schedule");
        jPanel6.setLayout(flowLayout5);
        flowLayout5.setAlignment(FlowLayout.LEFT);
        jPanel1.setMaximumSize(new Dimension(32767, 50));
        jPanel1.setMinimumSize(new Dimension(104, 50));
        jPanel1.setPreferredSize(new Dimension(104, 50));
        jPanel6.setMaximumSize(new Dimension(32767, 50));
        jPanel6.setMinimumSize(new Dimension(104, 50));
        jPanel6.setPreferredSize(new Dimension(104, 50));
        lblMonthly.setFont(new java.awt.Font("Dialog", 1, 18));
        lblMonthly.setText("Your Monthly Payment:");
        jPanel8.setMaximumSize(new Dimension(32767, 27));
        jPanel8.setLayout(flowLayout6);
        flowLayout6.setAlignment(FlowLayout.LEFT);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setMaximumSize(new Dimension(600, 471));
        boxLayout3 = new BoxLayout(panSchedule,BoxLayout.Y_AXIS);
        panSchedule.setLayout(boxLayout3);
        panSchedule.setMaximumSize(new Dimension(600, 471));
        jMenu1.setText("File");
        jMenu2.setText("Edit");
        jMenu3.setText("Help");
        jMenuItem1.setText("Print");
        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem2_actionPerformed(e);
            }
        });
        jMenuItem3.setText("Copy");
        jMenuItem4.setText("Paste");
        jMenuItem5.setText("Cut");
        jMenu4.setText("ShowMe");
        jMenuItem6.setText("example");
        // added for show me....
        jMenuItem6.addActionListener(this);
        jMenuBar1.setAlignmentX((float) 0.0);
        jSlider1.setValue(24);
        jSlider1.setMaximum(48);
        jSlider1.setPreferredSize(new Dimension(100, 24));
        jSlider1.setMinimumSize(new Dimension(120, 24));
        jSlider1.setMaximumSize(new Dimension(120, 24));
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                jSlider1_stateChanged(e);
            }
        });
        lblRate.setMaximumSize(new Dimension(50, 17));
        lblRate.setMinimumSize(new Dimension(50, 17));
        lblRate.setPreferredSize(new Dimension(50, 17));
        lblRate.setText("6.0 %");
        panOutput.setBorder(BorderFactory.createEtchedBorder());
        panTitle.setLayout(flowLayout7);
        flowLayout7.setAlignment(FlowLayout.LEFT);
        //this.getContentPane().add(panMain, BorderLayout.CENTER);
        this.add(panMain, BorderLayout.CENTER);
        panMain.add(panTitle);
        panTitle.add(jLabel1, null);
        panMain.add(panWorkArea);
        panWorkArea.add(panParameters, BorderLayout.WEST);
        panParameters.add(jPanel1, null);
        jPanel1.add(jLabel2, null);
        panParameters.add(jPanel5, null);
        jPanel5.add(jLabel3, null);
        jPanel5.add(txtLoan, null);
        panParameters.add(jPanel4, null);
        jPanel4.add(jLabel4, null);
        jPanel4.add(jSlider1, null);
        jPanel4.add(lblRate, null);
        panParameters.add(jPanel3, null);
        jPanel3.add(jLabel5, null);
        jPanel3.add(cboLength, null);
        panParameters.add(jPanel2, null);
        jPanel2.add(btnCalc, null);
        panWorkArea.add(panOutput, BorderLayout.CENTER);
        panOutput.add(jPanel6, null);
        jPanel6.add(jLabel6, null);
        panOutput.add(jPanel8, null);
        jPanel8.add(lblMonthly, null);
        panOutput.add(jScrollPane1, null);
        //jPanel7.add(jScrollPane1, null);
        jScrollPane1.getViewport().add(panSchedule, null);
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenuBar1.add(jMenu3);
        jMenu1.add(jMenuItem1);
        jMenu1.add(jMenuItem2);
        jMenu2.add(jMenuItem5);
        jMenu2.add(jMenuItem3);
        jMenu2.add(jMenuItem4);
        jMenu3.add(jMenu4);
        jMenu4.add(jMenuItem6);
        cboLength.setLightWeightPopupEnabled(false);
        cboLength.addItem( "10 years" );
        cboLength.addItem( "15 years" );
        cboLength.addItem( "20 years" );
        cboLength.addItem( "30 years" );
        cboLength.setSelectedIndex(3);
    }

    void btnCalc_actionPerformed(ActionEvent e) {
        int n = 12;
        String rateString = lblRate.getText().substring( 0, lblRate.getText().length()-2 );
        double r = Double.parseDouble( rateString )/100d;
        double M = Double.parseDouble( txtLoan.getText() );
        int t;
        if(cboLength.getSelectedIndex() == 0){
            t = 10;
        }else if(cboLength.getSelectedIndex() == 1){
            t = 15;
        }else if(cboLength.getSelectedIndex() == 2){
            t = 20;
        }else{
            t = 30;
        }
        double payment = ( (r*M)/(  1-Math.pow((1+(r/n)),(-1*n*t))  ) )/( n );
        this.lblMonthly.setText( "Your Monthly Payment: " +  nf.format(payment) );
        panSchedule.removeAll();
        panSchedule.repaint();
        String tString;
        int year = 2002;
        double interest = 0;
        double principal = 0;
        for(int i = 0; i< t ;i++){
            interest = 0;
            principal = 0;
            double totalPayed = payment * n;
            double monthlyInterest = 0;
            for(int j=0;j<n;j++){
                monthlyInterest = 0;
                monthlyInterest += M*r/(double)n;
                M -= (payment - monthlyInterest);
                principal += payment - monthlyInterest;
                interest += monthlyInterest;
            }
            tString =   "For " +
                        String.valueOf(year) +
                        " : Int= " +
                        nf.format(interest) +
                        " : Prin= " +
                        nf.format(principal) +
                        " : Bal= " +
                        nf.format(M);
            JLabel newLabel = new JLabel(tString);
            newLabel.setForeground(java.awt.Color.black);
            panSchedule.add( newLabel );
            year += 1;
        }
        jScrollPane1.revalidate();

        //show me junk... these are dynamic so they need to be updated after the
        //display has been regenerated
        JScrollBar vertBar = jScrollPane1.getVerticalScrollBar();
        JScrollBar horBar = jScrollPane1.getHorizontalScrollBar();
        this.replaceTable("vertBar", vertBar);
        this.replaceTable("horBar", horBar);
    }




    void jSlider1_stateChanged(ChangeEvent e) {
        lblRate.setText( String.valueOf(jSlider1.getValue()*.25) + " %" );
    }

    void jMenuItem2_actionPerformed(ActionEvent e) {
        System.exit(10);
    }


    // More Show Me junk....
    public void actionPerformed( ActionEvent ae ) {
        //System.out.println("start show me");
        this.launchControl();
    }

    public void addTableEntries() {
      this.addToTable("cbpLength", cboLength);
      System.out.println("combo box: " + cboLength);
      this.addToTable("textLoan", txtLoan);
      this.addToTable("sliderRate", jSlider1);
      this.addToTable("btnCalc", btnCalc);
      this.addToTable("lblMonthly", lblMonthly);
    }

     public void deFocus() {
      jLabel1.requestFocus();
    }

}