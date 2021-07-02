package applicationRunner.control;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
//><
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ControlleurRunner implements Initializable{
		private Timeline gameLoop;
		private Timeline gameLoopSaut;
		@FXML
		private Label lifeLabel;
		@FXML
		private Label distancePane;
		@FXML
		private Label finalScore;
		@FXML
		private Pane runnerPane;
		@FXML
		private Pane obstaclePane;
		@FXML
		private Pane theEndPane;
		@FXML
		private Pane fonddufond;//2eme plan
	    @FXML
	    private Pane premierPlanFond;
	    @FXML
	    private Pane thirdPlanFond;
		@FXML   
		private int temps;
		@FXML
		private ImageView runningMan;
		private static int InterApparition = 30;
		private IntegerProperty distance = new SimpleIntegerProperty(0);
		private IntegerProperty life = new SimpleIntegerProperty(5);
	    
	    public static ArrayList<Image> imgList = new ArrayList<>();
        private void imgListMaker() {
        	imgList.add(new Image("applicationRunner/control/1.png"));
        	imgList.add(new Image("applicationRunner/control/2.png"));
        	imgList.add(new Image("applicationRunner/control/3.png"));
        	imgList.add(new Image("applicationRunner/control/4.png"));
        	imgList.add(new Image("applicationRunner/control/5.png"));
        	imgList.add(new Image("applicationRunner/control/6.png"));
        	imgList.add(new Image("applicationRunner/control/7.png"));
        	imgList.add(new Image("applicationRunner/control/8.png"));
        	imgList.add(new Image("applicationRunner/control/9.png"));
        	imgList.add(new Image("applicationRunner/control/10.png"));
        	imgList.add(new Image("applicationRunner/control/11.png"));
        	imgList.add(new Image("applicationRunner/control/12.png"));
        }
        
	    public void initialize(URL location, ResourceBundle resources) {
	    	playMousic();
	    	imgListMaker();
	    	theEndPane.setVisible(false);
	    	
	    	finalScore.textProperty().bind(distance.asString());
	    	lifeLabel.textProperty().bind(life.asString());
	    	distancePane.textProperty().bind(distance.asString());
	    	
	    	runnerPane.setOnMouseClicked(e->{	
	    		//la fonction est en l'air est de trop atm mais sera utile quand on aura des double sauts apres.
	    		if(!estEnlAir())
	    			sauterv2();
	    		});
	    	initAnimation();
			gameLoop.play();			
	    }
	    
	    MediaPlayer player;
	    private void playMousic() {
	    	String s = "src/applicationRunner/control/music.wav";
	    	Media h = new Media(Paths.get(s).toUri().toString());
	        player = new MediaPlayer(h); 
	        player.play();
	    }
	    
	    private void initAnimation() {
			gameLoop = new Timeline();
			temps=0;
			gameLoop.setCycleCount(Timeline.INDEFINITE);
			
			KeyFrame kf = new KeyFrame(
					Duration.seconds(0.05), 
					(ev ->{
					
					collisionsManager();
					renderRunner((temps%12));
					defilementFondManager();
					calculPointManager();
					spawnManager(temps);		
					if (life.getValue()== 0) 
						theEnd();	
					temps++;
					})
					);
			gameLoop.getKeyFrames().add(kf);
		}
	    
	    private void collisionsManager() {
	    	for (int i = obstaclePane.getChildren().size()-1; i>=0 ; i--) {
				obstaclePane.getChildren().get(i).setLayoutX(obstaclePane.getChildren().get(i).getLayoutX()-35);
				if (obstaclePane.getChildren().get(i).getLayoutX()<-50)
					obstaclePane.getChildren().remove(i);
				if (estTouche(obstaclePane.getChildren().get(i)))
					life.setValue(life.getValue()-1);
			}
	    }
	    private void spawnManager(int temps) {
	    	if (temps%InterApparition==0){
				apparitionObjet();
			}
			if (temps % 300== 0 && InterApparition>15) 
				InterApparition = InterApparition -5;
	    }
	    private void defilementFondManager() {
	    	fonddufond.setLayoutX(fonddufond.getLayoutX()-5);
			if (fonddufond.getLayoutX()==-1200)
				fonddufond.setLayoutX(0);
			premierPlanFond.setLayoutX(premierPlanFond.getLayoutX()-15);
			if (premierPlanFond.getLayoutX()<=-1200)
				premierPlanFond.setLayoutX(0);
			thirdPlanFond.setLayoutX(thirdPlanFond.getLayoutX()-2);
			if (thirdPlanFond.getLayoutX()<=-1200)
				thirdPlanFond.setLayoutX(0);
	    }
	    private void calculPointManager() {
	    	distance.setValue(distance.getValue()+1);
	    }
        private void renderRunner(int time) {   
        	runningMan.setImage(imgList.get(time));
		}
		private void theEnd() {
			theEndPane.setVisible(true);
			gameLoop.stop();
		}
		private void apparitionObjet() {
			Rectangle objet = new Rectangle(20,20);
			objet.setFill(Color.GRAY);
			objet.setStroke(Color.RED);
			objet.setLayoutX(1220);
			double x = Math.random();
			if (x<0.33)
				objet.setLayoutY(350);
			else if (x<0.63)
				objet.setLayoutY(445);
			else
				objet.setLayoutY(490);
			obstaclePane.getChildren().add(objet);
		}
		
		
		static int gravityG;
		private void sauterv2() {
			gravityG = 18;// a augmenter pour sauter plus haut.
			gameLoopSaut = new Timeline();
			gameLoopSaut.setCycleCount(Timeline.INDEFINITE);
			
			KeyFrame kfsaut = new KeyFrame(
					Duration.seconds(0.01), 
					(ev ->{
						if (runningMan.getLayoutY()<=400) {
							runningMan.setLayoutY(runningMan.getLayoutY()-gravityG);
							gravityG-=1;
						}
						if(runningMan.getLayoutY()>400) {
							runningMan.setLayoutY(400);
							gameLoopSaut.stop();
						}
					})
					);
			gameLoopSaut.getKeyFrames().add(kfsaut);
			gameLoopSaut.play();
		}	
		
		private boolean estEnlAir() { return runningMan.getLayoutY()!=400; }          
		private boolean estTouche(Node g) {
	    	if(g.getLayoutX()<=runningMan.getLayoutX()+110
	    	&& g.getLayoutX()>=runningMan.getLayoutX()
	    	&& g.getLayoutY()<=runningMan.getLayoutY()+145
	    	&& g.getLayoutY()>=runningMan.getLayoutY()) {
	    		obstaclePane.getChildren().remove(g);
	    		return true;
	    	}
	    	return false;
	    }
}

