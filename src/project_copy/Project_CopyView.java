/*
 * Project_CopyView.java
 */
package project_copy;

import db_controls.AddonsControl;
import db_controls.BoardControl;
import db_controls.EnumControl;
import db_controls.IssueControl;
import db_controls.RepositoryControl;
import table_beans.Projects;
import table_beans.Roles;
import table_beans.Statuses;
import table_beans.Trackers;
import table_beans.Users;
import java.awt.Dimension;
import java.sql.DatabaseMetaData;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import org.apache.log4j.Logger;
import table_beans.RedmineEnum;

/**
 * The application's main frame.
 */
public class Project_CopyView extends FrameView {

    private ConnectToDb con = null;
    private ConnectToDb s_con = null;
    private ConnectToDb t_con = null;
    private PropDB pdb = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String s_db = "";
    private String t_db = "";
    private String mysql_db = "mysql";
    private String project = "";
    private IdChanges idch;
    private Users usr;
    private Roles rls;
    private Projects prj;
    private Trackers trk;
    private Statuses status;
    private static Logger logger = Logger.getLogger(Project_CopyView.class);
    private DefaultListModel globalSrcUsers;
    private DefaultListModel globalTrgUsers;

    public Project_CopyView(SingleFrameApplication app) {
        super(app);

        try {
            idch = IdChanges.getInstance();
        } catch (Exception e) {
            logger.error("in Project_CopyView", e);
            //e.printStackTrace();
        }

        initComponents();


        globalSrcUsers = new DefaultListModel();
        globalTrgUsers = new DefaultListModel();

        logger.info("Started!");



        B_DbApply.setEnabled(false);
        B_StartPageNext.setEnabled(false);
        B_loadRoles.setEnabled(false);
        B_loadTrackers.setEnabled(false);
        B_loadUsers.setEnabled(false);
        B_rewriteRoles.setEnabled(false);
        B_rewriteTrackers.setEnabled(false);
        B_rewriteUsers.setEnabled(false);
        B_rewriteStatuses.setEnabled(false);
        B_loadStatuses.setEnabled(false);
        B_rewriteEnum.setEnabled(false);
        B_loadEnums.setEnabled(false);
        UsersFrame.setPreferredSize(new Dimension(850, 700));
        UsersFrame.setMinimumSize(new Dimension(850, 700));

        ProjectsFrame.setPreferredSize(new Dimension(850, 700));
        ProjectsFrame.setMinimumSize(new Dimension(850, 700));


        RolesFrame.setPreferredSize(new Dimension(850, 700));
        RolesFrame.setMinimumSize(new Dimension(850, 700));

        TrackersFrame.setPreferredSize(new Dimension(850, 700));
        TrackersFrame.setMinimumSize(new Dimension(850, 700));

        StatusesFrame.setPreferredSize(new Dimension(850, 700));
        StatusesFrame.setMinimumSize(new Dimension(850, 700));



        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(true);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = Project_CopyApp.getApplication().getMainFrame();
            aboutBox = new Project_CopyAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        Project_CopyApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        B_DbConnect = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        B_StartPageNext = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        B_DbApply = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        UsersCheck = new javax.swing.JCheckBox();
        B_rewriteUsers = new javax.swing.JButton();
        B_loadUsers = new javax.swing.JButton();
        RolesCheck = new javax.swing.JCheckBox();
        B_rewriteRoles = new javax.swing.JButton();
        B_loadRoles = new javax.swing.JButton();
        TrackersCheck = new javax.swing.JCheckBox();
        B_rewriteTrackers = new javax.swing.JButton();
        B_loadTrackers = new javax.swing.JButton();
        StatusesCheck = new javax.swing.JCheckBox();
        B_rewriteStatuses = new javax.swing.JButton();
        B_loadStatuses = new javax.swing.JButton();
        EnumCheck = new javax.swing.JCheckBox();
        B_rewriteEnum = new javax.swing.JButton();
        B_loadEnums = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        UsersFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        SourceUsers = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        TA_SourceUserInfo = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        TA_TargetUserInfo = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        TargetUsers = new javax.swing.JList();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        TA_UsersRelations = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        B_UserIgnore = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ProjectsFrame = new javax.swing.JFrame();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        TA_prjSourceInfo = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        TA_prjTargetInfo = new javax.swing.JTextArea();
        B_prjApply = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        RolesFrame = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        SourceRoles = new javax.swing.JList();
        jScrollPane9 = new javax.swing.JScrollPane();
        TA_SourceRolesInfo = new javax.swing.JTextArea();
        jScrollPane10 = new javax.swing.JScrollPane();
        TA_TargetRolesInfo = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        TargetRoles = new javax.swing.JList();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        B_RoleRelatedWithTarget = new javax.swing.JButton();
        B_RoleAsNew = new javax.swing.JButton();
        B_RoleApply = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        TA_RolesRelations = new javax.swing.JTextArea();
        jLabel19 = new javax.swing.JLabel();
        B_RoleIgnore = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        TrackersFrame = new javax.swing.JFrame();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        SourceTrackers = new javax.swing.JList();
        jScrollPane14 = new javax.swing.JScrollPane();
        TA_SourceTrackersInfo = new javax.swing.JTextArea();
        jScrollPane15 = new javax.swing.JScrollPane();
        TA_TargetTrackersInfo = new javax.swing.JTextArea();
        jScrollPane16 = new javax.swing.JScrollPane();
        TargetTrackers = new javax.swing.JList();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        B_TrackersRelatedWithTarget = new javax.swing.JButton();
        B_TrackersAsNew = new javax.swing.JButton();
        B_TrackersApply = new javax.swing.JButton();
        jScrollPane17 = new javax.swing.JScrollPane();
        TA_TrackersRelations = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        B_TrackersIgnore = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        StatusesFrame = new javax.swing.JFrame();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        SourceStatuses = new javax.swing.JList();
        jScrollPane19 = new javax.swing.JScrollPane();
        TA_SourceStatusesInfo = new javax.swing.JTextArea();
        jScrollPane20 = new javax.swing.JScrollPane();
        TA_TargetStatusesInfo = new javax.swing.JTextArea();
        jScrollPane21 = new javax.swing.JScrollPane();
        TargetStatuses = new javax.swing.JList();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        B_StatusRelatedWithTarget = new javax.swing.JButton();
        B_StatusAsNew = new javax.swing.JButton();
        B_StatusApply = new javax.swing.JButton();
        jScrollPane22 = new javax.swing.JScrollPane();
        TA_StatusesRelations = new javax.swing.JTextArea();
        jLabel31 = new javax.swing.JLabel();
        B_StatusIgnore = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        EnumFrame = new javax.swing.JFrame();
        jPanel13 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        B_EnumDocNew = new javax.swing.JButton();
        B_EnumRelatDoc = new javax.swing.JButton();
        B_EnumIgnoretDoc = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        B_EnumPriorNew = new javax.swing.JButton();
        B_EnumRelatPrior = new javax.swing.JButton();
        B_EnumIgnoretPrior = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        B_EnumIgnoretAct = new javax.swing.JButton();
        B_EnumRelatAct = new javax.swing.JButton();
        B_EnumActNew = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        TA_EnumsIssues = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane24 = new javax.swing.JScrollPane();
        TA_SEnumDoc = new javax.swing.JTextArea();
        jScrollPane25 = new javax.swing.JScrollPane();
        L_SEnumDoc = new javax.swing.JList();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane26 = new javax.swing.JScrollPane();
        TA_SEnumPrior = new javax.swing.JTextArea();
        jScrollPane27 = new javax.swing.JScrollPane();
        L_SEnumPrior = new javax.swing.JList();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane28 = new javax.swing.JScrollPane();
        TA_SEnumAct = new javax.swing.JTextArea();
        jScrollPane29 = new javax.swing.JScrollPane();
        L_SEnumAct = new javax.swing.JList();
        jPanel16 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jScrollPane30 = new javax.swing.JScrollPane();
        L_TEnumDoc = new javax.swing.JList();
        jScrollPane31 = new javax.swing.JScrollPane();
        TA_TEnumDoc = new javax.swing.JTextArea();
        jPanel27 = new javax.swing.JPanel();
        jScrollPane32 = new javax.swing.JScrollPane();
        TA_TEnumPrior = new javax.swing.JTextArea();
        jScrollPane33 = new javax.swing.JScrollPane();
        L_TEnumPrior = new javax.swing.JList();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane34 = new javax.swing.JScrollPane();
        L_TEnumAct = new javax.swing.JList();
        jScrollPane35 = new javax.swing.JScrollPane();
        TA_TEnumAct = new javax.swing.JTextArea();
        B_ApplyEnum = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();

        mainPanel.setName("mainPanel"); // NOI18N

        jPanel1.setAutoscrolls(true);
        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(project_copy.Project_CopyApp.class).getContext().getResourceMap(Project_CopyView.class);
        B_DbConnect.setText(resourceMap.getString("B_DbConnect.text")); // NOI18N
        B_DbConnect.setName("B_DbConnect"); // NOI18N
        B_DbConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_DbConnectActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel1.setForeground(resourceMap.getColor("jLabel1.foreground")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jComboBox1.setName("jComboBox1"); // NOI18N

        B_StartPageNext.setText(resourceMap.getString("B_StartPageNext.text")); // NOI18N
        B_StartPageNext.setName("B_StartPageNext"); // NOI18N
        B_StartPageNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_StartPageNextActionPerformed(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jComboBox2.setSelectedItem(0);
        jComboBox2.setName("jComboBox2"); // NOI18N

        jComboBox3.setSelectedItem(0);
        jComboBox3.setName("jComboBox3"); // NOI18N

        B_DbApply.setText(resourceMap.getString("B_DbApply.text")); // NOI18N
        B_DbApply.setName("B_DbApply"); // NOI18N
        B_DbApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_DbApplyActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setName("jPanel6"); // NOI18N

        UsersCheck.setBackground(resourceMap.getColor("UsersCheck.background")); // NOI18N
        UsersCheck.setFont(resourceMap.getFont("UsersCheck.font")); // NOI18N
        UsersCheck.setForeground(resourceMap.getColor("UsersCheck.foreground")); // NOI18N
        UsersCheck.setText(resourceMap.getString("UsersCheck.text")); // NOI18N
        UsersCheck.setFocusable(false);
        UsersCheck.setName("UsersCheck"); // NOI18N
        UsersCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsersCheckActionPerformed(evt);
            }
        });

