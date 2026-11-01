package com.DarkBlue.GUI;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox{
	public static void display(final String a_title, final String a_message, final Alert.AlertType a_alertType){
		final Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(a_title);
		window.setMinWidth(250);
		
		final Label label = new Label(a_message);
		
		final Button closeButton = new Button("Close");
		closeButton.setOnAction(_ -> window.close());
		
		final VBox layout = new VBox(10);
		
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		final Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}
}
