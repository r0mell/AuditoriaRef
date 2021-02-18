package Auditoria;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class tipoAuditoria extends JFrame {

	private JPanel contentPane;

	public tipoAuditoria() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Integridad referencial");
		lblNewLabel.setBounds(56, 81, 182, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Integridad referencial de datos");
		lblNewLabel_1.setBounds(56, 145, 228, 14);
		contentPane.add(lblNewLabel_1);

		JButton btnImprimir = new JButton("Imprimir");
		btnImprimir.setBounds(272, 207, 89, 23);
		contentPane.add(btnImprimir);

		JButton btnAuditar = new JButton("Auditar");
		btnAuditar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
		
				String result = "";
				//se trae una variable del frame principal con el nombre de la base que se quiere auditar
				String aux = dataBaseView.opcionbd;
				System.out.println(aux);

				String connectionUrl = "jdbc:sqlserver://"+dataBaseView.server+";" + "database=" + aux + ";" + "user=sa;"
						+ "password="+dataBaseView.password+";";

				try (Connection connection = DriverManager.getConnection(connectionUrl);
						Statement statement = connection.createStatement();) {

					Auditoria audir = new Auditoria();
					System.out.println(
							"***************************ANOMALIAS DE FOREIGN KEY:*************************************************");
					result +="***************************ANOMALIAS DE FOREIGN KEY:*************************************************\n";
					result += audir.getFks(connection)+"\n";
					System.out.println(
							"***************************ANOMALIAS EN TABLAS QUE NO EXISTE UNA RELACION DE FK:*************************************************");
					result += "***************************ANOMALIAS EN TABLAS QUE NO EXISTE UNA RELACION DE FK:*************************************************\n";
					result += audir.TestTwo(connection)+"\n";
					
					result += new Metodos(connection).searchNulls();
					dataBaseView2 frame = new dataBaseView2(result);
					frame.setVisible(true);

//					audir.imprimirLog(
//							"***************************ANOMALIAS DE FOREIGN KEY:*************************************************"
//									+ "\n"
//									+ "***************************ANOMALIAS EN TABLAS QUE NO EXISTE UNA RELACION DE FK:*************************************************");
//					
				} catch (SQLException x) {
					x.printStackTrace();
					System.out.print("No funciona");

				}

			}
		});
		btnAuditar.setBounds(92, 207, 89, 23);
		contentPane.add(btnAuditar);

		JLabel lblNewLabel_2 = new JLabel("Integridad refencial");
		lblNewLabel_2.setBounds(171, 28, 113, 14);
		contentPane.add(lblNewLabel_2);
	}
}
