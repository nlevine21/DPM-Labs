package WallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 510, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {

		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		//
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}
		
		
		this.distance = distance;
		if (distance <= 22 ){ //if the distance is under 22 cm, rotate clockwise
			int speed = -1/20 * distance -300; //inversely proportionate speed function 
			leftMotor.setSpeed(speed);
			rightMotor.setSpeed(300);
			leftMotor.forward();
			rightMotor.backward();
		}else{ 
		if (distance >= 40) { //if the distance read is great, set the max distance 40 
			distance = 40;
		}
		int speed = 20 *distance - 300; // function for speed if distance is above 22 cm (function generated from two
										// point, 0 speed at 15 cm and 300 speed at 30cm (set Band Center)
		leftMotor.setSpeed(300);        
		rightMotor.setSpeed(speed);
		leftMotor.forward();
		rightMotor.forward();
		}
	}
	

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
