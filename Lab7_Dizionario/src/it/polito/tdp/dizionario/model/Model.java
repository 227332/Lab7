package it.polito.tdp.dizionario.model;


//import org.jgrapht.alg.FloydWarshallShortestPaths;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.dizionario.db.ParolaDAO;

public class Model {

	private int len = 0; //lunghezza delle parole che considero nel mio modello

	private List<Parola> dict; //lista di tutte le parole nel dizionario lunghe len

	//RICORDA: E' buona norma dichiarare un attributo sempre con l'interfaccia, se si può
	// e poi usare la classe specifica solo per istanziarlo
	private UndirectedGraph<Parola, DefaultEdge> graph;

	public int getLen() {
		return len;
	}

	public void caricaParole(int len) {
		if (len != this.len) { // altrimenti ce l'ho già, è la stessa len dell'ultima volta
			this.len = len;

			ParolaDAO dao = new ParolaDAO();
			this.dict = dao.searchByLength(len);
		}
	}

	public List<Parola> getDict() {
		return this.dict;
	}

	public void buildGraph() {

		graph = new SimpleGraph<Parola, DefaultEdge>(DefaultEdge.class);

		Graphs.addAllVertices(graph, this.dict);

		ParolaDAO dao = new ParolaDAO();

		//per ogni parola p1 nel dizionario cerco tutti i suoi vicini e per ogni suo vicino p2
		//aggiungo il rispettivo edge (p1,p2) nel grafo
		for (Parola p1 : this.dict) {

			List<Parola> paroleSimili = dao.paroleSimili(p1);

			for (Parola p2 : paroleSimili) {
				if (!p1.getNome().equals(p2.getNome()))
					graph.addEdge(p1, p2);
			}
		}

	}

	public List<Parola> getVicini(String s) {
		// find Parola from String
		Parola p = findParola(s);
		if (p == null)
			return null;

		return Graphs.neighborListOf(graph, p);
	}

	public List<Parola> getTutti(String s) {
		// find Parola from String
		Parola p = findParola(s);
		if (p == null)
			return null;

		//cercare tutti i nodi connessi con qualche path a p equivale ad effettuare una
		//visita in ampiezza del grafo partendo da p, perchè così visito tutta la componente
		//connessa in cui si trova p, andando dai nodi collegati con 1 edge a quelli con 2 edges
		// e poi 3 e così via... E questo è proprio quello che voglio!
		BreadthFirstIterator<Parola, DefaultEdge> bfs = new BreadthFirstIterator<>(graph, p);

		List<Parola> tutti = new ArrayList<>();
		while (bfs.hasNext()) {
			tutti.add(bfs.next());
		}

		return tutti;
	}

	//metodo che dice se due parole sono vicine ossia sono collegate tramite un edge
	private boolean simili(Parola p1, Parola p2) {
		int diffs = 0;//contatore delle lettere differenti nelle due parole

		String s1 = p1.getNome();
		String s2 = p2.getNome();

		if (s1.length() != s2.length()) {
			System.out.println("Something's wrong");
			return false;
		}

		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) != s2.charAt(i))
				diffs++;
		}

		if (diffs == 1)//ho 1 perchè due vicini differiscono esattamente di una lettera
			return true;

		return false;

	}

	private Parola findParola(String s) {

		// TODO optimize lookup with Hash or Tree
		for (Parola p : dict) {
			if (p.getNome().equals(s))
				return p;
		}

		return null;
	}

	/**
	 * Trova il cammino minimo tra le due parole specificate, usando l'algoritmo
	 * di Dijkstra.
	 * 
	 * @param s1
	 * @param s2
	 * @return elenco (ordinato) di vertici, oppure {@code null} se le parole
	 *         non sono valide, oppure se non esiste un cammino
	 */
	public List<Parola> getCammino(String s1, String s2) {
		Parola p1 = findParola(s1);
		Parola p2 = findParola(s2);

		if (p1 == null || p2 == null)
			return null;

		DijkstraShortestPath<Parola, DefaultEdge> dijkstra = new DijkstraShortestPath<Parola, DefaultEdge>(graph, p1,p2);

		GraphPath<Parola, DefaultEdge> path = dijkstra.getPath();
		
		//Siccome la documentazione non mi dice cosa restituisce getPathVertexList() nel caso
		//in cui l'argomento è null, faccio il seguente check:
		if (path == null)
			return null;

		return Graphs.getPathVertexList(path);

		/*CON Floyd-Wharshall ALGORITHM SI FACEVA COSI':
		 * FloydWarshallShortestPaths<Parola, DefaultEdge> floyd = new
		 * FloydWarshallShortestPaths<>(graph) ; 
		 * return Graphs.getPathVertexList(floyd.getShortestPath(p1, p2)) ;
		 */
	}

}
