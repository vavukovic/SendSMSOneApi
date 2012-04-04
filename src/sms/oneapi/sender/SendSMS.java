package sms.oneapi.sender;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.CardLayout;
import javax.swing.UIManager;
import sms.common.config.MainConfig;
import sms.common.exceptiontype.ConfigException;
import sms.common.exceptiontype.DeliveryReportListenerException;
import sms.common.exceptiontype.InboundMessageListenerException;
import sms.common.exceptiontype.QueryDeliveryStatusException;
import sms.common.exceptiontype.SendSmsException;
import sms.common.impl.SMSClient;
import sms.common.impl.SMSClient.SenderType;
import sms.common.model.DeliveryReportListener;
import sms.common.model.InboundMessageListener;
import sms.common.model.SMS;
import sms.common.response.RetrieveSMSResponse;
import sms.common.response.SMSSendDeliveryStatusResponse;
import sms.common.response.SMSSendResponse;
import sms.oneapi.exceptiontype.CancelDeliveryNotificationsException;
import sms.oneapi.exceptiontype.CancelReceiptNotificationsException;
import sms.oneapi.exceptiontype.SubscribeToDeliveryNotificationException;
import sms.oneapi.exceptiontype.SubscribeToReceiptNotificationsException;
import sms.oneapi.response.LocationResponse;
import sms.oneapi.response.SMSDeliveryReceiptSubscriptionResponse;
import sms.oneapi.response.SMSMessageReceiptSubscriptionResponse;
import sms.smpp.impl.SmppSessionWrapper.DLRType;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JRadioButton;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JCheckBox;

public class SendSMS extends JFrame {
	private static final long serialVersionUID = 1L;

	private SMSClient client;	
	private boolean actionInProgress = false;
	private JTextField txtSMSSenderAddress;
	private JTextField txtSMSRecipientAddress;
	private JTextField txtHLRAddress;
	private JTextField txtConfigHttpUsername;
	private JTextField txtConfigHttpPassword;
	private JTextField txtDataCoding;
	private JTextField txtEsmClass;
	private JTextField txtSourceTon;
	private JTextField txtSourceNpi;
	private JTextField txtDestinationNpi;
	private JTextField txtDestinationTon;
	private JTextField txtValidityPeriod;
	private JTextArea txtSMSMessageText;
	private DefaultListModel smsLogListModel = new DefaultListModel();
	private DefaultListModel hlrLogListModel = new DefaultListModel();
	private DefaultListModel registerSenderLogListModel = new DefaultListModel();
	private DefaultListModel lbsLogListModel = new DefaultListModel();
	private DefaultListModel dlrLogListModel = new DefaultListModel();
	private DefaultListModel moLogListModel = new DefaultListModel();
	private DefaultListModel sentSMSUrlLogListModel = new DefaultListModel();	
	private JTextField txtRegisterDesc;
	private JTextField txtRegisterGsm;
	private JTextField txtVerifiyPin;
	private JTextField txtVerifiyGsm;
	private JTextField txtSMSClientCorrelator;
	private JTextField txtSMSNotifyURL;
	private JTextField txtSMSSenderName;
	private JTextField txtSMSCallbackData;
	private JTextField txtLBSAddress;
	private JTextField txtDLRSenderAddress;
	private JTextField txtDLRClientCorrelator;
	private JTextField txtDLRNotifiyUrl;
	private JTextField txtDLRCallbackData;
	private JTextField txtDLRSubscriptionId;
	private JTextField txtMODestAddress;
	private JTextField txtMOClientCorrelator;
	private JTextField txtMOCallbackData;
	private JTextField txtMONotifyUrl;
	private JTextField txtMOSubscriptionId;
	private JTextField txtDLRCriteria;
	private JTextField txtMOCriteria;
	private JTextField txtMONotifFormat;
	private JTextField txtDLRResourceUrl;
	private JTextField txtDLRResourceId;
	private JTextField txtDLRQuerySenderAddress;
	private JLabel lblUnicode;
	private JTextField txtLBSRequestedAccuracy;
	private JTextField txtDLRCancelResourceURL;
	private JTextField txtMOCancelResourceURL;
	private JTextField txtMORegistrationId;
	private JTextField txtMOMaxBatchSize;
	private JTextField txtConfigRootMessagingUrl;
	private JTextField txtConfigVersionOneAPISMS;
	private JTextField txtConfigRetrieverRegistrationID;
	private JTextField txtConfigInboundRetrievingInterval;
	private JTextField txtConfigDLRRetrievingInterval;
	private JTextField txtConfigSMPPSystemId;
	private JTextField txtConfigSMPPPassword;
	private JTextField txtConfigSMPPHost;
	private JTextField txtConfigSMPPPort;
	private JTextField txtProtocolId;
	private JRadioButton rbOneAPI;
	private JRadioButton rbSMPP;	
	private JList listSentMessgesUrl;
	private JButton btnQuerySentSMSDLR;
	private JButton btnClearSentSMSUrlList; 
	private JButton btnRemoveSentSMSDLR;
	private JCheckBox chkSendBinary;
	private JCheckBox chkSendAsFlashNotif;
	private final JPanel panelGeneralMessageSettings;
	private final JPanel panelOptionalSMSFields;
	private final String SENT_SMS_URL_LIST_FILE_PATH = "sentSMSUrlList.txt";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SendSMS frame = new SendSMS();

