package Ball_Launch;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
  
public class Lab5 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Catapult motor connected to output C
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor catapultMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));


	
	public static void main(String[] args) {
		int buttonChoice = 0;
	
		// initiate launch 
		Launch lau = new Launch( leftMotor,  rightMotor, catapultMotor);
		
		// display that robot is ready to launch
		System.out.println("Ready");
		// until escape button is pressed call release every time the 
		// left, middle or right buttons are pressed
		while (buttonChoice != Button.ID_ESCAPE){
			buttonChoice = Button.waitForAnyPress();
			lau.release(buttonChoice);
		};
		System.exit(0);	
		
	}

}
