package it.polito.tdp.dizionario;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.dizionario.model.Model;
import it.polito.tdp.dizionario.model.Parola;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DizionarioController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtLen;

	@FXML
	private TextField txtParola;
	
    @FXML
    private TextField txtParolaArrivo;


	@FXML
	private Button btnGeneraGrafo;

	@FXML
	private Button btnTrovaVicini;

	@FXML
	private Button btnTrovaTutti;
	
    @FXML
    private Button btnTrovaCammino;

	@FXML
	private TextArea txtResult;

	@FXML
	void doGeneraGrafo(ActionEvent event) {

		String lunS = txtLen.getText();

		Integer lun;
		try {
			lun = Integer.parseInt(lunS);
		} catch (NumberFormatException e) {
			txtResult.appendText("Formato errato: " + lunS + "\n");
			return;
		}

		model.caricaParole(lun);

		txtResult.appendText("Parole caricate: " + model.getDict().size() + "\n");

		model.buildGraph();

		btnTrovaVicini.setDisable(false);
		btnTrovaTutti.setDisable(false);
		btnTrovaCammino.setDisable(false);

	}

	@FXML
	void doTrovaTutti(ActionEvent event) {
		
		String s = txtParola.getText() ;
		
		if(s.length() != model.getLen()) {
			txtResult.appendText("Lunghezza parola errata!\n");
			return ;
		}
		
		List<Parola> tutti = model.getTutti(s) ;
		
		txtResult.appendText("***"+s+"*** ("+tutti.size()+" parole)\n");
		
		for(Parola p: tutti) {
			txtResult.appendText("\t"+p.getNome()+"\n");
		}


	}

	@FXML
	void doTrovaVicini(ActionEvent event) {
		
		String s = txtParola.getText() ;
		
		if(s.length() != model.getLen()) {
			txtResult.appendText("Lunghezza parola errata!\n");
			return ;
		}
		
		List<Parola> vicini = model.getVicini(s) ;
		if (vicini==null) {
			txtResult.appendText("Parola inesistente!\n");
			return ;
		}
		
		txtResult.appendText("***"+s+"*** ("+vicini.size()+" parole)\n");
		
		for(Parola p: vicini) {
			txtResult.appendText("\t"+p.getNome()+"\n");
		}

	}
	
    @FXML
    void doTrovaCammino(ActionEvent event) {

		String s1 = txtParola.getText() ;
		String s2 = txtParolaArrivo.getText() ;
    	
		if(s1.length() != model.getLen() || s2.length() != model.getLen() ) {
			txtResult.appendText("Lunghezza parola errata!\n");
			return ;
		}
		List<Parola> cammino = model.getCammino(s1, s2) ;

		txtResult.appendText("***"+s1+" - "+s2+"*** ("+cammino.size()+" passi)\n");
		
		for(Parola p: cammino) {
			txtResult.appendText("\t"+p.getNome()+"\n");
		}

    }

	@FXML
	void initialize() {
		assert txtLen != null : "fx:id=\"txtLen\" was not injected: check your FXML file 'Dizionario.fxml'.";
		assert txtParola != null : "fx:id=\"txtParola\" was not injected: check your FXML file 'Dizionario.fxml'.";
		assert btnGeneraGrafo != null : "fx:id=\"btnGeneraGrafo\" was not injected: check your FXML file 'Dizionario.fxml'.";
		assert btnTrovaVicini != null : "fx:id=\"btnTrovaVicini\" was not injected: check your FXML file 'Dizionario.fxml'.";
		assert btnTrovaTutti != null : "fx:id=\"btnTrovaTutti\" was not injected: check your FXML file 'Dizionario.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Dizionario.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}
}
