package jfxtras.labs.scene.layout.test;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import jfxtras.labs.scene.layout.CircularPane;
import jfxtras.labs.scene.layout.CircularPane.AnimationLayoutInfo;
import jfxtras.scene.layout.HBox;

public class CircularPaneTrail extends Application {

    public static void main(String[] args) {
        launch(args);       
    }

	@Override
	public void start(Stage stage) {
		
		HBox lHBox = new HBox(20);
		Paint lShowDebug = Color.GREEN;
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			lCircularPane.setStartAngle(360.0 / 12);
			//lCircularPane.setChildrenAreCircular(true);
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			final List<Label> labels = new ArrayList<>();
			final List<Circle> circles = new ArrayList<>();
			final AtomicReference<Circle> lastFocus = new AtomicReference<>();
			final AtomicBoolean isPM = new AtomicBoolean(false);
			for (int i = 0; i < 12; i++) {
				javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(10, Color.GRAY);
				circles.add(c);
				javafx.scene.control.Label t = new javafx.scene.control.Label("" + (i + 1));
				t.getStyleClass().add("time");
				labels.add(t);
				c.setOnMouseEntered( (event) -> {
					int lastFocusIdx = circles.indexOf(lastFocus.get());
					int currentIdx = circles.indexOf( event.getSource() );
					
					if ( (lastFocusIdx == 11 && currentIdx == 0)
					  || (lastFocusIdx == 0 && currentIdx == 11)
					   ) {
						isPM.set( !isPM.get() );
					}
					
					for (int j = 0; j < 12; j++) {
						labels.get(j).setText( "" + ( (j + 1) + (isPM.get() ? 12 : 0) ) );
					}
				});
				c.setOnMouseExited( (event) -> {
					lastFocus.set( (Circle)event.getSource() );
				});
				StackPane lStackPane = new StackPane();
				lStackPane.setId("" + i);
				lStackPane.getChildren().add(c);
				lStackPane.getChildren().add(t);
				lCircularPane.getChildren().add(lStackPane);
			}
			lHBox.add(lCircularPane);
		}
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			//lCircularPane.setChildrenAreCircular(true);
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			final List<Label> labels = new ArrayList<>();
			final List<Circle> circles = new ArrayList<>();
			final AtomicReference<Circle> lastFocus = new AtomicReference<>();
			final AtomicBoolean isPM = new AtomicBoolean(false);
			for (int i = 0; i < 12; i++) {
				javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(10, Color.GRAY);
				circles.add(c);
				javafx.scene.control.Label t = new javafx.scene.control.Label("" + (i * 5));
				t.getStyleClass().add("time");
				labels.add(t);
				StackPane lStackPane = new StackPane();
				lStackPane.getChildren().add(c);
				lStackPane.getChildren().add(t);
				lCircularPane.getChildren().add(lStackPane);
			}
			StackPane lStackPane = new StackPane();
			lStackPane.getChildren().add(lCircularPane);
			Label l = new Label("M");
			l.getStyleClass().add("center");
			lStackPane.getChildren().add(l);
			lHBox.add(lStackPane, new HBox.C().hgrow(Priority.ALWAYS));
		}
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			//lCircularPane.setChildrenAreCircular(true);
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			final List<Label> labels = new ArrayList<>();
			final List<Circle> circles = new ArrayList<>();
			final AtomicReference<Circle> lastFocus = new AtomicReference<>();
			final AtomicBoolean isPM = new AtomicBoolean(false);
			for (int i = 0; i < 12; i++) {
				javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(10, Color.GRAY);
				circles.add(c);
				javafx.scene.control.Label t = new javafx.scene.control.Label("" + (i * 5));
				t.getStyleClass().add("time");
				labels.add(t);
				StackPane lStackPane = new StackPane();
				lStackPane.getChildren().add(c);
				lStackPane.getChildren().add(t);
				lCircularPane.getChildren().add(lStackPane);
			}
			StackPane lStackPane = new StackPane();
			lStackPane.getChildren().add(lCircularPane);
			Label l = new Label("S");
			l.getStyleClass().add("center");
			lStackPane.getChildren().add(l);
			lHBox.add(lStackPane);
		}
		
		{
			CircularPane lCircularPane = new CircularPane().withId("XX");
			//lCircularPane.setStyle("-fx-border-color:black;");
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			for (int i = 0; i < 12; i++) {
				javafx.scene.control.Button b = new javafx.scene.control.Button("XX");
//				b.setStyle("-fx-padding:10px;");
				b.setStyle("-fx-margin:10px;");
				lCircularPane.getChildren().add(b);
			}
			lHBox.add(lCircularPane);
		}
		
		{
			CircularPane lCircularPane = new CircularPane();
			lCircularPane.setMinSize(200,  200);
			//lCircularPane.setStyle("-fx-border-color:black;");
			//lCircularPane.setChildrenAreCircular(true);
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			for (int i = 0; i < 8; i++) {
				javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(10);
				lCircularPane.getChildren().add(c);
			}
			lHBox.add(lCircularPane);
			
//			final Node n = lCircularPane.getChildren().get(0);				
//			n.layoutXProperty().addListener( (observable) -> {
//				System.out.println("layoutX=" + n.getLayoutX());
//			});		
//			n.layoutYProperty().addListener( (observable) -> {
//				System.out.println("layoutY=" + n.getLayoutY());
//			});		
			
		}
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			//lCircularPane.setChildrenAreCircular(true);
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			for (int i = 0; i < 8; i++) {
				javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(10);
				lCircularPane.getChildren().add(c);
			}
			lHBox.add(lCircularPane, new HBox.C().hgrow(Priority.ALWAYS));
		}
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			//lCircularPane.setChildrenAreCircular(true);
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			for (int i = 0; i < 8; i++) {
				javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(5 + i);
				lCircularPane.getChildren().add(c);
			}
			lHBox.add(lCircularPane);
		}

		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateOverTheArc);
			for (int i = 0; i < 8; i++) {
				javafx.scene.shape.Rectangle c = new javafx.scene.shape.Rectangle(5 + (2*i), 5 + (2*i));
				lCircularPane.getChildren().add(c);
			}
			lHBox.add(lCircularPane);
		}
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateFromTheOrigin);
			for (int i = 0; i < 8; i++) {
				javafx.scene.shape.Rectangle c = new javafx.scene.shape.Rectangle(5 + (2*i), 5 + (2*i));
				c.setRotate(45);
				lCircularPane.getChildren().add(c);
			}
			lHBox.add(lCircularPane);
		}
		
		
		{
			CircularPane lCircularPane = new CircularPane();
			//lCircularPane.setStyle("-fx-border-color:black;");
			lCircularPane.setShowDebug(lShowDebug);
			lCircularPane.setAnimationInterpolation(CircularPane::animateFromTheOrigin);
			for (int i = 0; i < 10; i++) {
				javafx.scene.shape.Rectangle c = new javafx.scene.shape.Rectangle(20, 20);
				c.setRotate(i * 10);
				lCircularPane.getChildren().add(c);
			}
			lHBox.add(lCircularPane);
		}
		
        // setup scene
		Scene scene = new Scene(lHBox);
		scene.getStylesheets().add(this.getClass().getName().replace(".", "/") + ".css");
		
        // create stage
        stage.setTitle(this.getClass().getSimpleName());
        stage.setScene(scene);
        stage.show();	
	}
	
}

	