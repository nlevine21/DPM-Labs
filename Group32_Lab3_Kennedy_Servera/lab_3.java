package lab_3;

import lab_3.Odometer;
import lab_3.OdometryDisplay;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lab_3.navigation;
import lejos.hardware.sensor.EV3UltrasonicSensor;



public class lab_3 {

// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.4;
	private static final EV3UltrasonicSensor USSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));

// Static Resources:
//
// Ultrasonic sensor connected to input port S1
// Left motor connected to output A
// Right motor connected to output D
// Ultrasonic sensor motor connected to output C
		 	
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor USSensorMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));

	public static void main(String[] args) {
	int buttonChoice;

	final TextLCD t = LocalEV3.get().getTextLCD();
	Odometer odometer = new Odometer(leftMotor, rightMotor); // new instance of odometer 
	OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t); // new instance of odometer display 
	navigation drivePath = new navigation(leftMotor,rightMotor,USSensorMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer,USSensor,1); // new instance of the navigation class (the one without obstacles)
	navigation drive = new navigation(leftMotor,rightMotor,USSensorMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer,USSensor,2); // new instance of the navigation class (the one with obstacles)

	do {
		// clear the display
		t.clear();

		// ask the user whether the motors should drive in a square or float
		t.drawString("< Left | Right >", 0, 0);
		t.drawString("       |        ", 0, 1);
		t.drawString(" Run   | Avoid  ", 0, 2);
		t.drawString("Coarse | Block  ", 0, 3);
		t.drawString("       |        ", 0, 4);

		buttonChoice = Button.waitForAnyPress();
	} while (buttonChoice != Button.ID_LEFT
			&& buttonChoice != Button.ID_RIGHT);

	if (buttonChoice == Button.ID_LEFT) { //runs the path without the obstacle
		
		odometer.start();
		odometryDisplay.start();
		drivePath.start();
		
		
	} else { // runs the path with the obstacle 
		odometer.start();
		odometryDisplay.start();
		drive.start();
	}
	
	while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	System.exit(0);
}
}
