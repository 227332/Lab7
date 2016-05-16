package it.polito.tdp.dizionario.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;


/*
 * A differenza delle altre volte, questa volta cerchiamo di ottimizzare i tempi di 
 * connessione al DB tramite la tecnica nota come CONNECTION POOLING
 */

public class DBConnect {
	
	private static final String jdbcURL = "jdbc:mysql://localhost/dizionario?user=root" ;
	
	/*
	 * Definisco una variabile static che mi dice se il Connection Pool è stato già 
	 * creato oppure no
	 */
	private static ComboPooledDataSource dataSource = null ;
	
	public static Connection getConnection() {
		
		Connection conn;
		try {
			
			if(dataSource==null) {
				// devo creare ed attivare il Connection Pool
				dataSource = new ComboPooledDataSource() ;
				dataSource.setJdbcUrl(jdbcURL);
			}
			
			return dataSource.getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
			//lancio un'eccezione
			throw new RuntimeException("Errore nella connessione", e) ;
		}
	}

}