        B_rewriteUsers.setText(resourceMap.getString("B_rewriteUsers.text")); // NOI18N
        B_rewriteUsers.setName("B_rewriteUsers"); // NOI18N
        B_rewriteUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_rewriteUsersActionPerformed(evt);
            }
        });

        B_loadUsers.setText(resourceMap.getString("B_loadUsers.text")); // NOI18N
        B_loadUsers.setName("B_loadUsers"); // NOI18N
        B_loadUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_loadUsersActionPerformed(evt);
            }
        });

        RolesCheck.setFont(resourceMap.getFont("RolesCheck.font")); // NOI18N
        RolesCheck.setText(resourceMap.getString("RolesCheck.text")); // NOI18N
        RolesCheck.setFocusable(false);
        RolesCheck.setName("RolesCheck"); // NOI18N
        RolesCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RolesCheckItemStateChanged(evt);
            }
        });

        B_rewriteRoles.setText(resourceMap.getString("B_rewriteRoles.text")); // NOI18N
        B_rewriteRoles.setName("B_rewriteRoles"); // NOI18N
        B_rewriteRoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_rewriteRolesActionPerformed(evt);
            }
        });

        B_loadRoles.setText(resourceMap.getString("B_loadRoles.text")); // NOI18N
        B_loadRoles.setName("B_loadRoles"); // NOI18N
        B_loadRoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_loadRolesActionPerformed(evt);
            }
        });

        TrackersCheck.setFont(resourceMap.getFont("TrackersCheck.font")); // NOI18N
        TrackersCheck.setText(resourceMap.getString("TrackersCheck.text")); // NOI18N
        TrackersCheck.setFocusable(false);
        TrackersCheck.setName("TrackersCheck"); // NOI18N
        TrackersCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TrackersCheckItemStateChanged(evt);
            }
        });

        B_rewriteTrackers.setText(resourceMap.getString("B_rewriteTrackers.text")); // NOI18N
        B_rewriteTrackers.setName("B_rewriteTrackers"); // NOI18N
        B_rewriteTrackers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_rewriteTrackersActionPerformed(evt);
            }
        });

        B_loadTrackers.setText(resourceMap.getString("B_loadTrackers.text")); // NOI18N
        B_loadTrackers.setName("B_loadTrackers"); // NOI18N
        B_loadTrackers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_loadTrackersActionPerformed(evt);
            }
        });

        StatusesCheck.setFont(resourceMap.getFont("StatusesCheck.font")); // NOI18N
        StatusesCheck.setText(resourceMap.getString("StatusesCheck.text")); // NOI18N
        StatusesCheck.setFocusable(false);
        StatusesCheck.setName("StatusesCheck"); // NOI18N
        StatusesCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                StatusesCheckItemStateChanged(evt);
            }
        });

        B_rewriteStatuses.setText(resourceMap.getString("B_rewriteStatuses.text")); // NOI18N
        B_rewriteStatuses.setName("B_rewriteStatuses"); // NOI18N
        B_rewriteStatuses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_rewriteStatusesActionPerformed(evt);
            }
        });

        B_loadStatuses.setText(resourceMap.getString("B_loadStatuses.text")); // NOI18N
        B_loadStatuses.setName("B_loadStatuses"); // NOI18N
        B_loadStatuses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_loadStatusesActionPerformed(evt);
            }
        });

        EnumCheck.setFont(resourceMap.getFont("EnumCheck.font")); // NOI18N
        EnumCheck.setText(resourceMap.getString("EnumCheck.text")); // NOI18N
        EnumCheck.setFocusable(false);
        EnumCheck.setName("EnumCheck"); // NOI18N
        EnumCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                EnumCheckItemStateChanged(evt);
            }
        });

        B_rewriteEnum.setText(resourceMap.getString("B_rewriteEnum.text")); // NOI18N
        B_rewriteEnum.setName("B_rewriteEnum"); // NOI18N
        B_rewriteEnum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_rewriteEnumActionPerformed(evt);
            }
        });

        B_loadEnums.setText(resourceMap.getString("B_loadEnums.text")); // NOI18N
        B_loadEnums.setName("B_loadEnums"); // NOI18N
        B_loadEnums.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_loadEnumsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(UsersCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
                        .addComponent(B_rewriteUsers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_loadUsers))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(RolesCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
                        .addComponent(B_rewriteRoles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_loadRoles))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(TrackersCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                        .addComponent(B_rewriteTrackers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_loadTrackers))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(StatusesCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                        .addComponent(B_rewriteStatuses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_loadStatuses))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(EnumCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                        .addComponent(B_rewriteEnum)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_loadEnums)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UsersCheck)
                    .addComponent(B_loadUsers)
                    .addComponent(B_rewriteUsers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RolesCheck)
                    .addComponent(B_loadRoles)
                    .addComponent(B_rewriteRoles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TrackersCheck)
                    .addComponent(B_loadTrackers)
                    .addComponent(B_rewriteTrackers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StatusesCheck)
                    .addComponent(B_loadStatuses)
                    .addComponent(B_rewriteStatuses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EnumCheck)
                    .addComponent(B_loadEnums)
                    .addComponent(B_rewriteEnum))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(B_DbConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 415, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, 0, 690, Short.MAX_VALUE)
                            .addComponent(B_DbApply, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox3, 0, 562, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, 0, 562, Short.MAX_VALUE)))
                            .addComponent(jLabel2)))
                    .addComponent(B_StartPageNext, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_DbConnect)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(B_DbApply)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(B_StartPageNext)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(project_copy.Project_CopyApp.class).getContext().getActionMap(Project_CopyView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 554, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        UsersFrame.setName("UsersFrame"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        SourceUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        SourceUsers.setName("SourceUsers"); // NOI18N
        SourceUsers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                SourceUsersValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(SourceUsers);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        TA_SourceUserInfo.setColumns(20);
        TA_SourceUserInfo.setEditable(false);
        TA_SourceUserInfo.setRows(5);
        TA_SourceUserInfo.setName("TA_SourceUserInfo"); // NOI18N
        jScrollPane2.setViewportView(TA_SourceUserInfo);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        TA_TargetUserInfo.setColumns(20);
        TA_TargetUserInfo.setEditable(false);
        TA_TargetUserInfo.setRows(5);
        TA_TargetUserInfo.setName("TA_TargetUserInfo"); // NOI18N
        jScrollPane3.setViewportView(TA_TargetUserInfo);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        TargetUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TargetUsers.setName("TargetUsers"); // NOI18N
        TargetUsers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                TargetUsersValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(TargetUsers);

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        TA_UsersRelations.setColumns(20);
        TA_UsersRelations.setEditable(false);
        TA_UsersRelations.setFont(resourceMap.getFont("TA_UsersRelations.font")); // NOI18N
        TA_UsersRelations.setRows(5);
        TA_UsersRelations.setName("TA_UsersRelations"); // NOI18N
        jScrollPane5.setViewportView(TA_UsersRelations);

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        B_UserIgnore.setText(resourceMap.getString("B_UserIgnore.text")); // NOI18N
        B_UserIgnore.setName("B_UserIgnore"); // NOI18N
        B_UserIgnore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_UserIgnoreActionPerformed(evt);
            }
        });

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField1MousePressed(evt);
            }
        });
        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField1CaretUpdate(evt);
            }
        });

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N
        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextField2MouseReleased(evt);
            }
        });
        jTextField2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField2CaretUpdate(evt);
            }
        });
        jTextField2.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                jTextField2CaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField2InputMethodTextChanged(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                            .addComponent(B_UserIgnore, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                            .addComponent(jLabel16))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(26, 26, 26))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 478, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_UserIgnore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(jLabel16)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addGap(12, 12, 12))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(79, 79, 79)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UsersFrameLayout = new javax.swing.GroupLayout(UsersFrame.getContentPane());
        UsersFrame.getContentPane().setLayout(UsersFrameLayout);
        UsersFrameLayout.setHorizontalGroup(
            UsersFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UsersFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(UsersFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        UsersFrameLayout.setVerticalGroup(
            UsersFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UsersFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        ProjectsFrame.setName("ProjectsFrame"); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        TA_prjSourceInfo.setColumns(20);
        TA_prjSourceInfo.setEditable(false);
        TA_prjSourceInfo.setFont(resourceMap.getFont("TA_prjSourceInfo.font")); // NOI18N
        TA_prjSourceInfo.setRows(5);
        TA_prjSourceInfo.setName("TA_prjSourceInfo"); // NOI18N
        jScrollPane6.setViewportView(TA_prjSourceInfo);

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        TA_prjTargetInfo.setColumns(20);
        TA_prjTargetInfo.setEditable(false);
        TA_prjTargetInfo.setFont(resourceMap.getFont("TA_prjTargetInfo.font")); // NOI18N
        TA_prjTargetInfo.setRows(5);
        TA_prjTargetInfo.setName("TA_prjTargetInfo"); // NOI18N
        jScrollPane7.setViewportView(TA_prjTargetInfo);

        B_prjApply.setText(resourceMap.getString("B_prjApply.text")); // NOI18N
        B_prjApply.setName("B_prjApply"); // NOI18N
        B_prjApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_prjApplyActionPerformed(evt);
            }
        });

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(B_prjApply, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(273, 273, 273)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_prjApply)
                .addGap(12, 12, 12))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setForeground(resourceMap.getColor("jLabel14.foreground")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addGap(79, 79, 79)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ProjectsFrameLayout = new javax.swing.GroupLayout(ProjectsFrame.getContentPane());
        ProjectsFrame.getContentPane().setLayout(ProjectsFrameLayout);
        ProjectsFrameLayout.setHorizontalGroup(
            ProjectsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ProjectsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ProjectsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ProjectsFrameLayout.setVerticalGroup(
            ProjectsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProjectsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        RolesFrame.setName("RolesFrame"); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.setName("jPanel7"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        SourceRoles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        SourceRoles.setName("SourceRoles"); // NOI18N
        SourceRoles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                SourceRolesValueChanged(evt);
            }
        });
        jScrollPane8.setViewportView(SourceRoles);

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        TA_SourceRolesInfo.setColumns(20);
        TA_SourceRolesInfo.setEditable(false);
        TA_SourceRolesInfo.setRows(5);
        TA_SourceRolesInfo.setName("TA_SourceRolesInfo"); // NOI18N
        jScrollPane9.setViewportView(TA_SourceRolesInfo);

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        TA_TargetRolesInfo.setColumns(20);
        TA_TargetRolesInfo.setEditable(false);
        TA_TargetRolesInfo.setRows(5);
        TA_TargetRolesInfo.setName("TA_TargetRolesInfo"); // NOI18N
        jScrollPane10.setViewportView(TA_TargetRolesInfo);

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        TargetRoles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TargetRoles.setName("TargetRoles"); // NOI18N
        TargetRoles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                TargetRolesValueChanged(evt);
            }
        });
        jScrollPane11.setViewportView(TargetRoles);

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        B_RoleRelatedWithTarget.setText(resourceMap.getString("B_RoleRelatedWithTarget.text")); // NOI18N
        B_RoleRelatedWithTarget.setName("B_RoleRelatedWithTarget"); // NOI18N
        B_RoleRelatedWithTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_RoleRelatedWithTargetActionPerformed(evt);
            }
        });

        B_RoleAsNew.setText(resourceMap.getString("B_RoleAsNew.text")); // NOI18N
        B_RoleAsNew.setName("B_RoleAsNew"); // NOI18N
        B_RoleAsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_RoleAsNewActionPerformed(evt);
            }
        });

        B_RoleApply.setText(resourceMap.getString("B_RoleApply.text")); // NOI18N
        B_RoleApply.setName("B_RoleApply"); // NOI18N
        B_RoleApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_RoleApplyActionPerformed(evt);
            }
        });

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        TA_RolesRelations.setColumns(20);
        TA_RolesRelations.setEditable(false);
        TA_RolesRelations.setFont(resourceMap.getFont("TA_RolesRelations.font")); // NOI18N
        TA_RolesRelations.setRows(5);
        TA_RolesRelations.setName("TA_RolesRelations"); // NOI18N
        jScrollPane12.setViewportView(TA_RolesRelations);

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        B_RoleIgnore.setText(resourceMap.getString("B_RoleIgnore.text")); // NOI18N
        B_RoleIgnore.setName("B_RoleIgnore"); // NOI18N
        B_RoleIgnore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_RoleIgnoreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_RoleIgnore, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_RoleRelatedWithTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_RoleAsNew, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(B_RoleApply, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(26, 26, 26))
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 478, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(B_RoleAsNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_RoleRelatedWithTarget)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_RoleIgnore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jLabel19))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_RoleApply)
                .addGap(12, 12, 12))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setName("jPanel8"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setForeground(resourceMap.getColor("jLabel21.foreground")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addGap(79, 79, 79)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout RolesFrameLayout = new javax.swing.GroupLayout(RolesFrame.getContentPane());
        RolesFrame.getContentPane().setLayout(RolesFrameLayout);
        RolesFrameLayout.setHorizontalGroup(
            RolesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RolesFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(RolesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        RolesFrameLayout.setVerticalGroup(
            RolesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RolesFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        TrackersFrame.setName("TrackersFrame"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.setName("jPanel9"); // NOI18N

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        SourceTrackers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        SourceTrackers.setName("SourceTrackers"); // NOI18N
        SourceTrackers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                SourceTrackersValueChanged(evt);
            }
        });
        jScrollPane13.setViewportView(SourceTrackers);

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        TA_SourceTrackersInfo.setColumns(20);
        TA_SourceTrackersInfo.setEditable(false);
        TA_SourceTrackersInfo.setRows(5);
        TA_SourceTrackersInfo.setName("TA_SourceTrackersInfo"); // NOI18N
        jScrollPane14.setViewportView(TA_SourceTrackersInfo);

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        TA_TargetTrackersInfo.setColumns(20);
        TA_TargetTrackersInfo.setEditable(false);
        TA_TargetTrackersInfo.setRows(5);
        TA_TargetTrackersInfo.setName("TA_TargetTrackersInfo"); // NOI18N
        jScrollPane15.setViewportView(TA_TargetTrackersInfo);

        jScrollPane16.setName("jScrollPane16"); // NOI18N

        TargetTrackers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TargetTrackers.setName("TargetTrackers"); // NOI18N
        TargetTrackers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                TargetTrackersValueChanged(evt);
            }
        });
        jScrollPane16.setViewportView(TargetTrackers);

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        B_TrackersRelatedWithTarget.setText(resourceMap.getString("B_TrackersRelatedWithTarget.text")); // NOI18N
        B_TrackersRelatedWithTarget.setName("B_TrackersRelatedWithTarget"); // NOI18N
        B_TrackersRelatedWithTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_TrackersRelatedWithTargetActionPerformed(evt);
            }
        });

        B_TrackersAsNew.setText(resourceMap.getString("B_TrackersAsNew.text")); // NOI18N
        B_TrackersAsNew.setName("B_TrackersAsNew"); // NOI18N
        B_TrackersAsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_TrackersAsNewActionPerformed(evt);
            }
        });

        B_TrackersApply.setText(resourceMap.getString("B_TrackersApply.text")); // NOI18N
        B_TrackersApply.setName("B_TrackersApply"); // NOI18N
        B_TrackersApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_TrackersApplyActionPerformed(evt);
            }
        });

        jScrollPane17.setName("jScrollPane17"); // NOI18N

        TA_TrackersRelations.setColumns(20);
        TA_TrackersRelations.setEditable(false);
        TA_TrackersRelations.setFont(resourceMap.getFont("TA_TrackersRelations.font")); // NOI18N
        TA_TrackersRelations.setRows(5);
        TA_TrackersRelations.setName("TA_TrackersRelations"); // NOI18N
        jScrollPane17.setViewportView(TA_TrackersRelations);

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        B_TrackersIgnore.setText(resourceMap.getString("B_TrackersIgnore.text")); // NOI18N
        B_TrackersIgnore.setName("B_TrackersIgnore"); // NOI18N
        B_TrackersIgnore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_TrackersIgnoreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_TrackersIgnore, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_TrackersRelatedWithTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_TrackersAsNew, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(B_TrackersApply, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(26, 26, 26))
                            .addComponent(jScrollPane15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 478, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(B_TrackersAsNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_TrackersRelatedWithTarget)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_TrackersIgnore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jLabel25))
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_TrackersApply)
                .addGap(12, 12, 12))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel10.setName("jPanel10"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jLabel27.setForeground(resourceMap.getColor("jLabel27.foreground")); // NOI18N
        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        jLabel28.setFont(resourceMap.getFont("jLabel28.font")); // NOI18N
        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addGap(79, 79, 79)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout TrackersFrameLayout = new javax.swing.GroupLayout(TrackersFrame.getContentPane());
        TrackersFrame.getContentPane().setLayout(TrackersFrameLayout);
        TrackersFrameLayout.setHorizontalGroup(
            TrackersFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TrackersFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TrackersFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        TrackersFrameLayout.setVerticalGroup(
            TrackersFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TrackersFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        StatusesFrame.setName("StatusesFrame"); // NOI18N

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel11.setName("jPanel11"); // NOI18N

        jScrollPane18.setName("jScrollPane18"); // NOI18N

        SourceStatuses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        SourceStatuses.setName("SourceStatuses"); // NOI18N
        SourceStatuses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                SourceStatusesValueChanged(evt);
            }
        });
        jScrollPane18.setViewportView(SourceStatuses);

        jScrollPane19.setName("jScrollPane19"); // NOI18N

        TA_SourceStatusesInfo.setColumns(20);
        TA_SourceStatusesInfo.setEditable(false);
        TA_SourceStatusesInfo.setRows(5);
        TA_SourceStatusesInfo.setName("TA_SourceStatusesInfo"); // NOI18N
        jScrollPane19.setViewportView(TA_SourceStatusesInfo);

        jScrollPane20.setName("jScrollPane20"); // NOI18N

        TA_TargetStatusesInfo.setColumns(20);
        TA_TargetStatusesInfo.setEditable(false);
        TA_TargetStatusesInfo.setRows(5);
        TA_TargetStatusesInfo.setName("TA_TargetStatusesInfo"); // NOI18N
        jScrollPane20.setViewportView(TA_TargetStatusesInfo);

        jScrollPane21.setName("jScrollPane21"); // NOI18N

        TargetStatuses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TargetStatuses.setName("TargetStatuses"); // NOI18N
        TargetStatuses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                TargetStatusesValueChanged(evt);
            }
        });
        jScrollPane21.setViewportView(TargetStatuses);

        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N

        B_StatusRelatedWithTarget.setText(resourceMap.getString("B_StatusRelatedWithTarget.text")); // NOI18N
        B_StatusRelatedWithTarget.setName("B_StatusRelatedWithTarget"); // NOI18N
        B_StatusRelatedWithTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_StatusRelatedWithTargetActionPerformed(evt);
            }
        });

        B_StatusAsNew.setText(resourceMap.getString("B_StatusAsNew.text")); // NOI18N
        B_StatusAsNew.setName("B_StatusAsNew"); // NOI18N
        B_StatusAsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_StatusAsNewActionPerformed(evt);
            }
        });

        B_StatusApply.setText(resourceMap.getString("B_StatusApply.text")); // NOI18N
        B_StatusApply.setName("B_StatusApply"); // NOI18N
        B_StatusApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_StatusApplyActionPerformed(evt);
            }
        });

        jScrollPane22.setName("jScrollPane22"); // NOI18N

        TA_StatusesRelations.setColumns(20);
        TA_StatusesRelations.setEditable(false);
        TA_StatusesRelations.setFont(resourceMap.getFont("TA_StatusesRelations.font")); // NOI18N
        TA_StatusesRelations.setRows(5);
        TA_StatusesRelations.setName("TA_StatusesRelations"); // NOI18N
        jScrollPane22.setViewportView(TA_StatusesRelations);

        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N

        B_StatusIgnore.setText(resourceMap.getString("B_StatusIgnore.text")); // NOI18N
        B_StatusIgnore.setName("B_StatusIgnore"); // NOI18N
        B_StatusIgnore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_StatusIgnoreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_StatusIgnore, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_StatusRelatedWithTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(B_StatusAsNew, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(B_StatusApply, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addGap(26, 26, 26))
                            .addComponent(jScrollPane20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 478, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(B_StatusAsNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_StatusRelatedWithTarget)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_StatusIgnore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jLabel31))
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(jScrollPane22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_StatusApply)
                .addGap(12, 12, 12))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel12.setName("jPanel12"); // NOI18N

        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        jLabel33.setForeground(resourceMap.getColor("jLabel33.foreground")); // NOI18N
        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        jLabel34.setFont(resourceMap.getFont("jLabel34.font")); // NOI18N
        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34)
                .addGap(79, 79, 79)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout StatusesFrameLayout = new javax.swing.GroupLayout(StatusesFrame.getContentPane());
        StatusesFrame.getContentPane().setLayout(StatusesFrameLayout);
        StatusesFrameLayout.setHorizontalGroup(
            StatusesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, StatusesFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(StatusesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        StatusesFrameLayout.setVerticalGroup(
            StatusesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatusesFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        EnumFrame.setMinimumSize(new java.awt.Dimension(1243, 600));
        EnumFrame.setName("EnumFrame"); // NOI18N
        EnumFrame.setResizable(false);

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(null);

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel15.setName("jPanel15"); // NOI18N

        jPanel20.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel20.setName("jPanel20"); // NOI18N

        B_EnumDocNew.setText(resourceMap.getString("B_EnumDocNew.text")); // NOI18N
        B_EnumDocNew.setName("B_EnumDocNew"); // NOI18N
        B_EnumDocNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumDocNewActionPerformed(evt);
            }
        });

        B_EnumRelatDoc.setText(resourceMap.getString("B_EnumRelatDoc.text")); // NOI18N
        B_EnumRelatDoc.setName("B_EnumRelatDoc"); // NOI18N
        B_EnumRelatDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumRelatDocActionPerformed(evt);
            }
        });

        B_EnumIgnoretDoc.setText(resourceMap.getString("B_EnumIgnoretDoc.text")); // NOI18N
        B_EnumIgnoretDoc.setName("B_EnumIgnoretDoc"); // NOI18N
        B_EnumIgnoretDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumIgnoretDocActionPerformed(evt);
            }
        });

        jLabel38.setText(resourceMap.getString("jLabel38.text")); // NOI18N
        jLabel38.setName("jLabel38"); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(B_EnumDocNew)
                    .addComponent(B_EnumRelatDoc)
                    .addComponent(B_EnumIgnoretDoc))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumDocNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumRelatDoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumIgnoretDoc)
                .addGap(24, 24, 24))
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel21.setName("jPanel21"); // NOI18N

        jLabel39.setText(resourceMap.getString("jLabel39.text")); // NOI18N
        jLabel39.setName("jLabel39"); // NOI18N

        B_EnumPriorNew.setText(resourceMap.getString("B_EnumPriorNew.text")); // NOI18N
        B_EnumPriorNew.setName("B_EnumPriorNew"); // NOI18N
        B_EnumPriorNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumPriorNewActionPerformed(evt);
            }
        });

        B_EnumRelatPrior.setText(resourceMap.getString("B_EnumRelatPrior.text")); // NOI18N
        B_EnumRelatPrior.setName("B_EnumRelatPrior"); // NOI18N
        B_EnumRelatPrior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumRelatPriorActionPerformed(evt);
            }
        });

        B_EnumIgnoretPrior.setText(resourceMap.getString("B_EnumIgnoretPrior.text")); // NOI18N
        B_EnumIgnoretPrior.setName("B_EnumIgnoretPrior"); // NOI18N
        B_EnumIgnoretPrior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumIgnoretPriorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addComponent(B_EnumPriorNew)
                    .addComponent(B_EnumRelatPrior)
                    .addComponent(B_EnumIgnoretPrior))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumPriorNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumRelatPrior)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumIgnoretPrior)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel22.setName("jPanel22"); // NOI18N

        B_EnumIgnoretAct.setText(resourceMap.getString("B_EnumIgnoretAct.text")); // NOI18N
        B_EnumIgnoretAct.setName("B_EnumIgnoretAct"); // NOI18N
        B_EnumIgnoretAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumIgnoretActActionPerformed(evt);
            }
        });

        B_EnumRelatAct.setText(resourceMap.getString("B_EnumRelatAct.text")); // NOI18N
        B_EnumRelatAct.setName("B_EnumRelatAct"); // NOI18N
        B_EnumRelatAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumRelatActActionPerformed(evt);
            }
        });

        B_EnumActNew.setText(resourceMap.getString("B_EnumActNew.text")); // NOI18N
        B_EnumActNew.setName("B_EnumActNew"); // NOI18N
        B_EnumActNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_EnumActNewActionPerformed(evt);
            }
        });

        jLabel41.setText(resourceMap.getString("jLabel41.text")); // NOI18N
        jLabel41.setName("jLabel41"); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 192, Short.MAX_VALUE)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addComponent(B_EnumActNew)
                    .addComponent(B_EnumRelatAct)
                    .addComponent(B_EnumIgnoretAct))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 134, Short.MAX_VALUE)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumActNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumRelatAct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_EnumIgnoretAct)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel15);
        jPanel15.setBounds(390, 10, 200, 460);

        jPanel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel19.setName("jPanel19"); // NOI18N

        jScrollPane23.setName("jScrollPane23"); // NOI18N

        TA_EnumsIssues.setColumns(20);
        TA_EnumsIssues.setRows(5);
        TA_EnumsIssues.setName("TA_EnumsIssues"); // NOI18N
        jScrollPane23.setViewportView(TA_EnumsIssues);

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addContainerGap(44, Short.MAX_VALUE))
            .addComponent(jScrollPane23, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane23, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel19);
        jPanel19.setBounds(950, 10, 260, 420);

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel18.setName("jPanel18"); // NOI18N

        jPanel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel23.setName("jPanel23"); // NOI18N

        jScrollPane24.setName("jScrollPane24"); // NOI18N

        TA_SEnumDoc.setColumns(20);
        TA_SEnumDoc.setEditable(false);
        TA_SEnumDoc.setRows(5);
        TA_SEnumDoc.setName("TA_SEnumDoc"); // NOI18N
        jScrollPane24.setViewportView(TA_SEnumDoc);

        jScrollPane25.setName("jScrollPane25"); // NOI18N

        L_SEnumDoc.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        L_SEnumDoc.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        L_SEnumDoc.setName("L_SEnumDoc"); // NOI18N
        L_SEnumDoc.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                L_SEnumDocValueChanged(evt);
            }
        });
        jScrollPane25.setViewportView(L_SEnumDoc);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane25, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
            .addComponent(jScrollPane25, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel24.setName("jPanel24"); // NOI18N

        jScrollPane26.setName("jScrollPane26"); // NOI18N

        TA_SEnumPrior.setColumns(20);
        TA_SEnumPrior.setEditable(false);
        TA_SEnumPrior.setRows(5);
        TA_SEnumPrior.setName("TA_SEnumPrior"); // NOI18N
        jScrollPane26.setViewportView(TA_SEnumPrior);

        jScrollPane27.setName("jScrollPane27"); // NOI18N

        L_SEnumPrior.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        L_SEnumPrior.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        L_SEnumPrior.setName("L_SEnumPrior"); // NOI18N
        L_SEnumPrior.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                L_SEnumPriorValueChanged(evt);
            }
        });
        jScrollPane27.setViewportView(L_SEnumPrior);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane27, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
            .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
            .addComponent(jScrollPane27, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel25.setName("jPanel25"); // NOI18N

        jScrollPane28.setName("jScrollPane28"); // NOI18N

        TA_SEnumAct.setColumns(20);
        TA_SEnumAct.setEditable(false);
        TA_SEnumAct.setRows(5);
        TA_SEnumAct.setName("TA_SEnumAct"); // NOI18N
        jScrollPane28.setViewportView(TA_SEnumAct);

        jScrollPane29.setName("jScrollPane29"); // NOI18N

        L_SEnumAct.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        L_SEnumAct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        L_SEnumAct.setName("L_SEnumAct"); // NOI18N
        L_SEnumAct.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                L_SEnumActValueChanged(evt);
            }
        });
        jScrollPane29.setViewportView(L_SEnumAct);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
            .addGap(0, 364, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addComponent(jScrollPane28, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane29, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 141, Short.MAX_VALUE)
            .addGap(0, 141, Short.MAX_VALUE)
            .addComponent(jScrollPane28, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
            .addComponent(jScrollPane29, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel18);
        jPanel18.setBounds(10, 10, 370, 460);

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel16.setName("jPanel16"); // NOI18N

        jPanel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel26.setName("jPanel26"); // NOI18N

        jScrollPane30.setName("jScrollPane30"); // NOI18N

        L_TEnumDoc.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        L_TEnumDoc.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        L_TEnumDoc.setName("L_TEnumDoc"); // NOI18N
        L_TEnumDoc.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                L_TEnumDocValueChanged(evt);
            }
        });
        jScrollPane30.setViewportView(L_TEnumDoc);

        jScrollPane31.setName("jScrollPane31"); // NOI18N

        TA_TEnumDoc.setColumns(20);
        TA_TEnumDoc.setEditable(false);
        TA_TEnumDoc.setRows(5);
        TA_TEnumDoc.setName("TA_TEnumDoc"); // NOI18N
        jScrollPane31.setViewportView(TA_TEnumDoc);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jScrollPane30, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane31, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane30, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
            .addComponent(jScrollPane31, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel27.setName("jPanel27"); // NOI18N

        jScrollPane32.setName("jScrollPane32"); // NOI18N

        TA_TEnumPrior.setColumns(20);
        TA_TEnumPrior.setEditable(false);
        TA_TEnumPrior.setRows(5);
        TA_TEnumPrior.setName("TA_TEnumPrior"); // NOI18N
        jScrollPane32.setViewportView(TA_TEnumPrior);

        jScrollPane33.setName("jScrollPane33"); // NOI18N

        L_TEnumPrior.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        L_TEnumPrior.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        L_TEnumPrior.setName("L_TEnumPrior"); // NOI18N
        L_TEnumPrior.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                L_TEnumPriorValueChanged(evt);
            }
        });
        jScrollPane33.setViewportView(L_TEnumPrior);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(jScrollPane33, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane32, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane32, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
            .addComponent(jScrollPane33, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
        );

        jPanel28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel28.setName("jPanel28"); // NOI18N

        jScrollPane34.setName("jScrollPane34"); // NOI18N

        L_TEnumAct.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        L_TEnumAct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        L_TEnumAct.setName("L_TEnumAct"); // NOI18N
        L_TEnumAct.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                L_TEnumActValueChanged(evt);
            }
        });
        jScrollPane34.setViewportView(L_TEnumAct);

        jScrollPane35.setName("jScrollPane35"); // NOI18N

        TA_TEnumAct.setColumns(20);
        TA_TEnumAct.setEditable(false);
        TA_TEnumAct.setRows(5);
        TA_TEnumAct.setName("TA_TEnumAct"); // NOI18N
        jScrollPane35.setViewportView(TA_TEnumAct);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addComponent(jScrollPane34, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane35, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane34, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
            .addComponent(jScrollPane35, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel16);
        jPanel16.setBounds(600, 10, 340, 460);

        B_ApplyEnum.setText(resourceMap.getString("B_ApplyEnum.text")); // NOI18N
        B_ApplyEnum.setName("B_ApplyEnum"); // NOI18N
        B_ApplyEnum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_ApplyEnumActionPerformed(evt);
            }
        });
        jPanel13.add(B_ApplyEnum);
        B_ApplyEnum.setBounds(950, 440, 260, 25);

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel14.setName("jPanel14"); // NOI18N

        jLabel40.setFont(resourceMap.getFont("jLabel40.font")); // NOI18N
        jLabel40.setText(resourceMap.getString("jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addContainerGap(1107, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N

        jLabel37.setText(resourceMap.getString("jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N

        javax.swing.GroupLayout EnumFrameLayout = new javax.swing.GroupLayout(EnumFrame.getContentPane());
        EnumFrame.getContentPane().setLayout(EnumFrameLayout);
        EnumFrameLayout.setHorizontalGroup(
            EnumFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnumFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EnumFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(EnumFrameLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(286, 286, 286)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 1219, Short.MAX_VALUE))
                .addContainerGap())
        );
        EnumFrameLayout.setVerticalGroup(
            EnumFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnumFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(EnumFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel17.setName("jPanel17"); // NOI18N

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1078, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void B_DbConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_DbConnectActionPerformed
        // TODO add your handling code here:
        jComboBox2.removeAllItems();
        jComboBox3.removeAllItems();
        try {
            pdb = PropDB.getInstance();
            con = new ConnectToDb(pdb, mysql_db);


            if (con != null) {
                jLabel1.setText("Подключен");
            }
            stmt = con.getCon().createStatement();
            rs = stmt.executeQuery("Select db from " + mysql_db + ".db");
            while (rs.next()) {
                jComboBox2.addItem(rs.getString("db"));
                jComboBox3.addItem(rs.getString("db"));
            }
            jComboBox2.setSelectedIndex(0);
            jComboBox3.setSelectedIndex(0);
            B_DbApply.setEnabled(true);


        } catch (Exception e) {
            logger.error("in constructor", e);
            jLabel1.setText("Отключено");
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_DbConnectActionPerformed

    private void B_DbApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_DbApplyActionPerformed
        // TODO add your handling code here:
        s_db = jComboBox2.getSelectedItem().toString();
        t_db = jComboBox3.getSelectedItem().toString();

        jComboBox1.removeAllItems();
        try {

            s_con = new ConnectToDb(pdb, s_db);
            t_con = new ConnectToDb(pdb, t_db);

            DatabaseMetaData dbmd = s_con.getCon().getMetaData();

//check is table projects or not
            boolean isSourcePrj = false;
            ResultSet s_tables = dbmd.getTables(null, null, null, null);
            while (s_tables.next()) {
                if (s_tables.getString(3).compareTo("projects") == 0) {
                    isSourcePrj = true;
                }
            }
//                        

            dbmd = t_con.getCon().getMetaData();

//check has table projects or not
            boolean isTarPrj = false;
            s_tables = dbmd.getTables(null, null, null, null);
            while (s_tables.next()) {
                if (s_tables.getString(3).compareTo("projects") == 0) {
                    isTarPrj = true;
                }
            }


            if (!isSourcePrj) {

                JOptionPane.showMessageDialog(null, "В базе " + s_db + " нет таблицы \"projects\" !", "Беда!", JOptionPane.ERROR_MESSAGE);
                B_StartPageNext.setEnabled(false);
                B_loadRoles.setEnabled(false);
                B_loadTrackers.setEnabled(false);
                B_loadUsers.setEnabled(false);
                B_rewriteRoles.setEnabled(false);
                B_rewriteTrackers.setEnabled(false);
                B_rewriteUsers.setEnabled(false);
                B_rewriteStatuses.setEnabled(false);
                B_loadStatuses.setEnabled(false);
                B_rewriteEnum.setEnabled(false);
                B_loadEnums.setEnabled(false);
            } else {
                if (!isTarPrj) {
                    JOptionPane.showMessageDialog(null, "В базе " + t_db + " нет таблицы \"projects\" !", "Беда!", JOptionPane.ERROR_MESSAGE);
                    B_StartPageNext.setEnabled(false);
                    B_loadRoles.setEnabled(false);
                    B_loadTrackers.setEnabled(false);
                    B_loadUsers.setEnabled(false);
                    B_rewriteRoles.setEnabled(false);
                    B_rewriteTrackers.setEnabled(false);
                    B_rewriteUsers.setEnabled(false);
                    B_rewriteStatuses.setEnabled(false);
                    B_loadStatuses.setEnabled(false);
                    B_rewriteEnum.setEnabled(false);
                    B_loadEnums.setEnabled(false);
                } else {
                    rs = stmt.executeQuery("Select * from " + s_db + ".projects");
                    while (rs.next()) {
                        jComboBox1.addItem(rs.getString("name"));
                    }
                    B_StartPageNext.setEnabled(true);
                    B_loadRoles.setEnabled(true);
                    B_loadTrackers.setEnabled(true);
                    B_loadUsers.setEnabled(true);
                    B_rewriteRoles.setEnabled(true);
                    B_rewriteTrackers.setEnabled(true);
                    B_rewriteUsers.setEnabled(true);
                    B_rewriteStatuses.setEnabled(true);
                    B_loadStatuses.setEnabled(true);
                    B_rewriteEnum.setEnabled(true);
                    B_loadEnums.setEnabled(true);
                }
            }
        } catch (Exception e) {
            logger.error("in B_DbApplyActionPerformed", e);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_DbApplyActionPerformed

    private void B_StartPageNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_StartPageNextActionPerformed
        // TODO add your handling code here:


        idch.WriteReport("EntitiesReport (" + (new Date()).toString() + ").xls", idch.getReportE());

        project = jComboBox1.getSelectedItem().toString();
        ProjectsFrame.show();
        jLabel14.setText(project);
        try {
            prj = new Projects(s_con, t_con, s_db, t_db, project);
        } catch (Exception e) {
            logger.error("in constructor", e);
            //e.printStackTrace();
        }
        TA_prjSourceInfo.setText("");
        TA_prjSourceInfo.setText(prj.getSourceInfo());
        TA_prjTargetInfo.setText("");
        TA_prjTargetInfo.setText(prj.getTargetInfo());

    }//GEN-LAST:event_B_StartPageNextActionPerformed

    private void B_prjApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_prjApplyActionPerformed
        // TODO add your handling code here:
        System.out.println("new project id:" + prj.ProjectMove() + "");
        //   UsersFrame.show();
        ProjectsFrame.hide();
        progressBar.setIndeterminate(true);
        jLabel8.setText(project);
        try {
            if (usr == null) {
                usr = new Users(s_con, t_con, s_db, t_db);
            }
            idch.setMemberID(usr.moveMembers(idch.getUserID(), idch.getProjectID()));
            idch.setMember_rolesID(usr.moveMemberRoles(idch.getMemberID(), idch.getRoleID()));

            IssueControl issueCtrl = new IssueControl(s_db, t_db);
            //moving issues
            issueCtrl.moveIssuesWithRelatedAttributes();

            BoardControl boardCtrl = new BoardControl(s_db, t_db);
            //moving boards
            boardCtrl.moveBoardsWithRelatedMessages();

            AddonsControl addonsCtrl = new AddonsControl(s_db, t_db);
            //moving addons
            addonsCtrl.moveEnabledModules();
            addonsCtrl.moveProjectTrackers();
            idch.setDocumentID(addonsCtrl.moveDocuments());
            idch.setAttachmentID(addonsCtrl.moveAttachments());
            idch.setTimeEntryID(addonsCtrl.moveTimeEntries());
            idch.setQueryID(addonsCtrl.moveQueries());
            addonsCtrl.moveQuestions();
            //      JOptionPane.showMessageDialog(null, "addonsCtrl.moveQuestions();", "Info", JOptionPane.INFORMATION_MESSAGE);

            //moving repositories
            RepositoryControl rpCtrl = new RepositoryControl(s_db, t_db);
            idch.setRepositoryID(rpCtrl.moveRepositories());
            idch.setChangesetID(rpCtrl.moveChangesets());
            rpCtrl.moveChanges();
            rpCtrl.moveChangesetsIssues();

            idch.WriteReport("Report move project - " + project + "(" + (new Date()).toString() + ").xls", idch.getReportPrj());

            progressBar.setIndeterminate(false);
            JOptionPane.showMessageDialog(null, "Project " + project + " moved!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            logger.error("in B_DbApplyActionPerformed", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_prjApplyActionPerformed

    private void SourceUsersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SourceUsersValueChanged
        // TODO add your handling code here:
        TA_SourceUserInfo.setText("");
        TargetUsers.clearSelection();
        if (SourceUsers.getSelectedIndex() != -1) {
            jTextField1.setText(SourceUsers.getSelectedValue().toString());
            TA_SourceUserInfo.setText(usr.GetUserInfo(SourceUsers.getSelectedValue().toString(), s_db));
            TargetUsers.setSelectedValue(SourceUsers.getSelectedValue(), true);
            String srch = SourceUsers.getSelectedValue().toString();
            String ta = TA_UsersRelations.getText();
        //    TA_UsersRelations.setSelectionStart(ta.indexOf(ta.indexOf(srch), 0));
        //    TA_UsersRelations.setSelectionEnd(ta.indexOf(ta.indexOf(srch)) + srch.length());
        }


    }//GEN-LAST:event_SourceUsersValueChanged

    private void TargetUsersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_TargetUsersValueChanged
        // TODO add your handling code here:
        TA_TargetUserInfo.setText("");
        if (TargetUsers.getSelectedIndex() != -1) {
            TA_TargetUserInfo.setText(usr.GetUserInfo(TargetUsers.getSelectedValue().toString(), t_db));
        }
    }//GEN-LAST:event_TargetUsersValueChanged

    //перенести как нового пользователя
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if (SourceUsers.getSelectedIndex() != -1) {
            String selected = SourceUsers.getSelectedValue().toString();
            idch.setUsersRelations(idch.UpdateRelations(idch.getUsersRelations(), selected, "new_record"));
            String tmp_form = "";
            TA_UsersRelations.setText("");
            for (int i = 0; i < idch.getUsersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getUsersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getUsersRelations().get(i).get(1).toString() + "\n";
                TA_UsersRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать пользователя исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    //сопорставить пользователя с существующимА
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if ((SourceUsers.getSelectedIndex() != -1) && (TargetUsers.getSelectedIndex() != -1)) {
            String source = SourceUsers.getSelectedValue().toString();
            String target = TargetUsers.getSelectedValue().toString();
            idch.setUsersRelations(idch.UpdateRelations(idch.getUsersRelations(), source, target));
            String tmp_form = "";
            TA_UsersRelations.setText("");
            for (int i = 0; i < idch.getUsersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getUsersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getUsersRelations().get(i).get(1).toString() + "\n";
                TA_UsersRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать пользователей в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        idch.setUserID(usr.MoveValues(idch.getUsersRelations()));
        /// moving Users preferences
        AddonsControl addonCtrl = new AddonsControl(s_db, t_db);
        addonCtrl.moveUserPreferences();
        addonCtrl.moveTokens();



        if (idch.getUserID().size() > 0) {
            UsersCheck.setSelected(true);
        }
        Properties p = new Properties();
        for (int i = 0; i < idch.getUserID().size(); ++i) {
            p.setProperty(idch.getUserID().get(i).get(0).toString(), idch.getUserID().get(i).get(1).toString());
        }

        try {
            p.store(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("users|" + s_db + "|" + t_db + ".properties")), "UTF-8"), "Id-relations for table users in source && target DB");
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);

        }

        UsersFrame.hide();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void B_rewriteUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_rewriteUsersActionPerformed
        // TODO add your handling code here:
        UsersFrame.show();
        ProjectsFrame.hide();
        jLabel8.setText(project);
        try {
            usr = new Users(s_con, t_con, s_db, t_db);

            //список пользователей проекта источника
            ArrayList<ArrayList> src = usr.GetSourceUsers(false);
            DefaultListModel srcUsersLM = new DefaultListModel();
            SourceUsers.setModel(srcUsersLM);
            srcUsersLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                srcUsersLM.addElement(src.get(i).get(0).toString().toLowerCase());
            }

            //список пользователей назначения
            src.clear();
            src = usr.GetTargetUsers();
            DefaultListModel trgUsersLM = new DefaultListModel();
            TargetUsers.setModel(trgUsersLM);
            trgUsersLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                trgUsersLM.addElement(src.get(i).get(0).toString().toLowerCase());
            }

            globalSrcUsers.clear();
            globalTrgUsers.clear();

            globalSrcUsers = srcUsersLM;
            globalTrgUsers = trgUsersLM;

//            for (int i = 0; i < srcUsersLM.getSize(); ++i) {
//                globalSrcUsers.addElement(srcUsersLM.get(i));
//            }
//            for (int i = 0; i < trgUsersLM.getSize(); ++i) {
//                globalTrgUsers.addElement(trgUsersLM.get(i));
//            }



            ArrayList<ArrayList> relations = usr.CreateUsersRelations(false);
            idch.setUsersRelations(relations);
            String tmp_form = "";
            TA_UsersRelations.setText("");
            for (int i = 0; i < idch.getUsersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getUsersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getUsersRelations().get(i).get(1).toString() + "\n";
                TA_UsersRelations.append(tmp_form);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }



    }//GEN-LAST:event_B_rewriteUsersActionPerformed

    private void B_UserIgnoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_UserIgnoreActionPerformed
        // TODO add your handling code here:
        this.idch.UpdateRelations(idch.getUsersRelations(), SourceUsers.getSelectedValue().toString(), "ignore");
        //   ArrayList<ArrayList> relations = usr.CreateUsersRelations(false);
        //     idch.setUsersRelations(relations);
        String tmp_form = "";
        TA_UsersRelations.setText("");
        for (int i = 0; i < idch.getUsersRelations().size(); ++i) {
            tmp_form = "--------------------------------------\n"
                    + idch.getUsersRelations().get(i).get(0).toString() + "\n"
                    + "сопоставлен с \n"
                    + idch.getUsersRelations().get(i).get(1).toString() + "\n";
            TA_UsersRelations.append(tmp_form);
        }

    }//GEN-LAST:event_B_UserIgnoreActionPerformed

    private void B_loadUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_loadUsersActionPerformed
        // TODO add your handling code here:
        try {
            UsersCheck.setSelected(false);
            Properties p = new Properties();
            FileInputStream fis = new FileInputStream(new File("users|" + s_db + "|" + t_db + ".properties"));
            p.load(fis);
            ArrayList<ArrayList> loadUserId = new ArrayList<ArrayList>(0);


            for (Enumeration e = p.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                loadUserId.add(new ArrayList(0));
                loadUserId.get(loadUserId.size() - 1).add(key);
                loadUserId.get(loadUserId.size() - 1).add(p.getProperty(key));

                //System.out.println(key +"="+p.getProperty(key));
            }
            idch.setUserID(loadUserId);
            if (idch.getUserID().size() > 0) {
                UsersCheck.setSelected(true);
            } else {
                UsersCheck.setSelected(false);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            idch.setUserID(new ArrayList<ArrayList>(0));
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_loadUsersActionPerformed

    private void UsersCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersCheckActionPerformed
        // TODO add your handling code here:
        if (idch.getUserID().isEmpty()) {
            UsersCheck.setSelected(false);
        } else {
            UsersCheck.setSelected(true);
        }
    }//GEN-LAST:event_UsersCheckActionPerformed

    private void SourceRolesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SourceRolesValueChanged
        // TODO add your handling code here:
        TA_SourceRolesInfo.setText("");
        TargetRoles.clearSelection();
        if (SourceRoles.getSelectedIndex() != -1) {
            TA_SourceRolesInfo.setText(rls.GetRoleInfo(SourceRoles.getSelectedValue().toString(), s_db));
            TargetRoles.setSelectedValue(SourceRoles.getSelectedValue(), true);
        }
    }//GEN-LAST:event_SourceRolesValueChanged

    private void TargetRolesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_TargetRolesValueChanged
        // TODO add your handling code here:
        TA_TargetRolesInfo.setText("");
        if (TargetRoles.getSelectedIndex() != -1) {
            TA_TargetRolesInfo.setText(rls.GetRoleInfo(TargetRoles.getSelectedValue().toString(), s_db));
        }

    }//GEN-LAST:event_TargetRolesValueChanged

    private void B_RoleRelatedWithTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_RoleRelatedWithTargetActionPerformed
        // TODO add your handling code here:
        if ((SourceRoles.getSelectedIndex() != -1) && (TargetRoles.getSelectedIndex() != -1)) {
            String source = SourceRoles.getSelectedValue().toString();
            String target = TargetRoles.getSelectedValue().toString();
            idch.setUsersRelations(idch.UpdateRelations(idch.getRolesRelations(), source, target));
            String tmp_form = "";
            TA_RolesRelations.setText("");
            for (int i = 0; i < idch.getRolesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getRolesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getRolesRelations().get(i).get(1).toString() + "\n";
                TA_RolesRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать роли в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_B_RoleRelatedWithTargetActionPerformed

    private void B_RoleAsNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_RoleAsNewActionPerformed
        // TODO add your handling code here:
        if (SourceRoles.getSelectedIndex() != -1) {
            String selected = SourceRoles.getSelectedValue().toString();
            idch.setUsersRelations(idch.UpdateRelations(idch.getRolesRelations(), selected, "new_record"));
            String tmp_form = "";
            TA_RolesRelations.setText("");
            for (int i = 0; i < idch.getRolesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getRolesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getRolesRelations().get(i).get(1).toString() + "\n";
                TA_RolesRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать роль исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_RoleAsNewActionPerformed

    private void B_RoleApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_RoleApplyActionPerformed
        // TODO add your handling code here:
        idch.setRoleID(rls.MoveValues(idch.getRolesRelations()));

        if (idch.getRoleID().size() > 0) {
            RolesCheck.setSelected(true);
        }
        Properties p = new Properties();
        for (int i = 0; i < idch.getRoleID().size(); ++i) {
            p.setProperty(idch.getRoleID().get(i).get(0).toString(), idch.getRoleID().get(i).get(1).toString());
        }
        try {
            p.store(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("roles|" + s_db + "|" + t_db + ".properties")), "UTF-8"), "Id-relations for table users in source && target DB");
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);

        }

        RolesFrame.hide();

    }//GEN-LAST:event_B_RoleApplyActionPerformed

    private void B_RoleIgnoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_RoleIgnoreActionPerformed
        // TODO add your handling code here:
        if (SourceRoles.getSelectedIndex() != -1) {
            this.idch.UpdateRelations(idch.getRolesRelations(), SourceRoles.getSelectedValue().toString(), "ignore");
            String tmp_form = "";
            TA_RolesRelations.setText("");
            for (int i = 0; i < idch.getRolesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getRolesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getRolesRelations().get(i).get(1).toString() + "\n";
                TA_RolesRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать роль исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_RoleIgnoreActionPerformed

    private void B_rewriteRolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_rewriteRolesActionPerformed
        // TODO add your handling code here:

        RolesFrame.show();
        //jLabel21.setText(project);
        try {
            rls = new Roles(s_con, t_con, s_db, t_db);

            //список roles источника
            ArrayList src = rls.GetRoles(s_db);
            DefaultListModel srcRolesLM = new DefaultListModel();
            SourceRoles.setModel(srcRolesLM);
            srcRolesLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                srcRolesLM.addElement(src.get(i).toString());
            }

            //список пользователей назначения
            src.clear();
            src = rls.GetRoles(t_db);
            DefaultListModel trgRolesLM = new DefaultListModel();
            TargetRoles.setModel(trgRolesLM);
            trgRolesLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                trgRolesLM.addElement(src.get(i).toString());
            }

            ArrayList<ArrayList> relations = rls.CreateRolesRelations();
            idch.setRolesRelations(relations);
            String tmp_form = "";
            TA_RolesRelations.setText("");
            for (int i = 0; i < idch.getRolesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getRolesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getRolesRelations().get(i).get(1).toString() + "\n";
                TA_RolesRelations.append(tmp_form);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

    }//GEN-LAST:event_B_rewriteRolesActionPerformed

    private void RolesCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RolesCheckItemStateChanged
        // TODO add your handling code here:
        if (idch.getRoleID().isEmpty()) {
            RolesCheck.setSelected(false);
        } else {
            RolesCheck.setSelected(true);
        }
    }//GEN-LAST:event_RolesCheckItemStateChanged

    private void SourceTrackersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SourceTrackersValueChanged
        // TODO add your handling code here:
        TA_SourceTrackersInfo.setText("");
        TargetTrackers.clearSelection();
        if (SourceTrackers.getSelectedIndex() != -1) {
            TA_SourceTrackersInfo.setText(trk.GetTrackerInfo(SourceTrackers.getSelectedValue().toString(), s_db));
            TargetTrackers.setSelectedValue(SourceTrackers.getSelectedValue(), true);
        }
    }//GEN-LAST:event_SourceTrackersValueChanged

    private void TargetTrackersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_TargetTrackersValueChanged
        // TODO add your handling code here:
        TA_TargetTrackersInfo.setText("");
        if (TargetTrackers.getSelectedIndex() != -1) {
            TA_TargetTrackersInfo.setText(trk.GetTrackerInfo(TargetTrackers.getSelectedValue().toString(), s_db));
        }
    }//GEN-LAST:event_TargetTrackersValueChanged

    private void B_TrackersRelatedWithTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_TrackersRelatedWithTargetActionPerformed
        // TODO add your handling code here:
        if ((SourceTrackers.getSelectedIndex() != -1) && (TargetTrackers.getSelectedIndex() != -1)) {
            String source = SourceTrackers.getSelectedValue().toString();
            String target = TargetTrackers.getSelectedValue().toString();
            idch.setTrackersRelations(idch.UpdateRelations(idch.getTrackersRelations(), source, target));
            String tmp_form = "";
            TA_TrackersRelations.setText("");
            for (int i = 0; i < idch.getTrackersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getTrackersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getTrackersRelations().get(i).get(1).toString() + "\n";
                TA_TrackersRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать трекер в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_TrackersRelatedWithTargetActionPerformed

    private void B_TrackersAsNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_TrackersAsNewActionPerformed
        // TODO add your handling code here:
        if (SourceTrackers.getSelectedIndex() != -1) {
            String selected = SourceTrackers.getSelectedValue().toString();
            idch.setTrackersRelations(idch.UpdateRelations(idch.getTrackersRelations(), selected, "new_record"));
            String tmp_form = "";
            TA_TrackersRelations.setText("");
            for (int i = 0; i < idch.getTrackersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getTrackersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getTrackersRelations().get(i).get(1).toString() + "\n";
                TA_TrackersRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать роль исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_TrackersAsNewActionPerformed

    private void B_TrackersApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_TrackersApplyActionPerformed
        // TODO add your handling code here:
        idch.setTrackerID(trk.MoveValues(idch.getTrackersRelations()));

        if (idch.getRoleID().size() > 0) {
            TrackersCheck.setSelected(true);
        }
        Properties p = new Properties();
        for (int i = 0; i < idch.getTrackerID().size(); ++i) {
            p.setProperty(idch.getTrackerID().get(i).get(0).toString(), idch.getTrackerID().get(i).get(1).toString());
        }
        try {
            p.store(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("trackers|" + s_db + "|" + t_db + ".properties")), "UTF-8"), "Id-relations for table users in source && target DB");
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);

        }

        TrackersFrame.hide();
    }//GEN-LAST:event_B_TrackersApplyActionPerformed

    private void B_TrackersIgnoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_TrackersIgnoreActionPerformed
        // TODO add your handling code here:
        if (SourceTrackers.getSelectedIndex() != -1) {
            this.idch.UpdateRelations(idch.getTrackersRelations(), SourceTrackers.getSelectedValue().toString(), "ignore");
            String tmp_form = "";
            TA_TrackersRelations.setText("");
            for (int i = 0; i < idch.getTrackersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getTrackersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getTrackersRelations().get(i).get(1).toString() + "\n";
                TA_TrackersRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать трекер исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_TrackersIgnoreActionPerformed

    private void B_rewriteTrackersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_rewriteTrackersActionPerformed
        // TODO add your handling code here:

        TrackersFrame.show();
        //jLabel21.setText(project);
        try {
            trk = new Trackers(s_con, t_con, s_db, t_db);


            //список roles источника
            ArrayList src = trk.GetTrackers(s_db);
            DefaultListModel srcTrackersLM = new DefaultListModel();
            SourceTrackers.setModel(srcTrackersLM);
            srcTrackersLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                srcTrackersLM.addElement(src.get(i).toString());
            }

            //список пользователей назначения
            src.clear();
            src = trk.GetTrackers(t_db);
            DefaultListModel trgTrackersLM = new DefaultListModel();
            TargetTrackers.setModel(trgTrackersLM);
            trgTrackersLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                trgTrackersLM.addElement(src.get(i).toString());
            }

            ArrayList<ArrayList> relations = trk.CreateTrackersRelations();
            idch.setTrackersRelations(relations);
            String tmp_form = "";
            TA_TrackersRelations.setText("");
            for (int i = 0; i < idch.getTrackersRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getTrackersRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getTrackersRelations().get(i).get(1).toString() + "\n";
                TA_TrackersRelations.append(tmp_form);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

    }//GEN-LAST:event_B_rewriteTrackersActionPerformed

    private void TrackersCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TrackersCheckItemStateChanged
        // TODO add your handling code here:
        if (idch.getTrackerID().size() == 0) {
            TrackersCheck.setSelected(false);
        } else {
            TrackersCheck.setSelected(true);
        }
    }//GEN-LAST:event_TrackersCheckItemStateChanged

    private void B_loadRolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_loadRolesActionPerformed
        // TODO add your handling code here:
        try {
            RolesCheck.setSelected(false);
            Properties p = new Properties();
            FileInputStream fis = new FileInputStream(new File("roles|" + s_db + "|" + t_db + ".properties"));
            p.load(fis);
            ArrayList<ArrayList> loadRolesId = new ArrayList<ArrayList>(0);


            for (Enumeration e = p.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                loadRolesId.add(new ArrayList(0));
                loadRolesId.get(loadRolesId.size() - 1).add(key);
                loadRolesId.get(loadRolesId.size() - 1).add(p.getProperty(key));

                //System.out.println(key +"="+p.getProperty(key));
            }
            idch.setRoleID(loadRolesId);
            if (idch.getRoleID().size() > 0) {
                RolesCheck.setSelected(true);
            } else {
                RolesCheck.setSelected(false);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            idch.setRoleID(new ArrayList<ArrayList>(0));
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_loadRolesActionPerformed

    private void B_loadTrackersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_loadTrackersActionPerformed
        // TODO add your handling code here:
        try {
            TrackersCheck.setSelected(false);
            Properties p = new Properties();
            FileInputStream fis = new FileInputStream(new File("trackers|" + s_db + "|" + t_db + ".properties"));
            p.load(fis);
            ArrayList<ArrayList> loadTrackersId = new ArrayList<ArrayList>(0);


            for (Enumeration e = p.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                loadTrackersId.add(new ArrayList(0));
                loadTrackersId.get(loadTrackersId.size() - 1).add(key);
                loadTrackersId.get(loadTrackersId.size() - 1).add(p.getProperty(key));

                //System.out.println(key +"="+p.getProperty(key));
            }
            idch.setTrackerID(loadTrackersId);
            if (idch.getTrackerID().size() > 0) {
                TrackersCheck.setSelected(true);
            } else {
                TrackersCheck.setSelected(false);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            idch.setTrackerID(new ArrayList<ArrayList>(0));
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_loadTrackersActionPerformed

    private void SourceStatusesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SourceStatusesValueChanged
        // TODO add your handling code here:
        TA_SourceStatusesInfo.setText("");
        TargetStatuses.clearSelection();
        if (SourceStatuses.getSelectedIndex() != -1) {
            TA_SourceStatusesInfo.setText(status.GetStatusInfo(SourceStatuses.getSelectedValue().toString(), s_db));
            TargetStatuses.setSelectedValue(SourceStatuses.getSelectedValue(), true);
        }
    }//GEN-LAST:event_SourceStatusesValueChanged

    private void TargetStatusesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_TargetStatusesValueChanged
        // TODO add your handling code here:
        TA_TargetStatusesInfo.setText("");
        if (TargetStatuses.getSelectedIndex() != -1) {
            TA_TargetStatusesInfo.setText(status.GetStatusInfo(TargetStatuses.getSelectedValue().toString(), s_db));
        }
    }//GEN-LAST:event_TargetStatusesValueChanged

    private void B_StatusRelatedWithTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_StatusRelatedWithTargetActionPerformed
        // TODO add your handling code here:
        if ((SourceStatuses.getSelectedIndex() != -1) && (TargetStatuses.getSelectedIndex() != -1)) {
            String source = SourceStatuses.getSelectedValue().toString();
            String target = TargetStatuses.getSelectedValue().toString();
            idch.setStatusesRelations(idch.UpdateRelations(idch.getStatusesRelations(), source, target));
            String tmp_form = "";
            TA_StatusesRelations.setText("");
            for (int i = 0; i < idch.getStatusesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getStatusesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getStatusesRelations().get(i).get(1).toString() + "\n";
                TA_StatusesRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать статус в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_B_StatusRelatedWithTargetActionPerformed

    private void B_StatusAsNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_StatusAsNewActionPerformed
        // TODO add your handling code here:
        if (SourceStatuses.getSelectedIndex() != -1) {
            String selected = SourceStatuses.getSelectedValue().toString();
            idch.setStatusesRelations(idch.UpdateRelations(idch.getStatusesRelations(), selected, "new_record"));
            String tmp_form = "";
            TA_StatusesRelations.setText("");
            for (int i = 0; i < idch.getStatusesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getStatusesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getStatusesRelations().get(i).get(1).toString() + "\n";
                TA_StatusesRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать статус исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_StatusAsNewActionPerformed

    private void B_StatusApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_StatusApplyActionPerformed
        // TODO add your handling code here:
        idch.setStatusID(status.MoveValues(idch.getStatusesRelations()));

        if (idch.getStatusID().size() > 0) {
            StatusesCheck.setSelected(true);
        }
        Properties p = new Properties();
        for (int i = 0; i < idch.getStatusID().size(); ++i) {
            p.setProperty(idch.getStatusID().get(i).get(0).toString(), idch.getStatusID().get(i).get(1).toString());
        }
        try {
            p.store(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("statuses|" + s_db + "|" + t_db + ".properties")), "UTF-8"), "Id-relations for table users in source && target DB");
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);

        }

        StatusesFrame.hide();
    }//GEN-LAST:event_B_StatusApplyActionPerformed

    private void B_StatusIgnoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_StatusIgnoreActionPerformed
        // TODO add your handling code here:
        if (SourceStatuses.getSelectedIndex() != -1) {
            this.idch.UpdateRelations(idch.getStatusesRelations(), SourceStatuses.getSelectedValue().toString(), "ignore");
            String tmp_form = "";
            TA_StatusesRelations.setText("");
            for (int i = 0; i < idch.getStatusesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getStatusesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getStatusesRelations().get(i).get(1).toString() + "\n";
                TA_StatusesRelations.append(tmp_form);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать трекер исходной базы", "Внимание", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_B_StatusIgnoreActionPerformed

    private void StatusesCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_StatusesCheckItemStateChanged
        // TODO add your handling code here:
        if (idch.getStatusID().size() == 0) {
            StatusesCheck.setSelected(false);
        } else {
            StatusesCheck.setSelected(true);
        }
    }//GEN-LAST:event_StatusesCheckItemStateChanged

    private void B_rewriteStatusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_rewriteStatusesActionPerformed
        // TODO add your handling code here:

        StatusesFrame.show();
        //jLabel21.setText(project);
        try {
            status = new Statuses(s_con, t_con, s_db, t_db);


            //список roles источника
            ArrayList src = status.GetStatuses(s_db);
            DefaultListModel srcStatusesLM = new DefaultListModel();
            SourceStatuses.setModel(srcStatusesLM);
            srcStatusesLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                srcStatusesLM.addElement(src.get(i).toString());
            }

            //список пользователей назначения
            src.clear();
            src = status.GetStatuses(t_db);
            DefaultListModel trgStatusesLM = new DefaultListModel();
            TargetStatuses.setModel(trgStatusesLM);
            trgStatusesLM.clear();
            for (int i = 0; i < src.size(); ++i) {
                trgStatusesLM.addElement(src.get(i).toString());
            }

            ArrayList<ArrayList> relations = status.CreateStatusesRelations();
            idch.setStatusesRelations(relations);
            String tmp_form = "";
            TA_StatusesRelations.setText("");
            for (int i = 0; i < idch.getStatusesRelations().size(); ++i) {
                tmp_form = "--------------------------------------\n"
                        + idch.getStatusesRelations().get(i).get(0).toString() + "\n"
                        + "сопоставлен с \n"
                        + idch.getStatusesRelations().get(i).get(1).toString() + "\n";
                TA_StatusesRelations.append(tmp_form);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

    }//GEN-LAST:event_B_rewriteStatusesActionPerformed

    private void B_loadStatusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_loadStatusesActionPerformed
        // TODO add your handling code here:
        try {
            StatusesCheck.setSelected(false);
            Properties p = new Properties();
            FileInputStream fis = new FileInputStream(new File("statuses|" + s_db + "|" + t_db + ".properties"));
            p.load(fis);
            ArrayList<ArrayList> loadStatusesId = new ArrayList<ArrayList>(0);


            for (Enumeration e = p.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                loadStatusesId.add(new ArrayList(0));
                loadStatusesId.get(loadStatusesId.size() - 1).add(key);
                loadStatusesId.get(loadStatusesId.size() - 1).add(p.getProperty(key));

                //System.out.println(key +"="+p.getProperty(key));
            }
            idch.setStatusID(loadStatusesId);
            if (idch.getStatusID().size() > 0) {
                StatusesCheck.setSelected(true);
            } else {
                StatusesCheck.setSelected(false);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            idch.setStatusID(new ArrayList<ArrayList>(0));
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_loadStatusesActionPerformed

    private void EnumCheckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_EnumCheckItemStateChanged
        // TODO add your handling code here:
        if (idch.getEnumID().size() == 0) {
            EnumCheck.setSelected(false);
        } else {
            EnumCheck.setSelected(true);
        }
    }//GEN-LAST:event_EnumCheckItemStateChanged

    private void B_rewriteEnumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_rewriteEnumActionPerformed
        // TODO add your handling code here:
        EnumFrame.show();
        try {
            EnumControl eCtrl = EnumControl.getInstance(s_db, t_db);
//src
            DefaultListModel srcDocLM = new DefaultListModel();
            L_SEnumDoc.setModel(srcDocLM);
            srcDocLM.clear();
            List<RedmineEnum> srcDoc = eCtrl.getL_SDocEnums();
            for (int i = 0; i < srcDoc.size(); ++i) {
                srcDocLM.addElement(srcDoc.get(i).getName());
            }

            DefaultListModel srcPriorLM = new DefaultListModel();
            L_SEnumPrior.setModel(srcPriorLM);
            srcPriorLM.clear();
            List<RedmineEnum> srcPrior = eCtrl.getL_SPriorEnums();
            for (int i = 0; i < srcPrior.size(); ++i) {
                srcPriorLM.addElement(srcPrior.get(i).getName());
            }

            DefaultListModel srcActLM = new DefaultListModel();
            L_SEnumAct.setModel(srcActLM);
            srcActLM.clear();
            List<RedmineEnum> srcAct = eCtrl.getL_SActEnums();
            for (int i = 0; i < srcAct.size(); ++i) {
                srcActLM.addElement(srcAct.get(i).getName());
            }
//target
            DefaultListModel trgDocLM = new DefaultListModel();
            L_TEnumDoc.setModel(trgDocLM);
            trgDocLM.clear();
            List<RedmineEnum> trgDoc = eCtrl.getL_TDocEnums();
            for (int i = 0; i < trgDoc.size(); ++i) {
                trgDocLM.addElement(trgDoc.get(i).getName());
            }

            DefaultListModel trgPriorLM = new DefaultListModel();
            L_TEnumPrior.setModel(trgPriorLM);
            trgPriorLM.clear();
            List<RedmineEnum> trgPrior = eCtrl.getL_TPriorEnums();
            for (int i = 0; i < trgPrior.size(); ++i) {
                trgPriorLM.addElement(trgPrior.get(i).getName());
            }

            DefaultListModel trgActLM = new DefaultListModel();
            L_TEnumAct.setModel(trgActLM);
            trgActLM.clear();
            List<RedmineEnum> trgAct = eCtrl.getL_TActEnums();
            for (int i = 0; i < trgAct.size(); ++i) {
                trgActLM.addElement(trgAct.get(i).getName());
            }

            idch.setEnumRelations(eCtrl.CreateRelations());
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));

        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_rewriteEnumActionPerformed

    private void B_loadEnumsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_loadEnumsActionPerformed
        // TODO add your handling code here:
        try {
            EnumCheck.setSelected(false);
            EnumControl.getInstance(s_db, t_db).LoadEnumProperties();
            if (idch.getEnumID().size() > 0) {
                EnumCheck.setSelected(true);
            } else {
                EnumCheck.setSelected(false);
            }

        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage() + e.getStackTrace(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_B_loadEnumsActionPerformed

    private void L_SEnumDocValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_L_SEnumDocValueChanged
        // TODO add your handling code here:
        try {
            TA_SEnumDoc.setText("");
            EnumControl ec = EnumControl.getInstance(s_db, t_db);
            if (L_SEnumDoc.getSelectedIndex() != -1) {
                TA_SEnumDoc.setText(ec.FindByName(ec.getL_SDocEnums(), L_SEnumDoc.getSelectedValue().toString()).asString());
                L_TEnumDoc.clearSelection();
                L_TEnumDoc.setSelectedValue(L_SEnumDoc.getSelectedValue(), true);
            }
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }

    }//GEN-LAST:event_L_SEnumDocValueChanged

    private void L_SEnumPriorValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_L_SEnumPriorValueChanged
        // TODO add your handling code here:
        try {
            TA_SEnumPrior.setText("");
            EnumControl ec = EnumControl.getInstance(s_db, t_db);
            if (L_SEnumPrior.getSelectedIndex() != -1) {

                TA_SEnumPrior.setText(ec.FindByName(ec.getL_SPriorEnums(), L_SEnumPrior.getSelectedValue().toString()).asString());
                L_TEnumPrior.clearSelection();
                L_TEnumPrior.setSelectedValue(L_SEnumPrior.getSelectedValue(), true);
            }
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_L_SEnumPriorValueChanged

    private void L_SEnumActValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_L_SEnumActValueChanged
        // TODO add your handling code here:
        try {
            TA_SEnumAct.setText("");
            EnumControl ec = EnumControl.getInstance(s_db, t_db);
            if (L_SEnumAct.getSelectedIndex() != -1) {

                TA_SEnumAct.setText(ec.FindByName(ec.getL_SActEnums(), L_SEnumAct.getSelectedValue().toString()).asString());
                L_TEnumAct.clearSelection();
                L_TEnumAct.setSelectedValue(L_SEnumAct.getSelectedValue(), true);
            }
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_L_SEnumActValueChanged

    private void L_TEnumDocValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_L_TEnumDocValueChanged
        // TODO add your handling code here:
        try {
            TA_TEnumDoc.setText("");
            EnumControl ec = EnumControl.getInstance(s_db, t_db);
            if (L_TEnumDoc.getSelectedIndex() != -1) {
                TA_TEnumDoc.setText(ec.FindByName(ec.getL_TDocEnums(), L_TEnumDoc.getSelectedValue().toString()).asString());
            }
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_L_TEnumDocValueChanged

    private void L_TEnumPriorValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_L_TEnumPriorValueChanged
        // TODO add your handling code here:
        try {
            TA_TEnumPrior.setText("");
            EnumControl ec = EnumControl.getInstance(s_db, t_db);
            if (L_TEnumPrior.getSelectedIndex() != -1) {
                TA_TEnumPrior.setText(ec.FindByName(ec.getL_TPriorEnums(), L_TEnumPrior.getSelectedValue().toString()).asString());
            }
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_L_TEnumPriorValueChanged

    private void L_TEnumActValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_L_TEnumActValueChanged
        // TODO add your handling code here:
        try {
            TA_TEnumAct.setText("");
            EnumControl ec = EnumControl.getInstance(s_db, t_db);
            if (L_TEnumAct.getSelectedIndex() != -1) {
                TA_TEnumAct.setText(ec.FindByName(ec.getL_TActEnums(), L_TEnumAct.getSelectedValue().toString()).asString());
            }
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }//GEN-LAST:event_L_TEnumActValueChanged

    private void B_EnumDocNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumDocNewActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumDoc.getSelectedIndex() != -1)) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumDoc.getSelectedValue().toString(), "new_record"));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumDocNewActionPerformed

    private void B_EnumRelatDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumRelatDocActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumDoc.getSelectedIndex() != -1) && L_TEnumDoc.getSelectedIndex() != -1) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumDoc.getSelectedValue().toString(), L_TEnumDoc.getSelectedValue().toString()));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_B_EnumRelatDocActionPerformed

    private void B_EnumIgnoretDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumIgnoretDocActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumDoc.getSelectedIndex() != -1)) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumDoc.getSelectedValue().toString(), "ignore"));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumIgnoretDocActionPerformed

    private void B_EnumPriorNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumPriorNewActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumPrior.getSelectedIndex() != -1)) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumPrior.getSelectedValue().toString(), "new_record"));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumPriorNewActionPerformed

    private void B_EnumRelatPriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumRelatPriorActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumPrior.getSelectedIndex() != -1) && L_TEnumPrior.getSelectedIndex() != -1) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumPrior.getSelectedValue().toString(), L_TEnumPrior.getSelectedValue().toString()));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumRelatPriorActionPerformed

    private void B_EnumIgnoretPriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumIgnoretPriorActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumPrior.getSelectedIndex() != -1)) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumPrior.getSelectedValue().toString(), "ignore"));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumIgnoretPriorActionPerformed

    private void B_EnumActNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumActNewActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumAct.getSelectedIndex() != -1)) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumAct.getSelectedValue().toString(), "new_record"));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumActNewActionPerformed

    private void B_EnumRelatActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumRelatActActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumAct.getSelectedIndex() != -1) && L_TEnumAct.getSelectedIndex() != -1) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumAct.getSelectedValue().toString(), L_TEnumAct.getSelectedValue().toString()));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumRelatActActionPerformed

    private void B_EnumIgnoretActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_EnumIgnoretActActionPerformed
        // TODO add your handling code here:
        if ((L_SEnumAct.getSelectedIndex() != -1)) {
            idch.setEnumRelations(idch.UpdateRelations(idch.getEnumRelations(), L_SEnumAct.getSelectedValue().toString(), "ignore"));
            TA_EnumsIssues.setText("");
            TA_EnumsIssues.setText(idch.getPreparedIssue(idch.getEnumRelations()));
        } else {
            JOptionPane.showMessageDialog(null, "Необходимо выбрать данные в исходной и в целевой базах", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_B_EnumIgnoretActActionPerformed

    private void B_ApplyEnumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_ApplyEnumActionPerformed
        // TODO add your handling code here:
        idch.setEnumID(EnumControl.getInstance(s_db, t_db).MoveValues(idch.getEnumRelations()));

        if (idch.getEnumID().size() > 0) {
            EnumCheck.setSelected(true);
        }
        Properties p = new Properties();
        for (int i = 0; i < idch.getEnumID().size(); ++i) {
            p.setProperty(idch.getEnumID().get(i).get(0).toString(), idch.getEnumID().get(i).get(1).toString());
        }
        try {
            p.store(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("enumerations|" + s_db + "|" + t_db + ".properties")), "UTF-8"), "Id-relations for table users in source && target DB");
        } catch (Exception e) {
            logger.error("in constructor", e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Беда!", JOptionPane.ERROR_MESSAGE);

        }

        EnumFrame.hide();

    }//GEN-LAST:event_B_ApplyEnumActionPerformed

    private void jTextField2InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField2InputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2InputMethodTextChanged

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2KeyTyped

    private void jTextField2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2MouseReleased

    private void jTextField2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MousePressed
        // TODO add your handling code here:
        jTextField2.setSelectionStart(0);
        jTextField2.setSelectionEnd(jTextField2.getText().length());
    }//GEN-LAST:event_jTextField2MousePressed

    private void jTextField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MousePressed
        // TODO add your handling code here:
        jTextField1.setSelectionStart(0);
        jTextField2.setSelectionEnd(jTextField1.getText().length());
    }//GEN-LAST:event_jTextField1MousePressed

    private void jTextField2CaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField2CaretPositionChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2CaretPositionChanged

    private void jTextField2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField2CaretUpdate
        // TODO add your handling code here:
        DefaultListModel new_model = new DefaultListModel();

        String srch = jTextField2.getText();
        if (!srch.equals("")) {
            for (int i = 0; i < globalSrcUsers.getSize(); ++i) {
                String cur = globalSrcUsers.getElementAt(i).toString();
                if (cur.contains(srch)) {
                    new_model.addElement(cur);
                }
            }
            if (new_model.getSize() != 0) {
                SourceUsers.setModel(new_model);
            } else {
                SourceUsers.setModel(globalSrcUsers);
            }
        } else {
            SourceUsers.setModel(globalSrcUsers);
        }
    }//GEN-LAST:event_jTextField2CaretUpdate

    private void jTextField1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField1CaretUpdate
        // TODO add your handling code here:
        DefaultListModel new_model = new DefaultListModel();

        String srch = jTextField1.getText();
        if (!srch.equals("")) {
            for (int i = 0; i < globalTrgUsers.getSize(); ++i) {
                String cur = globalTrgUsers.getElementAt(i).toString();
                if (cur.contains(srch)) {
                    new_model.addElement(cur);
                }
            }
            if (new_model.getSize() != 0) {
                TargetUsers.setModel(new_model);
            } else {
                TargetUsers.setModel(globalTrgUsers);
            }
        } else {
            TargetUsers.setModel(globalTrgUsers);
        }
    }//GEN-LAST:event_jTextField1CaretUpdate
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B_ApplyEnum;
    private javax.swing.JButton B_DbApply;
    private javax.swing.JButton B_DbConnect;
    private javax.swing.JButton B_EnumActNew;
    private javax.swing.JButton B_EnumDocNew;
    private javax.swing.JButton B_EnumIgnoretAct;
    private javax.swing.JButton B_EnumIgnoretDoc;
    private javax.swing.JButton B_EnumIgnoretPrior;
    private javax.swing.JButton B_EnumPriorNew;
    private javax.swing.JButton B_EnumRelatAct;
    private javax.swing.JButton B_EnumRelatDoc;
    private javax.swing.JButton B_EnumRelatPrior;
    private javax.swing.JButton B_RoleApply;
    private javax.swing.JButton B_RoleAsNew;
    private javax.swing.JButton B_RoleIgnore;
    private javax.swing.JButton B_RoleRelatedWithTarget;
    private javax.swing.JButton B_StartPageNext;
    private javax.swing.JButton B_StatusApply;
    private javax.swing.JButton B_StatusAsNew;
    private javax.swing.JButton B_StatusIgnore;
    private javax.swing.JButton B_StatusRelatedWithTarget;
    private javax.swing.JButton B_TrackersApply;
    private javax.swing.JButton B_TrackersAsNew;
    private javax.swing.JButton B_TrackersIgnore;
    private javax.swing.JButton B_TrackersRelatedWithTarget;
    private javax.swing.JButton B_UserIgnore;
    private javax.swing.JButton B_loadEnums;
    private javax.swing.JButton B_loadRoles;
    private javax.swing.JButton B_loadStatuses;
    private javax.swing.JButton B_loadTrackers;
    private javax.swing.JButton B_loadUsers;
    private javax.swing.JButton B_prjApply;
    private javax.swing.JButton B_rewriteEnum;
    private javax.swing.JButton B_rewriteRoles;
    private javax.swing.JButton B_rewriteStatuses;
    private javax.swing.JButton B_rewriteTrackers;
    private javax.swing.JButton B_rewriteUsers;
    private javax.swing.JCheckBox EnumCheck;
    private javax.swing.JFrame EnumFrame;
    private javax.swing.JList L_SEnumAct;
    private javax.swing.JList L_SEnumDoc;
    private javax.swing.JList L_SEnumPrior;
    private javax.swing.JList L_TEnumAct;
    private javax.swing.JList L_TEnumDoc;
    private javax.swing.JList L_TEnumPrior;
    private javax.swing.JFrame ProjectsFrame;
    private javax.swing.JCheckBox RolesCheck;
    private javax.swing.JFrame RolesFrame;
    private javax.swing.JList SourceRoles;
    private javax.swing.JList SourceStatuses;
    private javax.swing.JList SourceTrackers;
    private javax.swing.JList SourceUsers;
    private javax.swing.JCheckBox StatusesCheck;
    private javax.swing.JFrame StatusesFrame;
    private javax.swing.JTextArea TA_EnumsIssues;
    private javax.swing.JTextArea TA_RolesRelations;
    private javax.swing.JTextArea TA_SEnumAct;
    private javax.swing.JTextArea TA_SEnumDoc;
    private javax.swing.JTextArea TA_SEnumPrior;
    private javax.swing.JTextArea TA_SourceRolesInfo;
    private javax.swing.JTextArea TA_SourceStatusesInfo;
    private javax.swing.JTextArea TA_SourceTrackersInfo;
    private javax.swing.JTextArea TA_SourceUserInfo;
    private javax.swing.JTextArea TA_StatusesRelations;
    private javax.swing.JTextArea TA_TEnumAct;
    private javax.swing.JTextArea TA_TEnumDoc;
    private javax.swing.JTextArea TA_TEnumPrior;
    private javax.swing.JTextArea TA_TargetRolesInfo;
    private javax.swing.JTextArea TA_TargetStatusesInfo;
    private javax.swing.JTextArea TA_TargetTrackersInfo;
    private javax.swing.JTextArea TA_TargetUserInfo;
    private javax.swing.JTextArea TA_TrackersRelations;
    private javax.swing.JTextArea TA_UsersRelations;
    private javax.swing.JTextArea TA_prjSourceInfo;
    private javax.swing.JTextArea TA_prjTargetInfo;
    private javax.swing.JList TargetRoles;
    private javax.swing.JList TargetStatuses;
    private javax.swing.JList TargetTrackers;
    private javax.swing.JList TargetUsers;
    private javax.swing.JCheckBox TrackersCheck;
    private javax.swing.JFrame TrackersFrame;
    private javax.swing.JCheckBox UsersCheck;
    private javax.swing.JFrame UsersFrame;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane28;
    private javax.swing.JScrollPane jScrollPane29;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane30;
    private javax.swing.JScrollPane jScrollPane31;
    private javax.swing.JScrollPane jScrollPane32;
    private javax.swing.JScrollPane jScrollPane33;
    private javax.swing.JScrollPane jScrollPane34;
    private javax.swing.JScrollPane jScrollPane35;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
