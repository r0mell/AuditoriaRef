package Auditoria;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class Auditoria {

	/*public static void main(String[] args) {
		// TODO Auto-generated method stub

		String connectionUrl = "jdbc:sqlserver://ROMEL;" + "database=pubs;" + "user=sa;" + "password=Passw0rd;";

		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement = connection.createStatement();) {

			System.out.println("***************************ANOMALIAS DE FOREIGN KEY:*************************************************");
			getFks(connection);
			System.out.println("***************************ANOMALIAS EN TABLAS QUE NO EXISTE UNA RELACION DE FK:*************************************************");
			TestTwo(connection);

			imprimirLog(
					"***************************ANOMALIAS DE FOREIGN KEY:*************************************************"
							+ "\n"
							+ "***************************ANOMALIAS EN TABLAS QUE NO EXISTE UNA RELACION DE FK:*************************************************");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.print("No funciona");

		}
	}*/

	public static void getTriggerAnomalies(String dad, String column, String child, Connection connection)
			throws SQLException {

		getNotInfk(dad, column, child, connection);

	}

	public static void getTriggerEmtpyAnomalies(String dad, String column, String child, Connection connection)
			throws SQLException {

		getNullas(dad, column, connection);
	}

	public static void consultarIntegReferencial(Connection connection) throws SQLException {
//	        String query = "select schema_name(tab.schema_id) + '.' + tab.name as [table]," +
//	"col.column_id," +
//	"col.name as column_name," +
//	"fk.object_id as rel," +
//	"schema_name(pk_tab.schema_id) + '.' + pk_tab.name as primary_table," +
//	"pk_col.name as pk_column_name," +
//	"fk_cols.constraint_column_id as no," +
//	"fk.name as fk_constraint_name," +
//	"fk.is_disabled as deshabilitado" +
//	"from sys.tables tab" +
//	"inner join sys.columns col " +
//	"on col.object_id = tab.object_id" +
//	"left outer join sys.foreign_key_columns fk_cols" +
//	"on fk_cols.parent_object_id = tab.object_id" +
//	"and fk_cols.parent_column_id = col.column_id" +
//	"left outer join sys.foreign_keys fk" +
//	"on fk.object_id = fk_cols.constraint_object_id" +
//	"left outer join sys.tables pk_tab" +
//	"on pk_tab.object_id = fk_cols.referenced_object_id" +
//	"left outer join sys.columns pk_col" +
//	"on pk_col.column_id = fk_cols.referenced_column_id" +
//	"and pk_col.object_id = fk_cols.referenced_object_id" +
//	"where fk.object_id is not null" +
//	"order by schema_name(tab.schema_id) + '.' + tab.name," +
//	"col.column_id";
		// rs = stm.executeQuery("SELECT * FROM [dbo].[sales");
		String query = "SELECT * FROM [dbo].[titles]";
		Statement stm = connection.createStatement();
		ResultSet rs = stm.executeQuery(query);
		while (rs.next()) {
			System.out.println("Tabla: " + rs.getString("table"));
			System.out.println("Columna" + rs.getString("column_name"));
			System.out.println("FK constraint: " + rs.getString("fk_constraint_name"));
			System.out.println("Tabla padre: " + rs.getString("primary_table"));
			if (rs.getBoolean("deshabilitado")) {
				System.out.println("Constraint no chequeado: " + rs.getString("res"));
			} else {
				System.out.println("OK");
			}

		}
		generarLog(query, connection);
	}

	public static void generarLog(String query, Connection conn) throws SQLException {
		// sp_configure 'show advanced options', 1
		// CallableStatement cst = conn.prepareCall("{call sp_configure 'show advanced
		// options', 1}");
		CallableStatement cst = conn.prepareCall("{call sp_configure (?,?)}");
		cst.setString(1, "show advanced options");
		cst.setInt(2, 1);
		cst.execute();

		CallableStatement cst2 = conn.prepareCall("{call logIntegRefer }");

		cst2.execute();
	}

	public static String getFks(Connection conn) throws SQLException {
		String result = "";
		Statement statement = conn.createStatement();
		String selectFks = "SELECT  obj.name AS FK_NAME,\r\n" + "    sch.name AS [schema_name],\r\n"
				+ "    tab1.name AS [table],\r\n" + "    col1.name AS [column],\r\n"
				+ "    tab2.name AS [referenced_table],\r\n" + "    col2.name AS [referenced_column]\r\n"
				+ "FROM sys.foreign_key_columns fkc\r\n" + "INNER JOIN sys.objects obj\r\n"
				+ "    ON obj.object_id = fkc.constraint_object_id\r\n" + "INNER JOIN sys.tables tab1\r\n"
				+ "    ON tab1.object_id = fkc.parent_object_id\r\n" + "INNER JOIN sys.schemas sch\r\n"
				+ "    ON tab1.schema_id = sch.schema_id\r\n" + "INNER JOIN sys.columns col1\r\n"
				+ "    ON col1.column_id = parent_column_id AND col1.object_id = tab1.object_id\r\n"
				+ "INNER JOIN sys.tables tab2\r\n" + "    ON tab2.object_id = fkc.referenced_object_id\r\n"
				+ "INNER JOIN sys.columns col2\r\n"
				+ "    ON col2.column_id = referenced_column_id AND col2.object_id = tab2.object_id\r\n" + "";
		ResultSet resultSet = statement.executeQuery(selectFks);
		String dad;
		String column;
		String child;

		while (resultSet.next()) {
			dad = resultSet.getString("referenced_table");
			column = resultSet.getString("column");
			child = resultSet.getString("table");
			result += getNotInfk(dad, column, child, conn)+'\n';

		}
		return result;
	}

	public static String getNotInfk(String dad, String column, String child, Connection conn) throws SQLException {
		String resultado = "";
		Statement statement = conn.createStatement();

		String Query = "SELECT  child.*\r\n" + "FROM    " + child + " child\r\n" + "LEFT JOIN\r\n" + dad + " padre\r\n"
				+ "ON      padre." + column + " = child." + column + "\r\n" + "WHERE   padre." + column + " IS NULL";

		ResultSet resultSet = statement.executeQuery(Query);
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (resultSet.next()) {
			System.out.println(
					"\n\nAnomaila en la relación entre " + child + " y " + dad + " en el registro con Datos:\n");
			resultado +="\n\nAnomaila en la relación entre " + child + " y " + dad + " en el registro con Datos:\n";
			for (int i = 1; i <= columnCount; i++) {
				String name = rsmd.getColumnName(i);
				System.out.println(name + ":     " + resultSet.getString(name));
				resultado += name + ":     " + resultSet.getString(name)+"\n";

			}
			resultado +="No encontrado en el padre: " + resultSet.getString(column)+"\n";
			System.out.println("No encontrado en el padre: " + resultSet.getString(column));

		}
		return resultado;
	}

	public static void getNullas(String table, String column, Connection conn) throws SQLException {

		Statement statement = conn.createStatement();
		String query = "select * from " + table + " where " + column + " = Null or " + column + " = ''";

		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (resultSet.next()) {
			System.out.println("\n\nAnomaila en los Triggers en la tabla " + table + ":\n");
			for (int i = 1; i <= columnCount; i++) {
				String name = rsmd.getColumnName(i);
				System.out.println(name + ":     " + resultSet.getString(name));

			}

		}

	}
/*
	public static void Test(Connection conn) throws SQLException {

		Statement statement = conn.createStatement();
		Statement sizeS = conn.createStatement();
		Statement pksS = conn.createStatement();
		// String query = "SELECT \r\n" +
		// " t.name AS TableName,\r\n" +
		// " tr.name AS TriggerName \r\n" +
		// "FROM sys.triggers tr\r\n" +
		// "INNER JOIN sys.tables t ON t.object_id = tr.parent_id";
		String query = "select [name],[OBJECT_ID] from sys.tables\r\n" + "";
		ResultSet size = sizeS.executeQuery(query);
		ResultSet resultSetHijos = statement.executeQuery(query);

		int sizeInt = 0;
		while (size.next()) {
			sizeInt++;
		}
		ArrayList<String> arrayHijos = new ArrayList<String>();
		while (resultSetHijos.next()) {
			arrayHijos.add(resultSetHijos.getString(1));
		}

		// System.out.println("el array hijo es :"+arrayHijos.toString());
		// **************************************************************************
		String pksQuery = "SELECT  OBJECT_NAME(ic.OBJECT_ID) AS TableName,\r\n"
				+ "        COL_NAME(ic.OBJECT_ID,ic.column_id) AS ColumnName\r\n"
				+ "FROM    sys.indexes AS i INNER JOIN \r\n"
				+ "        sys.index_columns AS ic ON  i.OBJECT_ID = ic.OBJECT_ID\r\n"
				+ "                                AND i.index_id = ic.index_id\r\n" + "WHERE   i.is_primary_key = 1";

		ResultSet pksResult = pksS.executeQuery(pksQuery);

		ArrayList<ArrayList<String>> papaPk = new ArrayList<ArrayList<String>>();
		while (pksResult.next()) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(pksResult.getString(1));
			temp.add(pksResult.getString(2));

			papaPk.add(temp);

		}
		// System.out.println(papaPk.toString());

		// **************************************************************************
		ArrayList<ArrayList<String>> hijoPadreRelated = new ArrayList<ArrayList<String>>();

		for (int g = 0; g < papaPk.size(); g++) {
			int veces = 0;
			for (int h = 0; h < papaPk.size(); h++) {
				String aux = papaPk.get(g).get(0);

				if (papaPk.get(g).get(0).equals(papaPk.get(h).get(0))) {
					veces++;
				}

			}
			if (veces == 1) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(papaPk.get(g).get(0));
				temp.add(papaPk.get(g).get(1));
				hijoPadreRelated.add(temp);
			}

		}

		System.out.println("EL NUEVO EEEESSSSSSSSSSSS:" + hijoPadreRelated.toString());

		// **************************************************************************************

		System.out.println("El SIZE = " + sizeInt);

		for (int i = 0; i < sizeInt; i++) {
			Statement statementAux = conn.createStatement();
			String queryAux = "Select top 1 * from " + arrayHijos.get(i);
			System.out.println("Select top 1 * from " + arrayHijos.get(i));
			ResultSet resultSetAux = statementAux.executeQuery(queryAux);
			ResultSetMetaData rsmdAux = resultSetAux.getMetaData();
			int columnCount = rsmdAux.getColumnCount();
			// System.out.println("Columm"+columnCount);
			while (resultSetAux.next()) {

				for (int j = 1; j <= columnCount; j++) {
					String name = rsmdAux.getColumnName(j);
					// System.out.println(name);

					for (int k = 0; k < hijoPadreRelated.size(); k++) {
						if (arrayHijos.get(i).equals(hijoPadreRelated.get(k).get(0)) == false) {
							if (name.equals(hijoPadreRelated.get(k).get(1))) {
								System.out.println("Posible Relación: " + arrayHijos.get(i) + " y "
										+ papaPk.get(k).get(0) + " A TRAVES DE " + papaPk.get(k).get(1));

								getTriggerAnomalies(papaPk.get(k).get(0), papaPk.get(k).get(1), arrayHijos.get(i),
										conn);
								getTriggerEmtpyAnomalies(papaPk.get(k).get(0), papaPk.get(k).get(1), arrayHijos.get(i),
										conn);

							}

						}

					}

				}

			}

		}

	}

	*/
	
	//en esta funcion esta el codigo que envio david, el query es el mismo 
	public static boolean ExisteEnFk(String dad, String child, Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		Statement sizeS = conn.createStatement();
		String query = "select * from(\r\n" + "SELECT  obj.name AS FK_NAME,\r\n" + "    sch.name AS [schema_name],\r\n"
				+ "    tab1.name AS [table],\r\n" + "    col1.name AS [column],\r\n"
				+ "    tab2.name AS [referenced_table],\r\n" + "    col2.name AS [referenced_column]\r\n"
				+ "FROM sys.foreign_key_columns fkc\r\n" + "INNER JOIN sys.objects obj\r\n"
				+ "    ON obj.object_id = fkc.constraint_object_id\r\n" + "INNER JOIN sys.tables tab1\r\n"
				+ "    ON tab1.object_id = fkc.parent_object_id\r\n" + "INNER JOIN sys.schemas sch\r\n"
				+ "    ON tab1.schema_id = sch.schema_id\r\n" + "INNER JOIN sys.columns col1\r\n"
				+ "    ON col1.column_id = parent_column_id AND col1.object_id = tab1.object_id\r\n"
				+ "INNER JOIN sys.tables tab2\r\n" + "    ON tab2.object_id = fkc.referenced_object_id\r\n"
				+ "INNER JOIN sys.columns col2\r\n"
				+ "    ON col2.column_id = referenced_column_id AND col2.object_id = tab2.object_id\r\n"
				+ "	)  JH\r\n" + "	where JH.[table] = '" + child + "' and JH.[referenced_table] = '" + dad + "'";

		ResultSet size = sizeS.executeQuery(query);
		ResultSet resultSetHijos = statement.executeQuery(query);
		int sizeInt = 0;
		while (size.next()) {
			sizeInt++;
		}
		if (sizeInt == 0) {
			return false;
		} else {
			return true;
		}

	}

	public static String TestTwo(Connection conn) throws SQLException {
		String res = "";
		Statement statement = conn.createStatement();
		Statement sizeS = conn.createStatement();
		Statement pksS = conn.createStatement();
		String query = "SELECT \r\n" + " t.name AS TableName,\r\n" + " tr.name AS TriggerName  \r\n"
				+ "FROM sys.triggers tr\r\n" + "INNER JOIN sys.tables t ON t.object_id = tr.parent_id";
		// String query ="select [name],[OBJECT_ID] from sys.tables\r\n" +
		// "";
		ResultSet size = sizeS.executeQuery(query);
		ResultSet resultSetHijos = statement.executeQuery(query);

		int sizeInt = 0;
		while (size.next()) {
			sizeInt++;
		}
		ArrayList<String> arrayHijos = new ArrayList<String>();
		while (resultSetHijos.next()) {
			arrayHijos.add(resultSetHijos.getString(1));
		}

		// System.out.println("el array hijo es :"+arrayHijos.toString());
		// **************************************************************************
		String pksQuery = "SELECT  OBJECT_NAME(ic.OBJECT_ID) AS TableName,\r\n"
				+ "        COL_NAME(ic.OBJECT_ID,ic.column_id) AS ColumnName\r\n"
				+ "FROM    sys.indexes AS i INNER JOIN \r\n"
				+ "        sys.index_columns AS ic ON  i.OBJECT_ID = ic.OBJECT_ID\r\n"
				+ "                                AND i.index_id = ic.index_id\r\n" + "WHERE   i.is_primary_key = 1";

		ResultSet pksResult = pksS.executeQuery(pksQuery);

		ArrayList<ArrayList<String>> papaPk = new ArrayList<ArrayList<String>>();
		while (pksResult.next()) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(pksResult.getString(1));
			temp.add(pksResult.getString(2));

			papaPk.add(temp);

		}
		// System.out.println(papaPk.toString());

		ArrayList<ArrayList<String>> triggerTables = new ArrayList<ArrayList<String>>();
		// **************************************************************************************

		System.out.println("El SIZE = " + sizeInt);
		res += "El SIZE = " + sizeInt+"\n";
		for (int i = 0; i < sizeInt; i++) {
			Statement statementAux = conn.createStatement();
			String queryAux = "Select top 1 * from " + arrayHijos.get(i);
			System.out.println("\n\nTABLA " + arrayHijos.get(i));
			res += "\n\nTABLA " + arrayHijos.get(i)+"\n";
			ResultSet resultSetAux = statementAux.executeQuery(queryAux);
			ResultSetMetaData rsmdAux = resultSetAux.getMetaData();
			int columnCount = rsmdAux.getColumnCount();
			// System.out.println("Columm"+columnCount);
			while (resultSetAux.next()) {

				for (int j = 1; j <= columnCount; j++) {
					String name = rsmdAux.getColumnName(j);
					// System.out.println(name);

					for (int k = 0; k < papaPk.size(); k++) {
						if (arrayHijos.get(i).equals(papaPk.get(k).get(0)) == false) {
							if (name.equals(papaPk.get(k).get(1))) {
								System.out.print("Posible Relación: " + arrayHijos.get(i) + " y " + papaPk.get(k).get(0)
										+ " A TRAVES DE " + papaPk.get(k).get(1));
								res += "Posible Relación: " + arrayHijos.get(i) + " y " + papaPk.get(k).get(0)
										+ " A TRAVES DE " + papaPk.get(k).get(1)+"\n";
								if (ExisteEnFk(papaPk.get(k).get(0), arrayHijos.get(i), conn)) {
									System.out.print("  (EXISTE UNA RELACIÓN FK)");
									res += "  (EXISTE UNA RELACIÓN FK)\n";
								}

								getTriggerAnomalies(papaPk.get(k).get(0), papaPk.get(k).get(1), arrayHijos.get(i),
										conn);

								getTriggerEmtpyAnomalies(papaPk.get(k).get(0), papaPk.get(k).get(1), arrayHijos.get(i),
										conn);
							}
						}
					}
				}
			}
		}
		return res;
	}
	
	//esta funcion aun no imprime todo lo que sale de la consola solo crea un archivo .txt y añade los titulos 
	public static void imprimirLog(String logs) {

		try {
			LocalDate fecha = LocalDate.now();
			String fechaImprimir = String.valueOf(fecha);
			String ruta = "C:\\Users\\Romel\\Documents\\AppAuditoria\\log" + fechaImprimir + ".txt";
			File file = new File(ruta);

			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(logs);
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	
	
	
}
