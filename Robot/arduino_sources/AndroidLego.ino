#include <ServoLego.h>
#include <SoftwareSerial.h>

ServoLego steerServo;
const int motorIN1 = 2;    // H-bridge leg 1 (pin IN1, dir1 PinA)
const int motorIN2 = 3;    // H-bridge leg 2 (pin IN2, dir2 PinA)
const int speedMotorA = 9;      //  H-bridge EN_A



// Setup for the bluetooth data input
int bufferSize = 11;
char buffer[14];
char terminationChar = '?';
char startChar = '!';
int termination_length = bufferSize;

// Second Serial interface with (TX, RX) at the bluetooth module
SoftwareSerial btSerial(4,5); 


void setup(){
  // PC serial connection setup
  Serial.begin(57600);
  Serial.println("Hello @ the serial connection!");
  Serial.setTimeout(200);

  steerServo.attach(7,8);
  // Motor A
  pinMode(motorIN1, OUTPUT); 
  pinMode(motorIN2, OUTPUT); 
  pinMode(speedMotorA, OUTPUT);
  // set motor to full speed
  digitalWrite(speedMotorA, HIGH);
  
    // Software Serial/bluetooth connection setup
  btSerial.begin(57600);
  btSerial.println("Hello @ the bluetooth connection!");
  // timeout for bluetooth buffer to fill
  btSerial.setTimeout(200);
  
}




void steer(int i) {
  if(i == 1)
    steerServo.left();
  else if(i == 2)
    steerServo.right();
  else
    steerServo.none();
}

void drive(int i) {
  if(i == 2) {
    digitalWrite(motorIN1, LOW);
    digitalWrite(motorIN2, HIGH);
  }
  else if(i == 1) {
    digitalWrite(motorIN1, HIGH);
    digitalWrite(motorIN2, LOW);
  } 
  else {
    digitalWrite(motorIN1, LOW);
    digitalWrite(motorIN2, LOW);
  }
}

void loop(){


  btSerial.readBytesUntil(terminationChar, buffer, termination_length);
  
    if ( buffer[6] == '1' ) {
    drive(1);
  } 
  if ( buffer[7] == '1' ) {
    drive(2);
  }
  if ( buffer[8] == '1' ) {
    steer(1);
  }
  if ( buffer[9] == '1' ) {
    steer(2);
  }
 
  if ( buffer[6] == '0' && buffer[7] == '0') {
   drive(0);
  }
 
  if ( buffer[8] == '0' && buffer[9] == '0') {
   steer(0);
  }
  
  
  
      Serial.print("1: "); 
  Serial.print(buffer[6]); 
    Serial.print(", 2: "); 
  Serial.print(buffer[7]); 
    Serial.print(", 3: "); 
  Serial.print(buffer[8]); 
    Serial.print(", 4: "); 
  Serial.println(buffer[9]); 
  

}





