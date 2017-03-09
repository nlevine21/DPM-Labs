package Localization;


import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import Localization.Navigation;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	final static int FAST = 200, ROTATE = 100, ACCELERATION = 1000;
	private float[] lineAngles = new float[4];
	private final double d=8.2; //distance from lightsensor to center in cm
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData,EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		Navigation nav=new Navigation(odo);
		nav.travelTo(15.24,15.24 );
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		leftMotor.setSpeed(95);
		rightMotor.setSpeed(ROTATE);
		float[] sample={0};
		int num=0;
		//rotate robot clockwise until it has stored angles of four black lines
		while(true){
			leftMotor.forward();
			rightMotor.backward();
			colorSensor.fetchSample(sample, 0);
			//test for black line, store it in array if found
			if(sample[0]<0.24){
				Sound.beep();
				lineAngles[num]=(float) odo.getAng();
				num=num+1;
				if(num==4){
					stopMotors();
					break;}
				// make thread sleep to stop it from storing the angle twice while it hovers over black line
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// do trig to compute (0,0) and 0 degrees
		double y;
		double x;
		y=-1*d*Math.cos(((lineAngles[3]-lineAngles[1])/2/360*(2*Math.PI)));
		x=-1*d*Math.cos((lineAngles[2]-lineAngles[0])/2/360*(2*Math.PI));
		stopMotors();
		//set odometer values based on calculations, with some calibrations found from trial and error
		odo.setPosition(new double [] {x+3,y, odo.getAng()+10}, new boolean [] {true, true, true});
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//move robot to black cross 
		nav.travelTo(0,0);
		//rotate it to positive x axis
		nav.turnTo(0, true);
		odo.setPosition(new double [] {0,0, 0}, new boolean [] {true, true, true});		
	}
	//stop motors from rotating 
	private void stopMotors(){
		leftMotor.stop();
		rightMotor.stop();
	}

}
