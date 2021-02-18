package Auditoria;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class dataBaseView extends JFrame {

	private JPanel contentPane;
	private JTextField txtHolaMundo;
	public static String server="";
	public static String password="";
	public static String opcionbd = "";
	private final JTextField txtLocalhost = new JTextField();
	private final JTextField txtSa = new JTextField();
	private final JPasswordField passwordField = new JPasswordField();
	private final JLabel lblServer = new JLabel("Server:");
	private final JLabel lblUser = new JLabel("User:");
	private final JLabel lblPassword = new JLabel("Password:");
	

	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					dataBaseView frame = new dataBaseView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public dataBaseView() {
		txtSa.setEditable(false);
		txtSa.setText("sa");
		txtSa.setBounds(189, 30, 28, 20);
		txtSa.setColumns(10);
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(66, 30, 86, 20);
		txtLocalhost.setColumns(10);

		initGUI();
	}
	private void initGUI() {
		
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
				txtHolaMundo = new JTextField();
		txtHolaMundo.setText("Integridad Referencial");
		txtHolaMundo.setBounds(155, 11, 137, 20);
		contentPane.add(txtHolaMundo);
		txtHolaMundo.setColumns(10);
		txtHolaMundo.setEditable(false);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(227, 61, 155, 22);
		contentPane.add(comboBox);
		
				JButton btnSeleccionarBD = new JButton("Seleccionar");
		btnSeleccionarBD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				tipoAuditoria auditView = new tipoAuditoria();
				auditView.setVisible(true);
				dispose();
				
				
				
				opcionbd = (String) comboBox.getSelectedItem();
				System.out.print("funciona"+ opcionbd);

			}
			
		});
		btnSeleccionarBD.setBounds(302, 203, 89, 23);
		contentPane.add(btnSeleccionarBD);
		
		
				JButton btnCargarDB = new JButton("CargarDB");
		btnCargarDB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				server =  txtLocalhost.getText();
				password = passwordField.getText();
				String[] opcionesDB = new String[10];
				
				String connectionUrl = "jdbc:sqlserver://"+server+";" + "database=master;" + "user=sa;"
						+ "password="+password+";";

				try (Connection connection = DriverManager.getConnection(connectionUrl);
						Statement statement = connection.createStatement();) {
					opcionesDB = buscarBases(connection);
					
					for (String stringdb : opcionesDB) {
						comboBox.addItem(stringdb);
						
					}

				} catch (SQLException x) {
					x.printStackTrace();
					System.out.print("No funciona");
					JOptionPane.showMessageDialog(null, "Error al conectar", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		btnCargarDB.setBounds(203, 203, 89, 23);
		contentPane.add(btnCargarDB);
		
				JTextArea textArea = new JTextArea();
		textArea.setBounds(30, 61, 147, 165);
		contentPane.add(textArea);
		textArea.setEditable(false);
		
		contentPane.add(txtLocalhost);
		
		contentPane.add(txtSa);
		passwordField.setBounds(293, 30, 89, 20);
		
		contentPane.add(passwordField);
		lblServer.setBounds(22, 33, 46, 14);
		
		contentPane.add(lblServer);
		lblUser.setBounds(155, 33, 46, 14);
		
		contentPane.add(lblUser);
		lblPassword.setBounds(227, 33, 57, 14);
		
		contentPane.add(lblPassword);
	}

	public void seleccionBD(Connection connection) throws SQLException {

	}

	public String[] buscarBases(Connection connection) throws SQLException {

		String[] opcionesDB = new String[10];
		
		int aux = 0;
		JTextArea textArea = new JTextArea();
		textArea.setBounds(30, 61, 147, 165);
		contentPane.add(textArea);
		textArea.setEditable(false);

		String bases = "";
		String query = "SELECT name FROM master.dbo.sysdatabases";
		Statement stm = connection.createStatement();
		ResultSet rs = stm.executeQuery(query);
		while (rs.next()) {
			bases += rs.getString(1) + "\n";
			opcionesDB[aux] = rs.getString(1);
			aux++;
		}
		textArea.setText(bases);
		;

		System.out.println(bases);
		System.out.println(opcionesDB[1]);
		
		return opcionesDB;

	}
}
