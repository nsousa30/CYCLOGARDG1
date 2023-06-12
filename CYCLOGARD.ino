
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;


const int trigPin = 32;
const int echoPin = 33;
const int buzzerPin = 25;


long duration;
int distance;
byte modosilencioso;

void setup() {

  pinMode(trigPin, OUTPUT); 
  pinMode(echoPin, INPUT); 
  pinMode(buzzerPin, OUTPUT); 
  

  Serial.begin(115200); 
  SerialBT.begin("CYCLOGARD"); 
  Serial.println("Dispositivo disponivel para conexão bluetooth");
}

void loop() {

  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);

  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH);

  distance = duration * 0.034 / 2;

  if (distance <= 50 && (modosilencioso==0 || modosilencioso==48) && distance!=0) {

    digitalWrite(buzzerPin,HIGH);

  } else {

    digitalWrite(buzzerPin,LOW);

  }

  
  String msg = String(distance);
  uint8_t buf[msg.length()];
  memcpy(buf,msg.c_str(),msg.length());

  SerialBT.write(buf,msg.length());
  delay(250);

 if (SerialBT.available()) {
    modosilencioso=SerialBT.read();
  }
  
}