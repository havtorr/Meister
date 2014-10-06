package SimpleJFXDemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SimpleJFXDemo extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		
		Pane root = new Pane();
		
		Scene scene = new Scene(root, 500, 500);
		
		arg0.setScene(scene);
		arg0.setTitle("SimpleJFXDemo");
		arg0.show();
	}
	
	public static void main(String[] args) {
		launch(SimpleJFXDemo.class, args);
		
	}

}