			frame.setIconImage((new ImageIcon("inb.jpg")).getImage());
			frame.setVisible(true);		
			frame.loadSentSMSUrlListFile();
			frame.loadConfiguration();	
			frame.setClient();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SendSMS() {
		setTitle("SEND SMS - OneAPI");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.destroy();
			}
		});
		setBounds(100, 100, 835, 624);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		JPanel panelSendSMS = new JPanel();
		panelSendSMS.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		tabbedPane.addTab("Send SMS", null, panelSendSMS, null);

		JPanel panelSMSData = new JPanel();
		panelSMSData.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JLabel lblSenderAddress = new JLabel("Sender Address:");
		lblSenderAddress.setBounds(16, 9, 119, 14);

		txtSMSSenderAddress = new JTextField();
		txtSMSSenderAddress.setBounds(145, 6, 368, 20);
		txtSMSSenderAddress.setColumns(10);

		JLabel lblRecipientaddress = new JLabel("Recipient Address:");
		lblRecipientaddress.setBounds(16, 35, 119, 14);

		txtSMSRecipientAddress = new JTextField();
		txtSMSRecipientAddress.setBounds(145, 32, 368, 20);
		txtSMSRecipientAddress.setColumns(10);

		JScrollPane scrollPane = new JScrollPane((Component) null);
		scrollPane.setBounds(145, 63, 368, 48);

		JLabel lblMessage = new JLabel("Message:");
		lblMessage.setBounds(16, 66, 101, 14);

		txtSMSMessageText = new JTextArea(3, 30);
		txtSMSMessageText.setLineWrap(true);
		txtSMSMessageText.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtSMSMessageText);

		txtSMSMessageText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkSMSMessageText();
			}		

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkSMSMessageText();			
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkSMSMessageText();
			}
		});	

		JPanel panelLog = new JPanel();
		panelLog.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Log", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelLog.setLayout(new CardLayout(0, 0));

		JList listSMS = new JList(smsLogListModel);
		listSMS.setVisibleRowCount(-1);
		JScrollPane pane = new JScrollPane(listSMS);

		panelLog.add(pane, "name_39164m949992302");

		panelSMSData.setLayout(null);
		panelSMSData.add(lblSenderAddress);
		panelSMSData.add(txtSMSSenderAddress);
		panelSMSData.add(lblRecipientaddress);
		panelSMSData.add(txtSMSRecipientAddress);
		panelSMSData.add(lblMessage);
		panelSMSData.add(scrollPane);

		panelGeneralMessageSettings = new JPanel();
		panelGeneralMessageSettings.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "General SMS Settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JLabel label_4 = new JLabel("DataCoding:");
		label_4.setBounds(20, 31, 77, 14);

		txtDataCoding = new JTextField();
		txtDataCoding.setBounds(120, 25, 57, 20);
		txtDataCoding.setColumns(10);

		txtEsmClass = new JTextField();
		txtEsmClass.setBounds(120, 51, 57, 20);
		txtEsmClass.setColumns(10);

		JLabel label_6 = new JLabel("EsmClass:");
		label_6.setBounds(20, 57, 77, 14);

		JLabel label_7 = new JLabel("Source TON:");
		label_7.setBounds(20, 83, 77, 14);

		txtSourceTon = new JTextField();
		txtSourceTon.setBounds(120, 77, 57, 20);
		txtSourceTon.setColumns(10);

		JLabel label_8 = new JLabel("Source NPI:");
		label_8.setBounds(20, 109, 77, 14);

		txtSourceNpi = new JTextField();
		txtSourceNpi.setBounds(120, 103, 57, 20);
		txtSourceNpi.setColumns(10);

		JLabel label_9 = new JLabel("Destination TON:");
		label_9.setBounds(20, 135, 95, 14);

		JLabel label_10 = new JLabel("Destination NPI:");
		label_10.setBounds(20, 161, 95, 14);

		txtDestinationNpi = new JTextField();
		txtDestinationNpi.setBounds(120, 155, 57, 20);
		txtDestinationNpi.setColumns(10);

		txtDestinationTon = new JTextField();
		txtDestinationTon.setBounds(120, 129, 57, 20);
		txtDestinationTon.setColumns(10);

		JLabel label_11 = new JLabel("Validity Period:");
		label_11.setBounds(20, 187, 95, 14);

		txtValidityPeriod = new JTextField();
		txtValidityPeriod.setBounds(120, 181, 57, 20);
		txtValidityPeriod.setToolTipText("HTTP format: \"HH:mm\"  , SMPP format: “YYMMDDhhmmsstnnp\" - “000011060755000R“ (means 11 days, 6 hours, 7 minutes, 55 seconds from now.)");
		txtValidityPeriod.setColumns(10);

		panelOptionalSMSFields = new JPanel();
		panelOptionalSMSFields.setBounds(12, 112, 503, 139);
		panelOptionalSMSFields.setBorder(new TitledBorder(null, "Optional ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSMSData.add(panelOptionalSMSFields);
		panelOptionalSMSFields.setLayout(null);

		JLabel lblClientCorrelator = new JLabel("Client Correlator:");
		lblClientCorrelator.setBounds(10, 21, 112, 14);
		panelOptionalSMSFields.add(lblClientCorrelator);

		txtSMSClientCorrelator = new JTextField();
		txtSMSClientCorrelator.setBounds(132, 21, 359, 20);
		panelOptionalSMSFields.add(txtSMSClientCorrelator);
		txtSMSClientCorrelator.setColumns(10);

		JLabel lblNotifyUrl = new JLabel("Notify URL:");
		lblNotifyUrl.setBounds(10, 47, 112, 14);
		panelOptionalSMSFields.add(lblNotifyUrl);

		txtSMSNotifyURL = new JTextField();
		txtSMSNotifyURL.setBounds(132, 47, 359, 20);
		panelOptionalSMSFields.add(txtSMSNotifyURL);
		txtSMSNotifyURL.setColumns(10);

		JLabel lblSenderName = new JLabel("Sender Name:");
		lblSenderName.setBounds(10, 75, 112, 14);
		panelOptionalSMSFields.add(lblSenderName);

		txtSMSSenderName = new JTextField();
		txtSMSSenderName.setBounds(132, 75, 359, 20);
		panelOptionalSMSFields.add(txtSMSSenderName);
		txtSMSSenderName.setColumns(10);

		JLabel lblCallbackData = new JLabel("Callback Data:");
		lblCallbackData.setBounds(10, 101, 112, 14);
		panelOptionalSMSFields.add(lblCallbackData);

		txtSMSCallbackData = new JTextField();
		txtSMSCallbackData.setBounds(132, 101, 359, 20);
		panelOptionalSMSFields.add(txtSMSCallbackData);
		txtSMSCallbackData.setColumns(10);

		JButton btnSendSMS = new JButton("Send");
		btnSendSMS.setBounds(423, 257, 90, 23);
		panelSMSData.add(btnSendSMS);
		btnSendSMS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendSMS();
			}
		});
		btnSendSMS.setActionCommand("SendSMS");

		lblUnicode = new JLabel("");
		lblUnicode.setForeground(Color.BLUE);
		lblUnicode.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUnicode.setBounds(69, 87, 66, 14);
		panelSMSData.add(lblUnicode);

		JPanel panelSendLBS = new JPanel();
		tabbedPane.addTab("Locate Terminal", null, panelSendLBS, null);

		JPanel panel_9 = new JPanel();
		panel_9.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Log", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_9.setLayout(new CardLayout(0, 0));

		JList listLBS = new JList(lbsLogListModel);
		listLBS.setVisibleRowCount(-1);
		JScrollPane scrollPane_1 = new JScrollPane(listLBS);
		panel_9.add(scrollPane_1, "name_5388036062334");
		scrollPane_1.setViewportView(listLBS);

		JPanel panel_12 = new JPanel();
		panel_12.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(16, 23, 128, 14);

		txtLBSAddress = new JTextField();
		txtLBSAddress.setBounds(154, 22, 303, 20);
		txtLBSAddress.setColumns(10);

		JButton btnSendLBS = new JButton("Send");
		btnSendLBS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				locateTerminal();
			}
		});
		btnSendLBS.setBounds(475, 21, 101, 23);
		btnSendLBS.setActionCommand("SendHLR");
		panel_12.setLayout(null);
		panel_12.add(lblAddress);
		panel_12.add(txtLBSAddress);
		panel_12.add(btnSendLBS);

		JLabel lblRequestedAccuracy = new JLabel("Requested Accuracy:");
		lblRequestedAccuracy.setBounds(16, 51, 128, 14);
		panel_12.add(lblRequestedAccuracy);

		txtLBSRequestedAccuracy = new JTextField();
		txtLBSRequestedAccuracy.setColumns(10);
		txtLBSRequestedAccuracy.setBounds(154, 50, 303, 20);
		panel_12.add(txtLBSRequestedAccuracy);
		GroupLayout gl_panelSendLBS = new GroupLayout(panelSendLBS);
		gl_panelSendLBS.setHorizontalGroup(
				gl_panelSendLBS.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendLBS.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_panelSendLBS.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_12, GroupLayout.PREFERRED_SIZE, 794, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_9, GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE))
								.addGap(10))
				);
		gl_panelSendLBS.setVerticalGroup(
				gl_panelSendLBS.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendLBS.createSequentialGroup()
						.addGap(11)
						.addComponent(panel_12, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
						.addGap(11)
						.addComponent(panel_9, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
						.addGap(11))
				);
		panelSendLBS.setLayout(gl_panelSendLBS);

		JPanel panelSendHLR = new JPanel();
		tabbedPane.addTab("Send HLR", null, panelSendHLR, null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JLabel lblAddress_1 = new JLabel("Address:");

		txtHLRAddress = new JTextField();
		txtHLRAddress.setColumns(10);

		JButton btnSendHLR = new JButton("Send");
		btnSendHLR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SendHLR();
			}
		});
		btnSendHLR.setActionCommand("SendHLR");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblAddress_1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(txtHLRAddress, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(btnSendHLR, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(281, Short.MAX_VALUE))
				);
		gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtHLRAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblAddress_1)
								.addComponent(btnSendHLR))
								.addContainerGap(12, Short.MAX_VALUE))
				);
		panel.setLayout(gl_panel);

		JPanel panelHLRLog = new JPanel();
		panelHLRLog.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Log", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelHLRLog.setLayout(new CardLayout(0, 0));

		JList listHLR = new JList(hlrLogListModel);	
		listHLR.setVisibleRowCount(-1);
		JScrollPane pane1 = new JScrollPane(listHLR);	
		panelHLRLog.add(pane1, "name_391641949992302");
		GroupLayout gl_panelSendHLR = new GroupLayout(panelSendHLR);
		gl_panelSendHLR.setHorizontalGroup(
				gl_panelSendHLR.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendHLR.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_panelSendHLR.createParallelGroup(Alignment.LEADING)
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 794, GroupLayout.PREFERRED_SIZE)
								.addComponent(panelHLRLog, GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE))
								.addGap(10))
				);
		gl_panelSendHLR.setVerticalGroup(
				gl_panelSendHLR.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendHLR.createSequentialGroup()
						.addGap(11)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
						.addGap(11)
						.addComponent(panelHLRLog, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
						.addGap(11))
				);
		panelSendHLR.setLayout(gl_panelSendHLR);

		JPanel panelDLR = new JPanel();
		tabbedPane.addTab("SMS Delivery Status", null, panelDLR, null);

		JPanel panelDLRLog = new JPanel();
		panelDLRLog.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Log", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JList listDLR = new JList(dlrLogListModel);
		listDLR.setVisibleRowCount(-1);
		JScrollPane scrollPane_2 = new JScrollPane(listDLR);
		scrollPane_2.setViewportView(listDLR);

		JPanel panel_15 = new JPanel();
		panel_15.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Subscribe To Delivery Notifications", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_15.setLayout(null);

		JLabel label = new JLabel("Sender Address:");
		label.setBounds(16, 29, 101, 14);
		panel_15.add(label);

		txtDLRSenderAddress = new JTextField();
		txtDLRSenderAddress.setBounds(127, 26, 285, 20);
		txtDLRSenderAddress.setColumns(10);
		panel_15.add(txtDLRSenderAddress);

		JPanel panel_16 = new JPanel();
		panel_16.setBounds(10, 79, 405, 133);
		panel_16.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Optional", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_15.add(panel_16);

		JLabel label_14 = new JLabel("Client Correlator:");
		label_14.setBounds(10, 30, 101, 14);

		txtDLRClientCorrelator = new JTextField();
		txtDLRClientCorrelator.setBounds(124, 27, 270, 20);
		txtDLRClientCorrelator.setColumns(10);

		JLabel label_17 = new JLabel("Callback Data:");
		label_17.setBounds(10, 59, 91, 14);

		txtDLRCallbackData = new JTextField();
		txtDLRCallbackData.setBounds(124, 56, 270, 20);
		txtDLRCallbackData.setColumns(10);

		JLabel lblCriteria = new JLabel("Criteria:");
		lblCriteria.setBounds(10, 87, 91, 14);

		txtDLRCriteria = new JTextField();
		txtDLRCriteria.setBounds(124, 84, 270, 20);
		txtDLRCriteria.setColumns(10);
		panel_16.setLayout(null);
		panel_16.add(label_14);
		panel_16.add(txtDLRClientCorrelator);
		panel_16.add(label_17);
		panel_16.add(txtDLRCallbackData);
		panel_16.add(lblCriteria);
		panel_16.add(txtDLRCriteria);

		JLabel label_15 = new JLabel("Notify URL:");
		label_15.setBounds(16, 54, 101, 14);
		panel_15.add(label_15);

		txtDLRNotifiyUrl = new JTextField();
		txtDLRNotifiyUrl.setBounds(127, 54, 285, 20);
		panel_15.add(txtDLRNotifiyUrl);
		txtDLRNotifiyUrl.setColumns(10);

		JButton btnDLRSubscribe = new JButton("Subscribe");
		btnDLRSubscribe.setBounds(296, 214, 118, 23);
		btnDLRSubscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				subscribeToDeliveryNotifications();
			}
		});
		panel_15.add(btnDLRSubscribe);

		JPanel panel_17 = new JPanel();
		panel_17.setBorder(new TitledBorder(null, "Cancel Subscription", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_17.setLayout(null);

		JLabel lblSubscriptionId = new JLabel("Subscription Id:");
		lblSubscriptionId.setBounds(16, 79, 128, 14);
		panel_17.add(lblSubscriptionId);

		txtDLRSubscriptionId = new JTextField();
		txtDLRSubscriptionId.setBounds(154, 76, 200, 20);
		txtDLRSubscriptionId.setColumns(10);
		panel_17.add(txtDLRSubscriptionId);

		txtDLRSubscriptionId.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkDLRFields();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkDLRFields();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkDLRFields();
			}
		});

		JButton btnDLRCancelSubscription = new JButton("Cancel");
		btnDLRCancelSubscription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelDeliveryNotifications();
			}
		});
		btnDLRCancelSubscription.setBounds(267, 102, 89, 23);
		panel_17.add(btnDLRCancelSubscription);

		JLabel label_18 = new JLabel("OR...");
		label_18.setBounds(154, 53, 80, 14);
		panel_17.add(label_18);

		JLabel label_19 = new JLabel("Resource URL:");
		label_19.setBounds(16, 27, 128, 14);
		panel_17.add(label_19);

		txtDLRCancelResourceURL = new JTextField();
		txtDLRCancelResourceURL.setColumns(10);
		txtDLRCancelResourceURL.setBounds(154, 23, 200, 20);
		panel_17.add(txtDLRCancelResourceURL);

		txtDLRCancelResourceURL.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkDLRFields();	
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkDLRFields();	
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkDLRFields();	
			}
		});

		JPanel panel_19 = new JPanel();
		panel_19.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Query Delivery Report", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_19.setLayout(null);

		JLabel lblRequestId = new JLabel("Resource URL:");
		lblRequestId.setBounds(16, 24, 125, 14);
		panel_19.add(lblRequestId);

		txtDLRResourceUrl = new JTextField();
		txtDLRResourceUrl.setColumns(10);
		txtDLRResourceUrl.setBounds(151, 20, 205, 20);
		panel_19.add(txtDLRResourceUrl);

		txtDLRResourceUrl.getDocument().addDocumentListener(new DocumentListener() {		
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkDLRFields();		
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkDLRFields();				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkDLRFields();		
			}
		});

		JButton btnDLRQuery = new JButton("Query");
		btnDLRQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				querySMSDeliveryStatus();
			}
		});
		btnDLRQuery.setBounds(267, 118, 89, 23);
		panel_19.add(btnDLRQuery);

		JLabel lblOr = new JLabel("OR...");
		lblOr.setBounds(154, 47, 80, 14);
		panel_19.add(lblOr);

		JLabel lblResourceId = new JLabel("Request Id:");
		lblResourceId.setBounds(16, 97, 125, 14);
		panel_19.add(lblResourceId);

		txtDLRResourceId = new JTextField();
		txtDLRResourceId.setColumns(10);
		txtDLRResourceId.setBounds(151, 92, 205, 20);
		panel_19.add(txtDLRResourceId);

		txtDLRResourceId.getDocument().addDocumentListener(new DocumentListener() {	
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkDLRFields();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkDLRFields();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkDLRFields();
			}
		});

		JLabel label_1 = new JLabel("Sender Address:");
		label_1.setBounds(16, 72, 125, 14);
		panel_19.add(label_1);

		txtDLRQuerySenderAddress = new JTextField();
		txtDLRQuerySenderAddress.setColumns(10);
		txtDLRQuerySenderAddress.setBounds(151, 68, 205, 20);
		panel_19.add(txtDLRQuerySenderAddress);
		GroupLayout gl_panelDLR = new GroupLayout(panelDLR);
		gl_panelDLR.setHorizontalGroup(
				gl_panelDLR.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDLR.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_panelDLR.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelDLR.createSequentialGroup()
										.addComponent(panel_15, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE)
										.addGap(6)
										.addGroup(gl_panelDLR.createParallelGroup(Alignment.LEADING)
												.addComponent(panel_17, GroupLayout.PREFERRED_SIZE, 366, GroupLayout.PREFERRED_SIZE)
												.addComponent(panel_19, GroupLayout.PREFERRED_SIZE, 366, GroupLayout.PREFERRED_SIZE)))
												.addGroup(gl_panelDLR.createSequentialGroup()
														.addComponent(panelDLRLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addGap(2)))
														.addGap(8))
				);
		gl_panelDLR.setVerticalGroup(
				gl_panelDLR.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDLR.createSequentialGroup()
						.addGap(11)
						.addGroup(gl_panelDLR.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_15, GroupLayout.PREFERRED_SIZE, 298, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panelDLR.createSequentialGroup()
										.addComponent(panel_17, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
										.addGap(1)
										.addComponent(panel_19, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)))
										.addGap(3)
										.addComponent(panelDLRLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGap(11))
				);
		panelDLRLog.setLayout(new CardLayout(0, 0));
		panelDLRLog.add(scrollPane_2, "name_391641948882302");
		panelDLR.setLayout(gl_panelDLR);

		txtDLRQuerySenderAddress.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkDLRFields();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {			
				checkDLRFields();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkDLRFields();	
			}
		});

		JPanel panelMO = new JPanel();
		tabbedPane.addTab("Inbound SMS", null, panelMO, null);

		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new TitledBorder(null, "Subscribe To Message Notifications", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setLayout(null);

		JLabel lblDestinationAddress = new JLabel("Dest Address:");
		lblDestinationAddress.setBounds(16, 29, 101, 14);
		panel_7.add(lblDestinationAddress);

		txtMODestAddress = new JTextField();
		txtMODestAddress.setColumns(10);
		txtMODestAddress.setBounds(127, 26, 275, 20);
		panel_7.add(txtMODestAddress);

		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new TitledBorder(null, "Optional", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_13.setLayout(null);
		panel_13.setBounds(10, 79, 394, 162);
		panel_7.add(panel_13);

		JLabel label_3 = new JLabel("Client Correlator:");
		label_3.setBounds(10, 30, 101, 14);
		panel_13.add(label_3);

		txtMOClientCorrelator = new JTextField();
		txtMOClientCorrelator.setColumns(10);
		txtMOClientCorrelator.setBounds(111, 27, 269, 20);
		panel_13.add(txtMOClientCorrelator);

		JLabel label_12 = new JLabel("Callback Data:");
		label_12.setBounds(10, 59, 101, 14);
		panel_13.add(label_12);

		txtMOCallbackData = new JTextField();
		txtMOCallbackData.setColumns(10);
		txtMOCallbackData.setBounds(111, 56, 269, 20);
		panel_13.add(txtMOCallbackData);

		JLabel lblCriteria_1 = new JLabel("Criteria:");
		lblCriteria_1.setBounds(10, 90, 101, 14);
		panel_13.add(lblCriteria_1);

		txtMOCriteria = new JTextField();
		txtMOCriteria.setColumns(10);
		txtMOCriteria.setBounds(111, 87, 269, 20);
		panel_13.add(txtMOCriteria);

		JLabel lblNotiificationFormat = new JLabel("Notif Format:");
		lblNotiificationFormat.setBounds(10, 119, 91, 14);
		panel_13.add(lblNotiificationFormat);

		txtMONotifFormat = new JTextField();
		txtMONotifFormat.setColumns(10);
		txtMONotifFormat.setBounds(111, 116, 269, 20);
		panel_13.add(txtMONotifFormat);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(-37, 220, 794, 158);
		panel_13.add(panel_2);
		panel_2.setLayout(new CardLayout(0, 0));

		JLabel label_13 = new JLabel("Notify URL:");
		label_13.setBounds(16, 54, 70, 14);
		panel_7.add(label_13);

		txtMONotifyUrl = new JTextField();
		txtMONotifyUrl.setColumns(10);
		txtMONotifyUrl.setBounds(127, 54, 275, 20);
		panel_7.add(txtMONotifyUrl);

		JButton btnMOSubscribe = new JButton("Subscribe");
		btnMOSubscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				subscribeToReceiptNotifications();
			}
		});
		btnMOSubscribe.setBounds(284, 243, 118, 23);
		panel_7.add(btnMOSubscribe);

		JPanel panel_18 = new JPanel();
		panel_18.setBorder(new TitledBorder(null, "Cancel Subscription", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_18.setLayout(null);

		JLabel label_16 = new JLabel("Subscription Id:");
		label_16.setBounds(17, 72, 118, 14);
		panel_18.add(label_16);

		txtMOSubscriptionId = new JTextField();
		txtMOSubscriptionId.setColumns(10);
		txtMOSubscriptionId.setBounds(145, 72, 219, 20);
		panel_18.add(txtMOSubscriptionId);

		txtMOSubscriptionId.getDocument().addDocumentListener(new DocumentListener() {		
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkMOFields();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkMOFields();		
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkMOFields();			
			}
		});

		JButton btnMOCancelSubscription = new JButton("Cancel");
		btnMOCancelSubscription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelReceiptNotifications();
			}
		});
		btnMOCancelSubscription.setBounds(275, 98, 89, 23);
		panel_18.add(btnMOCancelSubscription);

		JLabel label_20 = new JLabel("Resource URL:");
		label_20.setBounds(17, 27, 118, 14);
		panel_18.add(label_20);

		txtMOCancelResourceURL = new JTextField();
		txtMOCancelResourceURL.setColumns(10);
		txtMOCancelResourceURL.setBounds(145, 27, 219, 20);
		panel_18.add(txtMOCancelResourceURL);

		txtMOCancelResourceURL.getDocument().addDocumentListener(new DocumentListener() {		
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkMOFields();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkMOFields();	
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkMOFields();	
			}
		});

		JLabel label_21 = new JLabel("OR...");
		label_21.setBounds(145, 53, 80, 14);
		panel_18.add(label_21);

		JPanel panelInboundLog = new JPanel();
		panelInboundLog.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Log", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JList listMO = new JList(moLogListModel);
		listMO.setVisibleRowCount(-1);
		JScrollPane scrollPane_3 = new JScrollPane(listMO);
		scrollPane_3.setViewportView(listMO);

		JPanel panel_20 = new JPanel();
		panel_20.setBorder(new TitledBorder(null, "Retrieve Inbound Messages", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_20.setLayout(null);

		JPanel panel_21 = new JPanel();
		panel_21.setBorder(new TitledBorder(null, "Mandatory", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_21.setBounds(10, 21, 354, 56);
		panel_20.add(panel_21);
		panel_21.setLayout(null);

		JLabel lblRegId = new JLabel("Registration Id:");
		lblRegId.setBounds(10, 23, 102, 14);
		panel_21.add(lblRegId);

		txtMORegistrationId = new JTextField();
		txtMORegistrationId.setBounds(115, 20, 229, 20);
		panel_21.add(txtMORegistrationId);
		txtMORegistrationId.setColumns(10);

		JButton btnMORetrieveInboundMessages = new JButton("Retrieve");
		btnMORetrieveInboundMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retrieveInboundMessages();
			}
		});
		btnMORetrieveInboundMessages.setBounds(275, 135, 89, 23);
		panel_20.add(btnMORetrieveInboundMessages);

		JPanel panel_22 = new JPanel();
		panel_22.setBorder(new TitledBorder(null, "Optional", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_22.setBounds(10, 77, 354, 56);
		panel_20.add(panel_22);
		panel_22.setLayout(null);

		JLabel lblMOMaxBatchSize = new JLabel("Max Batch Size:");
		lblMOMaxBatchSize.setBounds(10, 26, 105, 14);
		panel_22.add(lblMOMaxBatchSize);

		txtMOMaxBatchSize = new JTextField();
		txtMOMaxBatchSize.setBounds(115, 23, 229, 20);
		panel_22.add(txtMOMaxBatchSize);
		txtMOMaxBatchSize.setColumns(10);
		GroupLayout gl_panelMO = new GroupLayout(panelMO);
		gl_panelMO.setHorizontalGroup(
				gl_panelMO.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelMO.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_panelMO.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelMO.createSequentialGroup()
										.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE)
										.addGap(8)
										.addGroup(gl_panelMO.createParallelGroup(Alignment.LEADING)
												.addComponent(panel_18, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)
												.addComponent(panel_20, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)))
												.addComponent(panelInboundLog, GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE))
												.addGap(10))
				);
		gl_panelMO.setVerticalGroup(
				gl_panelMO.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelMO.createSequentialGroup()
						.addGap(11)
						.addGroup(gl_panelMO.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_7, GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panelMO.createSequentialGroup()
										.addComponent(panel_18, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
										.addGap(5)
										.addComponent(panel_20, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)))
										.addGap(7)
										.addComponent(panelInboundLog, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE))
				);
		panelInboundLog.setLayout(new CardLayout(0, 0));
		panelInboundLog.add(scrollPane_3, "name_743641949992302");
		panelMO.setLayout(gl_panelMO);

		JPanel panelSenderRegistration = new JPanel();
		tabbedPane.addTab("Sender Registration", null, panelSenderRegistration, null);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Log", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_5.setLayout(new CardLayout(0, 0));

		JList listRegistration = new JList(registerSenderLogListModel);
		listRegistration.setVisibleRowCount(-1);
		JScrollPane pane2 = new JScrollPane(listRegistration);		
		panel_5.add(pane2, "name_613671448272338");

		JPanel panel_10 = new JPanel();
		panel_10.setBorder(new TitledBorder(null, "Register Sender", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_10.setLayout(null);

		JLabel lblGsm = new JLabel("Gsm:");
		lblGsm.setEnabled(false);
		lblGsm.setBounds(10, 26, 70, 14);
		panel_10.add(lblGsm);

		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setEnabled(false);
		lblDescription.setBounds(10, 52, 70, 14);
		panel_10.add(lblDescription);

		txtRegisterDesc = new JTextField();
		txtRegisterDesc.setEnabled(false);
		txtRegisterDesc.setColumns(10);
		txtRegisterDesc.setBounds(85, 49, 332, 20);
		panel_10.add(txtRegisterDesc);

		txtRegisterGsm = new JTextField();
		txtRegisterGsm.setEnabled(false);
		txtRegisterGsm.setColumns(10);
		txtRegisterGsm.setBounds(85, 23, 332, 20);
		panel_10.add(txtRegisterGsm);

		JButton btnGetRegSenders = new JButton("Get Registered Senders");
		btnGetRegSenders.setEnabled(false);
		btnGetRegSenders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//DisplayRegisteredSenders();
			}
		});
		btnGetRegSenders.setActionCommand("getRegSenders");
		btnGetRegSenders.setBounds(576, 22, 196, 23);
		panel_10.add(btnGetRegSenders);

		JButton btnRegisterSender = new JButton("Register");
		btnRegisterSender.setEnabled(false);
		btnRegisterSender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//RegisterSender();
			}
		});
		btnRegisterSender.setActionCommand("registerSender");
		btnRegisterSender.setBounds(439, 22, 108, 23);
		panel_10.add(btnRegisterSender);

		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new TitledBorder(null, "Verifiy Sender", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_11.setLayout(null);

		JLabel label_2 = new JLabel("Gsm:");
		label_2.setEnabled(false);
		label_2.setBounds(10, 26, 70, 14);
		panel_11.add(label_2);

		JLabel lblPion = new JLabel("Pin:");
		lblPion.setEnabled(false);
		lblPion.setBounds(10, 52, 70, 14);
		panel_11.add(lblPion);

		txtVerifiyPin = new JTextField();
		txtVerifiyPin.setEnabled(false);
		txtVerifiyPin.setColumns(10);
		txtVerifiyPin.setBounds(85, 49, 332, 20);
		panel_11.add(txtVerifiyPin);

		txtVerifiyGsm = new JTextField();
		txtVerifiyGsm.setEnabled(false);
		txtVerifiyGsm.setColumns(10);
		txtVerifiyGsm.setBounds(85, 23, 332, 20);
		panel_11.add(txtVerifiyGsm);

		JButton btnVerifySender = new JButton("Verify");
		btnVerifySender.setEnabled(false);
		btnVerifySender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//VerifySender();
			}
		});
		btnVerifySender.setActionCommand("verifiySender");
		btnVerifySender.setBounds(439, 22, 105, 23);
		panel_11.add(btnVerifySender);
		GroupLayout gl_panelSenderRegistration = new GroupLayout(panelSenderRegistration);
		gl_panelSenderRegistration.setHorizontalGroup(
				gl_panelSenderRegistration.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSenderRegistration.createSequentialGroup()
						.addGap(10)
						.addGroup(gl_panelSenderRegistration.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_10, GroupLayout.PREFERRED_SIZE, 794, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_11, GroupLayout.PREFERRED_SIZE, 794, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE))
								.addGap(10))
				);
		gl_panelSenderRegistration.setVerticalGroup(
				gl_panelSenderRegistration.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSenderRegistration.createSequentialGroup()
						.addGap(11)
						.addComponent(panel_10, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
						.addGap(11)
						.addComponent(panel_11, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
						.addGap(24)
						.addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
						.addGap(11))
				);
		panelSenderRegistration.setLayout(gl_panelSenderRegistration);

		JPanel panelAccountInfo = new JPanel();
		tabbedPane.addTab("Account Info", null, panelAccountInfo, null);

		JButton btnShowCredits = new JButton("Show Available Credits");
		btnShowCredits.setEnabled(false);
		btnShowCredits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//DisplayAvailableCredits();
			}
		});
		btnShowCredits.setActionCommand("SendSMS");
		GroupLayout gl_panelAccountInfo = new GroupLayout(panelAccountInfo);
		gl_panelAccountInfo.setHorizontalGroup(
				gl_panelAccountInfo.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAccountInfo.createSequentialGroup()
						.addGap(37)
						.addComponent(btnShowCredits)
						.addContainerGap(544, Short.MAX_VALUE))
				);
		gl_panelAccountInfo.setVerticalGroup(
				gl_panelAccountInfo.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelAccountInfo.createSequentialGroup()
						.addGap(40)
						.addComponent(btnShowCredits)
						.addContainerGap(495, Short.MAX_VALUE))
				);
		panelAccountInfo.setLayout(gl_panelAccountInfo);
		JPanel panelConfiguration = new JPanel();
		tabbedPane.addTab("Config", null, panelConfiguration, null);
		panelConfiguration.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(10, 11, 546, 536);
		panelConfiguration.add(panel_1);
		panel_1.setLayout(null);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "OneAPI", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_3.setBounds(10, 110, 526, 222);
		panel_1.add(panel_3);
		panel_3.setLayout(null);

		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(20, 23, 65, 14);
		panel_3.add(lblUsername);

		JLabel label_5 = new JLabel("Password:");
		label_5.setBounds(20, 48, 65, 14);
		panel_3.add(label_5);

		txtConfigHttpUsername = new JTextField();
		txtConfigHttpUsername.setColumns(10);
		txtConfigHttpUsername.setBounds(191, 20, 325, 20);
		panel_3.add(txtConfigHttpUsername);

		txtConfigHttpPassword = new JTextField();
		txtConfigHttpPassword.setColumns(10);
		txtConfigHttpPassword.setBounds(191, 45, 325, 20);
		panel_3.add(txtConfigHttpPassword);

		JLabel lblRootMessagingUrl = new JLabel("Root Messaging Url:");
		lblRootMessagingUrl.setBounds(20, 76, 122, 14);
		panel_3.add(lblRootMessagingUrl);

		txtConfigRootMessagingUrl = new JTextField();
		txtConfigRootMessagingUrl.setColumns(10);
		txtConfigRootMessagingUrl.setBounds(191, 73, 325, 20);
		panel_3.add(txtConfigRootMessagingUrl);

		JLabel lbl34343 = new JLabel("Version:");
		lbl34343.setBounds(20, 104, 122, 14);
		panel_3.add(lbl34343);

		txtConfigVersionOneAPISMS = new JTextField();
		txtConfigVersionOneAPISMS.setColumns(10);
		txtConfigVersionOneAPISMS.setBounds(191, 101, 325, 20);
		panel_3.add(txtConfigVersionOneAPISMS);

		JLabel lblRetrieverRegistrationid = new JLabel("Inbound Retriever Reg Id:");
		lblRetrieverRegistrationid.setBounds(20, 132, 172, 14);
		panel_3.add(lblRetrieverRegistrationid);

		txtConfigRetrieverRegistrationID = new JTextField();
		txtConfigRetrieverRegistrationID.setColumns(10);
		txtConfigRetrieverRegistrationID.setBounds(191, 128, 325, 20);
		panel_3.add(txtConfigRetrieverRegistrationID);

		JLabel lblRetrievingInterval = new JLabel("Inbound Retrieving Interval:");
		lblRetrievingInterval.setBounds(20, 160, 172, 14);
		panel_3.add(lblRetrievingInterval);

		txtConfigInboundRetrievingInterval = new JTextField();
		txtConfigInboundRetrievingInterval.setColumns(10);
		txtConfigInboundRetrievingInterval.setBounds(191, 156, 325, 20);
		panel_3.add(txtConfigInboundRetrievingInterval);

		JLabel lblDlrInterval = new JLabel("DLR Retrieving Interval:");
		lblDlrInterval.setBounds(20, 189, 172, 14);
		panel_3.add(lblDlrInterval);

		txtConfigDLRRetrievingInterval = new JTextField();
		txtConfigDLRRetrievingInterval.setColumns(10);
		txtConfigDLRRetrievingInterval.setBounds(191, 185, 325, 20);
		panel_3.add(txtConfigDLRRetrievingInterval);

		JButton btnSaveConfig = new JButton("Save");
		btnSaveConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfiguration();			
			}
		});
		btnSaveConfig.setActionCommand("SaveConfig");
		btnSaveConfig.setBounds(435, 478, 101, 23);
		panel_1.add(btnSaveConfig);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sender Type", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setLayout(null);
		panel_4.setBounds(10, 11, 526, 88);
		panel_1.add(panel_4);

		rbSMPP = new JRadioButton("SMPP");
		rbSMPP.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!actionInProgress) {
					if (rbSMPP.isSelected()) {
						rbOneAPI.setSelected(false);
					} else {
						rbOneAPI.setSelected(true);
					}
				}
			}
		});
		rbSMPP.setBounds(179, 34, 85, 23);
		panel_4.add(rbSMPP);

		rbOneAPI = new JRadioButton("OneAPI");
		rbOneAPI.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (!actionInProgress) {
					if (rbOneAPI.isSelected()) {
						rbSMPP.setSelected(false);
					} else {
						rbSMPP.setSelected(true);
					}
				}
			}
		});
		rbOneAPI.setSelected(true);
		rbOneAPI.setBounds(56, 34, 91, 23);
		panel_4.add(rbOneAPI);

		JPanel panel_14 = new JPanel();
		panel_14.setBorder(new TitledBorder(null, "SMPP", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_14.setLayout(null);
		panel_14.setBounds(10, 343, 526, 124);
		panel_1.add(panel_14);

		JLabel label_22 = new JLabel("System Id:");
		label_22.setBounds(20, 21, 60, 14);
		panel_14.add(label_22);

		JLabel label_23 = new JLabel("Password:");
		label_23.setBounds(20, 46, 65, 14);
		panel_14.add(label_23);

		JLabel label_24 = new JLabel("Host:");
		label_24.setBounds(20, 71, 60, 14);
		panel_14.add(label_24);

		JLabel label_25 = new JLabel("Port:");
		label_25.setBounds(20, 96, 60, 14);
		panel_14.add(label_25);

		txtConfigSMPPSystemId = new JTextField();
		txtConfigSMPPSystemId.setColumns(10);
		txtConfigSMPPSystemId.setBounds(191, 15, 325, 20);
		panel_14.add(txtConfigSMPPSystemId);

		txtConfigSMPPPassword = new JTextField();
		txtConfigSMPPPassword.setColumns(10);
		txtConfigSMPPPassword.setBounds(191, 40, 325, 20);
		panel_14.add(txtConfigSMPPPassword);

		txtConfigSMPPHost = new JTextField();
		txtConfigSMPPHost.setColumns(10);
		txtConfigSMPPHost.setBounds(191, 65, 325, 20);
		panel_14.add(txtConfigSMPPHost);

		txtConfigSMPPPort = new JTextField();
		txtConfigSMPPPort.setColumns(10);
		txtConfigSMPPPort.setBounds(191, 90, 325, 20);
		panel_14.add(txtConfigSMPPPort);
		getContentPane().add(tabbedPane);

		JPanel panelQuerySentSMS = new JPanel();
		panelQuerySentSMS.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Query Sent SMS Delivery Status", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(null, "SMS Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setLayout(null);

		chkSendAsFlashNotif = new JCheckBox("Send as Flash Notification");
		chkSendAsFlashNotif.setToolTipText("Send as flash notification.");
		chkSendAsFlashNotif.setActionCommand("IsFlash");
		chkSendAsFlashNotif.setBounds(16, 16, 210, 23);
		panel_6.add(chkSendAsFlashNotif);

		chkSendBinary = new JCheckBox("Send Binary Message");
		chkSendBinary.setToolTipText("Message will be send as binary.");
		chkSendBinary.setActionCommand("IsBinary");
		chkSendBinary.setBounds(16, 36, 223, 23);
		panel_6.add(chkSendBinary);

		//Help data when using OneAPI simulator
				txtSMSSenderAddress.setText("tel:3855373346444");
				txtSMSRecipientAddress.setText("tel:38598434322");
				txtSMSMessageText.setText("Hello!");
				txtSMSClientCorrelator.setText("ref2781398");
				txtLBSAddress.setText("tel:38598434322;tel:385543543322");
				txtLBSRequestedAccuracy.setText("20");
				txtMORegistrationId.setText("regId32242");
				txtMOMaxBatchSize.setText("2");
				txtMODestAddress.setText("tel:3855373346444");
				txtMONotifyUrl.setText("http://www.test");
				txtDLRSenderAddress.setText("tel:3855373346444");
				txtDLRNotifiyUrl.setText("http://www.test");	

		GroupLayout gl_panelSendSMS = new GroupLayout(panelSendSMS);
		gl_panelSendSMS.setHorizontalGroup(
				gl_panelSendSMS.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendSMS.createSequentialGroup()
						.addGap(8)
						.addGroup(gl_panelSendSMS.createParallelGroup(Alignment.LEADING)
								.addComponent(panelSMSData, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
								.addComponent(panelLog, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE))
								.addGap(10)
								.addGroup(gl_panelSendSMS.createParallelGroup(Alignment.LEADING)
										.addComponent(panel_6, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
										.addComponent(panelQuerySentSMS, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(panelGeneralMessageSettings, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
										.addContainerGap())
				);
		gl_panelSendSMS.setVerticalGroup(
				gl_panelSendSMS.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelSendSMS.createSequentialGroup()
						.addGroup(gl_panelSendSMS.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelSendSMS.createSequentialGroup()
										.addGap(9)
										.addComponent(panelGeneralMessageSettings, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(panel_6, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
										.addGroup(gl_panelSendSMS.createSequentialGroup()
												.addGap(10)
												.addComponent(panelSMSData, GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)))
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addGroup(gl_panelSendSMS.createParallelGroup(Alignment.TRAILING)
														.addComponent(panelLog, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
														.addComponent(panelQuerySentSMS, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
														.addContainerGap())
				);

		JPanel panel_23 = new JPanel();
		panel_23.setLayout(new CardLayout(0, 0));

		JScrollPane scrollPane_4 = new JScrollPane((Component) null);
		panel_23.add(scrollPane_4, "name_118303183524506");

		listSentMessgesUrl = new JList(sentSMSUrlLogListModel);
		listSentMessgesUrl.setVisibleRowCount(-1);
		scrollPane_4.setViewportView(listSentMessgesUrl);

		btnQuerySentSMSDLR = new JButton("Query");
		btnQuerySentSMSDLR.setEnabled(false);
		btnQuerySentSMSDLR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				querySelUrl();
			}
		});

		btnClearSentSMSUrlList = new JButton("Clear");
		btnClearSentSMSUrlList.setEnabled(false);
		btnClearSentSMSUrlList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearUrlList();
			}
		});

		btnRemoveSentSMSDLR = new JButton("Remove");
		btnRemoveSentSMSDLR.setEnabled(false);
		btnRemoveSentSMSDLR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelUrl();
			}
		});
		GroupLayout gl_panelQuerySentSMS = new GroupLayout(panelQuerySentSMS);
		gl_panelQuerySentSMS.setHorizontalGroup(
				gl_panelQuerySentSMS.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelQuerySentSMS.createSequentialGroup()
						.addGap(4)
						.addComponent(panel_23, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
						.addGap(10)
						.addGroup(gl_panelQuerySentSMS.createParallelGroup(Alignment.LEADING)
								.addComponent(btnQuerySentSMSDLR, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnRemoveSentSMSDLR, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnClearSentSMSUrlList, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
								.addGap(4))
				);
		gl_panelQuerySentSMS.setVerticalGroup(
				gl_panelQuerySentSMS.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelQuerySentSMS.createSequentialGroup()
						.addGap(4)
						.addGroup(gl_panelQuerySentSMS.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_23, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
								.addGroup(gl_panelQuerySentSMS.createSequentialGroup()
										.addComponent(btnQuerySentSMSDLR)
										.addGap(11)
										.addComponent(btnRemoveSentSMSDLR)
										.addGap(2)
										.addComponent(btnClearSentSMSUrlList)))
										.addGap(7))
				);
		panelQuerySentSMS.setLayout(gl_panelQuerySentSMS);
		panelGeneralMessageSettings.setLayout(null);
		panelGeneralMessageSettings.add(label_4);
		panelGeneralMessageSettings.add(txtDataCoding);
		panelGeneralMessageSettings.add(txtEsmClass);
		panelGeneralMessageSettings.add(label_6);
		panelGeneralMessageSettings.add(label_7);
		panelGeneralMessageSettings.add(txtSourceTon);
		panelGeneralMessageSettings.add(label_8);
		panelGeneralMessageSettings.add(txtSourceNpi);
		panelGeneralMessageSettings.add(label_9);
		panelGeneralMessageSettings.add(label_10);
		panelGeneralMessageSettings.add(txtDestinationNpi);
		panelGeneralMessageSettings.add(txtDestinationTon);
		panelGeneralMessageSettings.add(label_11);
		panelGeneralMessageSettings.add(txtValidityPeriod);

		JLabel label_26 = new JLabel("Protocol Id:");
		label_26.setBounds(20, 214, 95, 14);
		panelGeneralMessageSettings.add(label_26);

		txtProtocolId = new JTextField();
		txtProtocolId.setColumns(10);
		txtProtocolId.setBounds(120, 208, 57, 20);
		panelGeneralMessageSettings.add(txtProtocolId);

		panelSendSMS.setLayout(gl_panelSendSMS);
	}

	protected void setClient() {
		try {	
			initProtocol();				
		} catch (Exception e) {
			smsLogListModel.addElement(e.getMessage());	
		}
	}

	protected void initProtocol() throws Exception {
		try {
			smsLogListModel.addElement("Initializing...");

			if (client != null) {
				client.destroy();
			}

			client = new SMSClient();	
			
			setControlsStatus();
			
			addDeliveryReportLisntener();
			addIncomingMessageLisntener();		

			smsLogListModel.removeElement("Initializing...");
			smsLogListModel.addElement("Successfully initialized.");

		} catch (Exception e) {
			smsLogListModel.removeElement("Initializing...");
			throw e;	
		}
	}
	
	protected void addDeliveryReportLisntener() throws DeliveryReportListenerException  {
		client.addDeliveryReportListener(new DeliveryReportListener() {

			@Override
			public void onDeliveryReportReceived(SMSSendDeliveryStatusResponse arg0, DLRType arg1) {
				if (arg1.equals(DLRType.hlr)) {
					hlrLogListModel.addElement("DLR: " + arg0.toString());	
				} else {
					smsLogListModel.addElement("DLR: " + arg0.toString());	
				}
			}
		});
	}

	protected void addIncomingMessageLisntener() throws InboundMessageListenerException{
		client.addInboundMessageListener(new InboundMessageListener() {		

			@Override
			public void onMessageRetrieved(RetrieveSMSResponse arg0) {		
				smsLogListModel.addElement("Inbound SMS: " + arg0.toString());		
			}
		});
	}

	private void sendSMS() {
		SMS sms = new SMS();
		sms.setSenderAddress(txtSMSSenderAddress.getText());
		this.addRecipients(sms);

		if (!chkSendBinary.isSelected()) {
			sms.setMessageText(txtSMSMessageText.getText());
		} else {
			sms.setMessageBinary(txtSMSMessageText.getText());
		}

		if (client.getSenderType().equals(SenderType.ONEAPI)) {
			sms.setClientCorrelator(txtSMSClientCorrelator.getText());
			sms.setNotifyURL(txtSMSNotifyURL.getText());
			sms.setSenderName(txtSMSSenderName.getText());
			sms.setCallbackData(txtSMSCallbackData.getText());

		} else if (client.getSenderType().equals(SenderType.SMPP)) {
			if (!txtDataCoding.getText().isEmpty()) {
				sms.setDatacoding((byte)Integer.parseInt(txtDataCoding.getText()));
			}

			if (!txtEsmClass.getText().isEmpty()) {
				sms.setEsmclass((byte)Integer.parseInt(txtEsmClass.getText()));
			}

			if (!txtSourceTon.getText().isEmpty()) {
				sms.setSrcton(Integer.parseInt(txtSourceTon.getText()));
			}

			if (!txtSourceNpi.getText().isEmpty()) {
				sms.setSrcnpi(Integer.parseInt(txtSourceNpi.getText()));
			}

			if (!txtDestinationTon.getText().isEmpty()) {
				sms.setDestton(Integer.parseInt(txtDestinationTon.getText()));
			}

			if (!txtDestinationNpi.getText().isEmpty()) {
				sms.setDestnpi(Integer.parseInt(txtDestinationNpi.getText()));
			}

			if (!txtValidityPeriod.getText().isEmpty()) {
				sms.setValidityPeriod(txtValidityPeriod.getText());
			}

			if (!txtProtocolId.getText().isEmpty()) {
				sms.setProtocolid(Integer.parseInt(txtProtocolId.getText()));
			}

			if (txtDataCoding.getText().trim().equals(8) && (chkSendBinary.isSelected() == false)) {
				sms.setEncodeUnicodeTextToBinary(true);
			}

			if (txtSourceTon.getText().isEmpty() || txtSourceNpi.getText().isEmpty()) {
				sms.setAutoResolveSrcTonAndNpiOptions(true);
			}

			if (txtDestinationTon.getText().isEmpty() || txtDestinationNpi.getText().isEmpty()) {
				sms.setAutoResolveDestTonAndNpiOptions(true);
			}

			if (chkSendAsFlashNotif.isSelected()) {
				sms.setSendAsFlashNotification(true);
			}
		}

		try {
			SMSSendResponse response = client.sendSMS(sms);
			smsLogListModel.addElement("Send SMS Response: " +  response.toString());
	
			if (response.getResourceReference() != null) {

				String url = response.getResourceReference().getResourceURL();
				if (url.contains("SendSMSService")) {
					url = url.replace("SendSMSService", "QuerySMSService");
				}

				if (!url.isEmpty()) { 
					sentSMSUrlLogListModel.addElement(url);
					listSentMessgesUrl.setSelectedIndex(sentSMSUrlLogListModel.size() - 1);
					setUrlLogButtonsStatus();
					
					try {	
						FileWriter fw = new FileWriter(SENT_SMS_URL_LIST_FILE_PATH, true); 
						fw.write(url.concat("\n"));//appends the string to the file 
						fw.close();					
						
					} catch (Exception e) {
						e.printStackTrace();
					} 		
				}
			}

		} catch (SendSmsException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Send Message",JOptionPane.ERROR_MESSAGE);
		}
	}	

	private void locateTerminal() {		
		try {

			LocationResponse response =null;

			String[] addresses = txtLBSAddress.getText().split(";");

			if (addresses.length > 1) {
				response = client.locateMultipleTerminals(addresses, Integer.parseInt(txtLBSRequestedAccuracy.getText()));
			} else {
				response = client.locateTerminal(txtLBSAddress.getText(), Integer.parseInt(txtLBSRequestedAccuracy.getText()));
			}

			StringWriter sw = new StringWriter();
			ObjectMapper mapper = new ObjectMapper();
			MappingJsonFactory jsonFactory = new MappingJsonFactory();

			JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
			mapper.writeValue(jsonGenerator, response);
			sw.close();

			String a = sw.getBuffer().toString();
			System.out.println(a);

			lbsLogListModel.addElement("Terminal Location: " + response.toString());

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Locate Terminal", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void querySMSDeliveryStatus() {
		try {

			SMSSendDeliveryStatusResponse response = null;

			if (!txtDLRResourceUrl.getText().trim().isEmpty()) {
				response = client.queryDeliveryStatusByUrl(txtDLRResourceUrl.getText());
			} else if (!txtDLRResourceId.getText().trim().isEmpty()) {
				response = client.queryDeliveryStatus(txtDLRQuerySenderAddress.getText(), txtDLRResourceId.getText());
			} else { 
				JOptionPane.showMessageDialog(this, "Please fill 'Resource Url' or ('Sender Address' and 'Request Id') fields.", "Query SMS Delivery Status",JOptionPane.ERROR_MESSAGE);	
				return;
			}	

			dlrLogListModel.addElement("DLR: " + response.toString());	

		} catch (QueryDeliveryStatusException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Query SMS Delivery Status",JOptionPane.ERROR_MESSAGE);
		}
	}

	private void subscribeToDeliveryNotifications() {		
		try {
			SMSDeliveryReceiptSubscriptionResponse response = client.subscribeToDeliveryNotifications(txtDLRSenderAddress.getText(), txtDLRNotifiyUrl.getText(), txtDLRCriteria.getText(), txtDLRClientCorrelator.getText(), txtDLRCallbackData.getText());
			dlrLogListModel.addElement("Subscription Response: " + response.toString());

		} catch (SubscribeToDeliveryNotificationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Subscribe To Delivery Notifications", JOptionPane.ERROR_MESSAGE);
		}		
	}

	private void cancelDeliveryNotifications() {		
		int response = 0;
		try {

			if (!txtDLRCancelResourceURL.getText().trim().isEmpty()) {			
				response = client.cancelDeliveryNotifications(txtDLRCancelResourceURL.getText());	
			} else if (!txtDLRSubscriptionId.getText().trim().isEmpty()) {
				response = client.cancelDeliveryNotifications(txtDLRSubscriptionId.getText());
			} else { 
				JOptionPane.showMessageDialog(this, "Please fill 'Resource Url' or 'Subscription Id' fields.", "Cancel Delivery Notifications",JOptionPane.ERROR_MESSAGE);	
				return;
			}	

			dlrLogListModel.addElement("Cancelation Response: " + String.valueOf(response));

		} catch (CancelDeliveryNotificationsException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Cancel Delivery Notifications", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void retrieveInboundMessages() {	
		try {

			RetrieveSMSResponse response = null;

			if (!txtMOMaxBatchSize.getText().trim().isEmpty()) {
				response = client.retrieveInboundMessages(txtMORegistrationId.getText(), Integer.parseInt(txtMOMaxBatchSize.getText()));
			} else {
				response =  client.retrieveInboundMessages(txtMORegistrationId.getText());
			}	

			moLogListModel.addElement("Inbound SMS: " + response.toString());	

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Retrieve Inbound Messages", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void subscribeToReceiptNotifications() {		
		try {
			SMSMessageReceiptSubscriptionResponse response = client.subscribeToReceiptNotifications(txtMODestAddress.getText(), txtMONotifyUrl.getText(), txtMOCriteria.getText(), txtMONotifFormat.getText(), txtMOClientCorrelator.getText(), txtMOCallbackData.getText());
			moLogListModel.addElement("Subscription Response: " + response.toString());

		} catch (SubscribeToReceiptNotificationsException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Subscribe To Receipt Notifications", JOptionPane.ERROR_MESSAGE);
		}		
	}

	private void cancelReceiptNotifications() {		
		int response = 0;

		try {
			if (!txtMOCancelResourceURL.getText().trim().isEmpty()) {			
				response = client.cancelReceiptNotificationsByUrl(txtMOCancelResourceURL.getText());

			} else if (!txtMOSubscriptionId.getText().trim().isEmpty()) {
				response = client.cancelReceiptNotifications(txtMOSubscriptionId.getText());

			} else { 
				JOptionPane.showMessageDialog(this, "Please fill 'Resource Url' or 'Subscription Id' fields.", "Cancel Delivery Notifications",JOptionPane.ERROR_MESSAGE);	
				return;
			}	

			moLogListModel.addElement("Cancelation Response: " + String.valueOf(response));

		} catch (CancelReceiptNotificationsException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Cancel Receipt Notifications", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void SendHLR() {
		String[] destinations = txtHLRAddress.getText().split(";");
		for (String destination : destinations) {
			try {
				SMSSendResponse response = client.sendHLRRequest(destination);
				hlrLogListModel.addElement("HLR Request Response: " + response.toString());

			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,e.getMessage(), "Send HLR",JOptionPane.ERROR_MESSAGE);
			}	
		}		
	}

	protected void loadConfiguration() {	
		try {
			MainConfig config = new MainConfig();
			config.loadFromConfigFile();

			if (config.getSenderType().equals(SenderType.ONEAPI)) {
				rbOneAPI.setSelected(true);
			} else if (config.getSenderType().equals(SenderType.SMPP)) {
				rbSMPP.setSelected(true);
			}

			txtConfigHttpUsername.setText(config.getOneAPI().getAuthorization().getUsername());
			txtConfigHttpPassword.setText(config.getOneAPI().getAuthorization().getPassword());
			txtConfigRootMessagingUrl.setText(config.getOneAPI().getSmsMessagingBaseUrl());
			txtConfigVersionOneAPISMS.setText(config.getOneAPI().getVersionOneAPISMS());
			txtConfigRetrieverRegistrationID.setText(config.getOneAPI().getRetrieveInboundMessagesRegistrationId());		
			txtConfigInboundRetrievingInterval.setText(String.valueOf(config.getOneAPI().getInboundMessagesRetrievingInterval()));
			txtConfigDLRRetrievingInterval.setText(String.valueOf(config.getOneAPI().getDlrRetrievingInterval()));

			txtConfigSMPPSystemId.setText(config.getSmpp().getSystemId());
			txtConfigSMPPPassword.setText(config.getSmpp().getPassword());
			txtConfigSMPPHost.setText(config.getSmpp().getHost());
			txtConfigSMPPPort.setText(String.valueOf(config.getSmpp().getPort()));

		} catch (ConfigException e) {
			JOptionPane.showMessageDialog(this, "Error occured while trying to load configuration file in the 'Config' tab.", "Load Config Data", JOptionPane.ERROR_MESSAGE);
		}	
	}

	protected void saveConfiguration() {		
		try {
			MainConfig config = new MainConfig();
			config.loadFromConfigFile();

			if (rbOneAPI.isSelected()) {
				config.setSenderType(SenderType.ONEAPI);
			} else if (rbSMPP.isSelected()) {
				config.setSenderType(SenderType.SMPP);
			}

			config.getOneAPI().getAuthorization().setUsername(txtConfigHttpUsername.getText());
			config.getOneAPI().getAuthorization().setPassword(txtConfigHttpPassword.getText());
			config.getOneAPI().setSmsMessagingBaseUrl(txtConfigRootMessagingUrl.getText());
			config.getOneAPI().setVersionOneAPISMS(txtConfigVersionOneAPISMS.getText());
			config.getOneAPI().setRetrieveInboundMessagesRegistrationId(txtConfigRetrieverRegistrationID.getText());

			if (txtConfigInboundRetrievingInterval.getText().trim().isEmpty()) {
				config.getOneAPI().setInboundMessagesRetrievingInterval(0);
			} else {	
				config.getOneAPI().setInboundMessagesRetrievingInterval(Integer.parseInt(txtConfigInboundRetrievingInterval.getText()));
			}

			if (txtConfigDLRRetrievingInterval.getText().trim().isEmpty()) {
				config.getOneAPI().setDlrRetrievingInterval(0);
			} else {	
				config.getOneAPI().setDlrRetrievingInterval(Integer.parseInt(txtConfigDLRRetrievingInterval.getText()));
			}

			config.getSmpp().setSystemId(txtConfigSMPPSystemId.getText());
			config.getSmpp().setPassword(txtConfigSMPPPassword.getText());
			config.getSmpp().setHost(txtConfigSMPPHost.getText());

			if (!txtConfigSMPPPort.getText().isEmpty()) {
				config.getSmpp().setPort(Integer.parseInt(txtConfigSMPPPort.getText()));
			} else {
				config.getSmpp().setPort(0);
			}

			config.saveToConfigFile();

		} catch (ConfigException e) {
			JOptionPane.showMessageDialog(this, "Error occured while trying to save configuration to file", "Save Config Data", JOptionPane.ERROR_MESSAGE);
		}

		setClient();	

		JOptionPane.showMessageDialog(this, "Data are successfully saved and applied.", "Save Config Data", JOptionPane.INFORMATION_MESSAGE);		
	}

	private void checkMOFields() {
		if (!txtMOCancelResourceURL.getText().trim().isEmpty()) {
			txtMOSubscriptionId.setEnabled(false);
		} else if (!txtMOSubscriptionId.getText().trim().isEmpty()) {
			txtMOCancelResourceURL.setEnabled(false);
		} else {
			txtMOSubscriptionId.setEnabled(true);
			txtMOCancelResourceURL.setEnabled(true);
		}
	}

	private void checkDLRFields() {
		if (!txtDLRResourceUrl.getText().trim().isEmpty()) {
			txtDLRResourceId.setEnabled(false);
			txtDLRQuerySenderAddress.setEnabled(false);
		} else if (!txtDLRQuerySenderAddress.getText().trim().isEmpty() || !txtDLRResourceId.getText().trim().isEmpty()) {
			txtDLRResourceUrl.setEnabled(false);
		} else {
			txtDLRResourceId.setEnabled(true);
			txtDLRQuerySenderAddress.setEnabled(true);
			txtDLRResourceUrl.setEnabled(true);
		}

		if (!txtDLRSubscriptionId.getText().trim().isEmpty()) {
			txtDLRCancelResourceURL.setEnabled(false);		
		} else if (!txtDLRCancelResourceURL.getText().trim().isEmpty()) {
			txtDLRSubscriptionId.setEnabled(false);
		} else {
			txtDLRSubscriptionId.setEnabled(true);
			txtDLRCancelResourceURL.setEnabled(true);
		}

	}

	private void checkSMSMessageText() {	
		if (client != null && client.isUnicode(txtSMSMessageText.getText())) {
			lblUnicode.setText("UNICODE->");  	
			txtDataCoding.setText("8");
		} else {
			lblUnicode.setText(""); 
			if (txtDataCoding.getText().equals("8")) {
				txtDataCoding.setText("");
			}
		}
	}

	private void addRecipients(SMS sms) {
		String[] recipients = txtSMSRecipientAddress.getText().split(";");
		for (String recipient : recipients) {
			sms.addRecipientAddress(recipient);
		}	
	}

	private void querySelUrl() {
		// Get the index of all the selected items
		int[] selectedIx = listSentMessgesUrl.getSelectedIndices();

		// Get all the selected items using the indices
		for (int i=0; i<selectedIx.length; i++) {   
			try {   	
				String selUrl = (String )listSentMessgesUrl.getModel().getElementAt(selectedIx[i]);

				SMSSendDeliveryStatusResponse response = client.queryDeliveryStatusByUrl(selUrl);
				smsLogListModel.addElement("DLR: " + response.toString());	

			} catch (QueryDeliveryStatusException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Query SMS Delivery Status",JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	private void removeSelUrl() {
		// Get the index of all the selected items
		Object[] selectedValues = listSentMessgesUrl.getSelectedValues();
			
		for (Object url : selectedValues) {
			String valueToRemove = (String)url;
			removeLineFromFile(valueToRemove);
			sentSMSUrlLogListModel.removeElement(valueToRemove);
		}
	
		this.setUrlLogButtonsStatus();
	}

	private void clearUrlList() {
		sentSMSUrlLogListModel.clear();
		setUrlLogButtonsStatus();
		
		File f=new File(SENT_SMS_URL_LIST_FILE_PATH);
		if(f.exists() && f.isFile()){
			f.delete();
		}
	}
	
	private void setControlsStatus() {
		boolean oneAPIEnabled = client.getSenderType().equals(SenderType.ONEAPI);
		
		if (oneAPIEnabled) {
			chkSendAsFlashNotif.setSelected(false);
			chkSendBinary.setSelected(false);
		}
		
		chkSendAsFlashNotif.setEnabled(!oneAPIEnabled);
		chkSendBinary.setEnabled(!oneAPIEnabled);	
						
		Component[] comOptionalSMSFields = panelOptionalSMSFields.getComponents();  
		for (int a = 0; a < comOptionalSMSFields.length; a++) {  
			comOptionalSMSFields[a].setEnabled(oneAPIEnabled);  
		} 
			
		Component[] comGeneralSettings = panelGeneralMessageSettings.getComponents();  
		for (int a = 0; a < comGeneralSettings.length; a++) {  
			comGeneralSettings[a].setEnabled(!oneAPIEnabled);  
		} 
			
		this.setUrlLogButtonsStatus();
	}

	private void setUrlLogButtonsStatus() {
		boolean oneAPIEnabled = client.getSenderType().equals(SenderType.ONEAPI);
		btnQuerySentSMSDLR.setEnabled(sentSMSUrlLogListModel.size() > 0 && oneAPIEnabled);
		btnClearSentSMSUrlList.setEnabled(sentSMSUrlLogListModel.size() > 0 && oneAPIEnabled);
		btnRemoveSentSMSDLR.setEnabled(sentSMSUrlLogListModel.size() > 0 && oneAPIEnabled);	
		listSentMessgesUrl.setEnabled(sentSMSUrlLogListModel.size() > 0 && oneAPIEnabled);
	}
	
	private void loadSentSMSUrlListFile() {
		if (!(new File(SENT_SMS_URL_LIST_FILE_PATH)).exists()) return; 

		try {
			BufferedReader br = new BufferedReader(new FileReader(SENT_SMS_URL_LIST_FILE_PATH));  
			String line = null;

			//Read from the original file and write to the new 
			//unless content matches data to be removed.
			while ((line = br.readLine()) != null) {
				sentSMSUrlLogListModel.addElement(line);
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeLineFromFile(String lineToRemove) {

	    try {

	      File inFile = new File(SENT_SMS_URL_LIST_FILE_PATH);
	      
	      if (!inFile.isFile()) {
	        System.out.println("Parameter is not an existing file");
	        return;
	      }
	       
	      //Construct the new file that will later be renamed to the original filename. 
	      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
	      
	      BufferedReader br = new BufferedReader(new FileReader(SENT_SMS_URL_LIST_FILE_PATH));
	      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
	      
	      String line = null;

	      //Read from the original file and write to the new 
	      //unless content matches data to be removed.
	      while ((line = br.readLine()) != null) {
	        
	        if (!line.trim().equals(lineToRemove)) {

	          pw.println(line);
	          pw.flush();
	        }
	      }
	      pw.close();
	      br.close();
	      
	      //Delete the original file
	      if (!inFile.delete()) {
	        System.out.println("Could not delete file");
	        return;
	      } 
	      
	      //Rename the new file to the filename the original file had.
	      if (!tempFile.renameTo(inFile))
	        System.out.println("Could not rename file");
	      
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex) {
	      ex.printStackTrace();
	    }
	  }
}
