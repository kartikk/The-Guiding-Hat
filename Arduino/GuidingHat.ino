#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>

/* Assign a unique ID to this sensor at the same time */
Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

#define ledPin 13  // use the built in LED on pin 13 of the Uno
char state = 0;
int flag = 0;        // make sure that you return the state only once
int usflag=0;       //flag for ultrasonic sensor
#define echoPin 7 // Echo Pin
#define trigPin 8 // Trigger Pin
#define frontvib 11 //front vibrator pin
#define leftvib 12 //left vibrator pin
#define rightvib 10 //right vibrator pin
#define backvib 9 //back vibrator pin
#define maximumRange 400 // Maximum range needed
#define minimumRange 0 // Minimum range needed
long duration, distance; // Duration used to calculate distance
long previousMillis = 0,previousMillisUs = 0;
long interval = 500, intervalUs = 50;

void setup() {
    // sets the pins as outputs:
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);
    pinMode(frontvib, OUTPUT);
    digitalWrite(frontvib, LOW);
    pinMode(leftvib, OUTPUT);
    digitalWrite(leftvib, LOW);
    pinMode(rightvib, OUTPUT);
    digitalWrite(rightvib, LOW);
    pinMode(backvib, OUTPUT);
    digitalWrite(backvib, LOW);
    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);
    Serial.begin(9600); // Default baud rate
    delay(100);
    mag.begin();
}
void ultrasonic()
{
 /* The following trigPin/echoPin cycle is used to determine the
 distance of the nearest object by bouncing soundwaves off of it. */ 
 digitalWrite(trigPin, LOW); 
 delayMicroseconds(2); 

 digitalWrite(trigPin, HIGH);
 delayMicroseconds(10); 
 
 digitalWrite(trigPin, LOW);
 duration = pulseIn(echoPin, HIGH);
 
 //Calculate the distance (in cm) based on the speed of sound.
 distance = duration/58.2;
 
 if (distance >= maximumRange || distance <= minimumRange){
 /* Send a negative number to computer and Turn LED ON 
 to indicate "out of range" */
 Serial.println("-1");
 }
 else {
 /* Send the distance to the computer using Serial protocol, and
 turn LED OFF to indicate successful reading. */
 Serial.println(distance);
 if(distance<15)
 {
        digitalWrite(11, HIGH);
        digitalWrite(12, HIGH);
        digitalWrite(10, HIGH);
        digitalWrite(9, HIGH); 
        usflag=1;
 }
 else if(usflag==1)
 {
        digitalWrite(11, LOW);
        digitalWrite(12, LOW);
        digitalWrite(10, LOW);
        digitalWrite(9, LOW);
        usflag=0;
 }
 
 } 
}
 
 
 void magnetometer()
 {
     /* Get a new sensor event */ 
  sensors_event_t event; 
  mag.getEvent(&event);
 
  /* Display the results (magnetic vector values are in micro-Tesla (uT)) */
 // Serial.print("X: "); Serial.print(event.magnetic.x); Serial.print("  ");
 // Serial.print("Y: "); Serial.print(event.magnetic.y); Serial.print("  ");
 // Serial.print("Z: "); Serial.print(event.magnetic.z); Serial.print("  ");Serial.println("uT");

  // Hold the module so that Z is pointing 'up' and you can measure the heading with x&y
  // Calculate heading when the magnetometer is level, then correct for signs of axis.
  float heading = atan2(event.magnetic.y, event.magnetic.x);
   
  //  float declinationAngle = 0.22;
 // heading += declinationAngle;
  // Correct for when signs are reversed.
  if(heading < 0)
    heading += 2*PI;
    
  // Check for wrap due to addition of declination.
  if(heading > 2*PI)
    heading -= 2*PI;
   
  // Convert radians to degrees for readability.
  float headingDegrees = heading * 180/M_PI; 
  
  //Serial.print("Heading (degrees): ");
  Serial.print('<'); 
  Serial.print(headingDegrees);
  Serial.print('>');
 }
 
 void bluetooth()
 {
       //if some data is sent, read it and save it in the state variable
    if(Serial.available() > 0){
      state = Serial.read();
      //flag to check if data is fresh
      flag=0;
    }
    // if the state is 0 the led will turn off
    if (state == '0') {
        digitalWrite(ledPin, LOW);
        digitalWrite(12, LOW);
        digitalWrite(11, LOW);
        digitalWrite(10, LOW);
        digitalWrite(9, LOW);
        if(flag == 0){
       //   Serial.println("LED: off");
          flag = 1;
        }
    }
    // if the state is 1 the led will turn on
    else if (state == '1') {
        digitalWrite(ledPin, HIGH);
        if(flag == 0){
       //   Serial.println("LED: on");
          flag = 1;
        }
    }
        else if (state == 'F') {
          digitalWrite(frontvib, HIGH);
          if(flag == 0){
          Serial.println("VIbrator: front vibrator on");
          flag = 1;
        }}
            else if (state == 'L') {
              digitalWrite(leftvib, HIGH);
          if(flag == 0){
          Serial.println("Vibrator: left vibrator on");
          flag = 1;
        }} 
            else if (state == 'R') {
              digitalWrite(rightvib, HIGH);
          if(flag == 0){
          Serial.println("Vibrator: right vibrator on");
          flag = 1;
        }} 
            else if (state == 'B') {
          if(flag == 0){
            digitalWrite(backvib, HIGH);
          Serial.println("Vibrator: back vibrator on");
          flag = 1;
        }}
           else if (state == 'f') {
           digitalWrite(frontvib, LOW);
          if(flag == 0){
         // Serial.println("Vibrator: front vibrator off");
          flag = 1;
        }}     
           else if (state == 'l') {
          digitalWrite(leftvib, LOW);
          if(flag == 0){
        //  Serial.println("Vibrator: left vibrator off");
          flag = 1;
        }} 
            else if (state == 'r') {
              digitalWrite(rightvib, LOW);
          if(flag == 0){
         // Serial.println("Vibrator: right vibrator off");
          flag = 1;
        }} 
            else if (state == 'b') {
          if(flag == 0){
            digitalWrite(backvib, LOW);
        //  Serial.println("Vibrator: back vibrator off");
          flag = 1;
        }}     
    }

 
 
void loop() {
  unsigned long currentMillis = millis();
  bluetooth();
  if(currentMillis - previousMillisUs > intervalUs) {
   previousMillisUs = currentMillis; 
    ultrasonic();
  }
  if(currentMillis - previousMillis > interval) {
   previousMillis = currentMillis; 
    magnetometer();
  }
}

