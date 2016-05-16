package it.polito.tdp.dizionario.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.dizionario.model.Parola;

public class ParolaDAO {

	//metodo che restituisce la List di tutte le Parole presenti nel dizionario,
	//in ordine alfabetico (ossia nello stesso ordine con cui sono nel dizionario)
	public List<Parola> readAll() {
		Connection conn = DBConnect.getConnection();

		String sql = "SELECT id, nome FROM parola ORDER BY id ;";

		List<Parola> result = new ArrayList<>();

		PreparedStatement st;
		try {
			st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {

				Parola p = new Parola(res.getInt("id"), res.getString("nome"));

				result.add(p);
			}
			
			/*
			 * OSS: Nel caso in cui la connessione al DB avviene mediante la tecnica del
			 * Connection Pooling, bisogna modificae solo la classe DBConnect mentre tale classe
			 * DAO rimane come prima! Però ricorda che ora il metodo close() non chiude più la
			 * connessione in quanto il Connection Pool restituisce una connessione conn che
			 * con close() restituisce il DB al Pool invece di chiudere la connessione. Grazie
			 * a ciò non si perde tempo a dover ogni volta attivare la connessione al DB
			 */
			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	//metodo che restituisce la List di tutte le Parole lunghe length presenti nel dizionario,
	//in ordine alfabetico (ossia nello stesso ordine con cui sono nel dizionario)
	public List<Parola> searchByLength(int length) {
		Connection conn = DBConnect.getConnection();

		String sql = "SELECT id, nome FROM parola WHERE CHAR_LENGTH(nome) = ? ORDER BY id ;";

		List<Parola> result = new ArrayList<>();

		PreparedStatement st;
		try {
			st = conn.prepareStatement(sql);

			st.setInt(1, length);

			ResultSet res = st.executeQuery();

			while (res.next()) {

				Parola p = new Parola(res.getInt("id"), res.getString("nome"));

				result.add(p);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}


	public List<Parola> paroleSimili(Parola p) {

		List<Parola> risultato = new LinkedList<>();

		for (int i = 0; i < p.getNome().length(); i++) {
			risultato.addAll(paroleSimiliInPosizione(p, i));
		}

		return risultato;
	}

	/*
	 * Tale metodo trovo tutti i vicini della Parola p nel seguente modo: cerca prima tutte le
	 * le parole che differiscono da p solo nella prima lettera, poi quelle che differiscono 
	 * solo nella seconda lettera e così via...
	 * Per fare ciò RICORDA che in linguaggio SQL il trattino basso _ indica che al suo posto
	 * ci può andare qualsiasi lettera. Ecco perchè definisco una variabile pattern avente un 
	 * trattino basso, proprio perchè, ad esempio se ho la String "a_e" mi considera tutte le
	 * parole di tre lettere avente come prima lettera a,come terza lettera e e in mezzo ogni 
	 * altra possibile lettera
	 */
	private List<Parola> paroleSimiliInPosizione(Parola p, int pos) {

		/*
		 * Per rendere tale query più efficiente, va modificato il DB dizionario, aggiungendo una
		 * colonna contenente la lunghezza di ogni parola nella tabella Parola e mettendo un indice
		 * secondario su tale attributo (in Heidi puoi definire come indici PRIMARY, UNIQUE, KEY
		 * e nel nostro caso è KEY). Ecco perchè nella query metto "...where lun = ? ..."
		 * 
		 * io però non ho modificato il database dizionario perciò se faccio run tale 
		 * query mi dà errore
		 */
		String sql = "select id, nome from parola where lun = ? AND nome LIKE ?";

		/*
		 * pattern è una String uguale a p.getNome() ma con in posizione pos il trattino
		 * basso _ invece della lettera che ha p.getNome()
		 */
		String pattern = p.getNome().substring(0, pos) + "_" + 
				p.getNome().substring(pos + 1) ;

		Connection conn = DBConnect.getConnection() ;
		try {
			List<Parola> result = new LinkedList<>();

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, p.getNome().length());
			st.setString(2, pattern);
			
			ResultSet res = st.executeQuery() ;
			
			while (res.next()) {

				Parola p2 = new Parola(res.getInt("id"), res.getString("nome"));

				result.add(p2);
			}

			conn.close();
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
		
	}

}
