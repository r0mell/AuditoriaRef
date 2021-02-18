package Auditoria;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class dataBaseView2 extends JFrame {

	private JPanel contentPane;

	public String result = "";
	private final JScrollPane scrollPane = new JScrollPane();
	

	/**
	 * Create the frame.
	 */
	public dataBaseView2(String res) {
		this.result = res;
		initGUI(result);
	}
	private void initGUI(String res) {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 563);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton = new JButton("Imprimir");
		contentPane.add(btnNewButton, BorderLayout.NORTH);
		btnNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewAction(evt);
            }
        });
		contentPane.add(scrollPane);
		
				JTextArea textArea = new JTextArea();
				scrollPane.setViewportView(textArea);
				textArea.setEditable(false);
				textArea.setText(res);
		
		
	}

    private void jMenuNewAction(java.awt.event.ActionEvent evt) {
    	saveChooser(this);
    }

	
	public void saveChooser(Component parent) { 
    	String ruta = "";
    	JFileChooser jFileCh = new JFileChooser();
    	int userSelection = jFileCh.showSaveDialog(parent);
    	if (userSelection == JFileChooser.APPROVE_OPTION) {
    		try {
                ruta = jFileCh.getSelectedFile().getAbsolutePath()+".txt";
                FileWriter fw = new FileWriter(ruta);
                fw.write(this.result);
                fw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    	}
    }
}
