
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

// Definição dos pins
const int trigPin = 32;
const int echoPin = 33;
const int trigPin2 = 34;
const int echoPin2 = 35;

// Definição das Variáveis
long duration;
int distance;
int distance1;
int distance2;

void setup() {

  pinMode(trigPin, OUTPUT); // Define o trigPin como uma saida
  pinMode(echoPin, INPUT); // Define o echoPin como uma entrada
  pinMode(trigPin2, OUTPUT); 
  pinMode(echoPin2, INPUT);

  Serial.begin(115200); // Começa a comunicação Serial
  SerialBT.begin("CYCLOGARD"); //Nome do dispositivo bluetooth
  Serial.println("Dispositivo disponivel para conexão bluetooth");
}

void loop() {

  // Sensor 1

  // Limpa o trigPin
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  // Define o trigPin no estado HIGH durante 10 micro segundos
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  // Lê o echoPin, retorna o tempo do eco em microsegundos
  duration = pulseIn(echoPin, HIGH);
  // Calcular a distância
  distance = duration * 0.034 / 2;


   Sensor 2

  digitalWrite(trigPin2, LOW);
  delayMicroseconds(2);
  
  digitalWrite(trigPin2, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin2, LOW);
  
  duration = pulseIn(echoPin2, HIGH);
  
  distance2 = duration * 0.034 / 2;

  if (distance1 >= distance2) {
      
      distance = distance2;

  } else {

    distance = distance1;
    
  }

  delay(100);

  // Nesta parte a variável distance é convertida em string e é usada na transmissão bluetooth na forma de vários char
  String msg = String(distance);
  uint8_t buf[msg.length()];
  memcpy(buf,msg.c_str(),msg.length());
  //Enviar bytes
  SerialBT.write(buf,msg.length());
  SerialBT.println(); 

  
  //Ler mensagens recebidas via bluetooth
 if (SerialBT.available()) {
    Serial.write(SerialBT.read());
  }
}