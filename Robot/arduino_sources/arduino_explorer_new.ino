
#include <SoftwareSerial.h>
#include <NewPing.h>

#define TRIGGER_PIN  12  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     11  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

// NewPing setup of pins and maximum distance.
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); 
// Wait 50ms between pings (about 20 pings/sec). 29ms should be the shortest delay between pings.
#define US_MILLIS_TO_WAIT 50
// variable to keep the last runtime
unsigned long us_starttime = millis();

// Second Serial interface with (TX, RX) at the bluetooth module
SoftwareSerial btSerial(2,3); 

// Status LED
int LEDPin = 8;

// Pins for the h-bridge
const int motorIN1 = 4;    // H-bridge leg 1 (pin IN1, dir1 PinA)
const int motorIN2 = 5;    // H-bridge leg 2 (pin IN2, dir2 PinA)
const int speedMotorA = 9; // H-bridge EN_A
const int motorIN3 = 6;     // H-bridge leg 3 (pin IN3, dir1 PinB)
const int motorIN4 = 7;     // H-bridge leg 4 (pin IN4, dir2 PinB)
const int speedMotorB = 10; // H-bridge EN_B

//steerSpeed and driveSpeed init
int driveSpeed = 0;
int steerSpeed = 0;

void setup() {

  // PC serial connection setup
  Serial.begin(57600);
  Serial.println("Hello @ the serial connection!");

  // Software Serial/bluetooth connection setup
  btSerial.begin(57600);
  btSerial.println("Hello @ the bluetooth connection!");
  // timeout for bluetooth buffer to fill
  btSerial.setTimeout(200);

  // Motor A
  pinMode(motorIN1, OUTPUT); 
  pinMode(motorIN2, OUTPUT); 
  pinMode(speedMotorA, OUTPUT);

  // Motor B
  pinMode(motorIN3, OUTPUT); 
  pinMode(motorIN4, OUTPUT); 
  pinMode(speedMotorB, OUTPUT);

  // LED Pin setup
  pinMode(LEDPin, OUTPUT);
  


}


// Setup for the bluetooth data input
int bufferSize = 11;
char buffer[14];
char terminationChar = '?';
char startChar = '!';
int termination_length = bufferSize;

// Have the LED turning on
boolean LEDOn;
void LEDToggle() {
  LEDOn = !LEDOn;
  if(LEDOn)
    analogWrite(LEDPin, 255);
  else
    analogWrite(LEDPin, 0);  
}



// Ultrasonic distance measure
int ultrasonicDistance() {
  // Make sure not to ping to fast
  Serial.print("Ping: ");
  //if( (millis() - us_starttime) < US_MILLIS_TO_WAIT) {      
  // Send ping, get ping time in microseconds (uS).    
  unsigned int uS = sonar.ping();
  // Convert ping time to distance in cm and print result (0 = outside set distance range)
  int distance_cm = uS / US_ROUNDTRIP_CM;
  // Set the last scan time
  us_starttime = millis();
  // Print distance

  //Serial.print(distance_cm); 
  //Serial.println("cm");
  return distance_cm;

}



// Set the direction of the given motor
void forwardBackward(int dir, int s) {
    // Toggle LED to have some awareness of communicaiton
  LEDToggle();
  if (dir == 0) { // forward
    digitalWrite(motorIN1, HIGH); 
    analogWrite(speedMotorA, s);
  }
  if (dir == 1) { // backward
    digitalWrite(motorIN2, HIGH); 
    analogWrite(speedMotorA, s);
  }
}


// Set the direction of the given motor
void leftRight(int dir, int s) {
    // Toggle LED to have some awareness of communicaiton
  LEDToggle();
  if (dir == 0) { // left
    digitalWrite(motorIN3, HIGH); 
    analogWrite(speedMotorB, s);
  }
  if (dir == 1) { // right
    digitalWrite(motorIN4, HIGH);
    analogWrite(speedMotorB, s);
  }
}

void motorStop() {
  digitalWrite(motorIN1, LOW); 
  digitalWrite(motorIN2, LOW); 
  digitalWrite(motorIN3, LOW); 
  digitalWrite(motorIN4, LOW); 
}



void loop() {

  // flush bluetooth buffer as well as the data buffer
  // btSerial.flush();
  // memset(&buffer[0], 0, sizeof(buffer));

  // Make sure we are not directly in front of an object
  if( ultrasonicDistance() < 12 && ultrasonicDistance() > 0) {
    motorStop();
  }



  // read input from the bluetooth connection
  // expect something like  !1,200,0,100?

  btSerial.readBytesUntil(terminationChar, buffer, termination_length);
 

  int int0 = buffer[0] - '0';
  int int1 = buffer[1] - '0';
  int int2 = buffer[2] - '0';
  driveSpeed = int2+int1*10+int0*100;

  int int3 = buffer[3] - '0';
  int int4 = buffer[4] - '0';
  int int5 = buffer[5] - '0';
  steerSpeed = int5+int4*10+int3*100;
  
  
    Serial.print("drivespeed: "); 
  Serial.print(driveSpeed);   
    Serial.print("steerspeed: "); 
  Serial.print(steerSpeed); 
      Serial.print(", 1: "); 
  Serial.print(buffer[6]); 
    Serial.print(", 2: "); 
  Serial.print(buffer[7]); 
    Serial.print(", 3: "); 
  Serial.print(buffer[8]); 
    Serial.print(", 4: "); 
  Serial.println(buffer[9]); 
  
 
 

  
  

  if ( buffer[6] == '1' ) {
    forwardBackward(0, driveSpeed);
  } 
  else {
    digitalWrite(motorIN1, LOW);
  }
  if ( buffer[7] == '1' ) {
    forwardBackward(1, driveSpeed);
  }
  else {
    digitalWrite(motorIN2, LOW);
  }
  if ( buffer[8] == '1' ) {
    leftRight(0, steerSpeed);
  }
  else {
    digitalWrite(motorIN3, LOW);
  }
  if ( buffer[9] == '1' ) {
    leftRight(1, steerSpeed);
  }
  else {
    digitalWrite(motorIN4, LOW);
  }
  if ( buffer[6] == '0' && buffer[7] == '0' && buffer[8] == '0' && buffer[9] == '0') {
    motorStop();
  }


  // send out a heartbeat
  btSerial.println(ultrasonicDistance());

  //    // split input to our neeeds and convert them to int
  //    char * fb_direction_char = strtok (buffer,"!,"); //forward/backward direction
  //    int fb_direction = atoi(fb_direction_char);
  
  //    char * fb_speed_char = strtok (NULL, ","); //forward/backward speed
  //    int fb_speed = atoi(fb_speed_char);
  //    
  //    char * lr_direction_char = strtok (NULL, ","); // left/right direction 
  //    int lr_direction = atoi(lr_direction_char);
  //    char * lr_speed_char = strtok (NULL, ","); // left/right speed
  //    int lr_speed = atoi(lr_speed_char);
  //
  //    // show what we got
  //    Serial.print("forward/backward dir: "); 
  //    Serial.println(fb_direction);
  //    Serial.print("forward/backward speed: "); 
  //    Serial.println(fb_speed);
  //    Serial.print("left/right dir: "); 
  //    Serial.println(lr_direction);
  //    Serial.print("left/right speed: "); 
  //    Serial.println(lr_speed);


  // send values









}



















