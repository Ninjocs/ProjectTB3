#include <HTTPClient.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_FeatherOLED.h>
#include <Arduino.h>
#include "TEMT6000.h"

#define BUTTON_PIN 2
#define DHTPIN 27
#define DHTPIN2 12
#define DHTTYPE DHT22

Adafruit_FeatherOLED oled = Adafruit_FeatherOLED();
DHT_Unified dht(DHTPIN, DHTTYPE);? BK
TEMT6000 lux(A0, DHTPIN2, 1023);

void setup() {
  Serial.begin(9600);
  dht.begin();
  oled.init();

  pinMode(BUTTON_PIN, INPUT_PULLUP);
  
  WiFi.begin(ssid, pass); //Here is where you put the identifier of your internet connexion to send the information 
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("WiFi connected.");

}

void sendPlaceData(float temperature, float humidity) {
  if (WiFi.status() == WL_CONNECTED) { 

    String serverURL = "https://wasteshield.cleverapps.io/api/places"; 
    http.begin(serverURL);

    http.addHeader("Content-Type", "application/json"); 

    String payload = "{\"name\": \"Place_name\", \"temperature\": " + String(temperature) + 
                 ", \"humidity\": " + String(humidity) + "}";

    int httpResponseCode = http.POST(payload); 

    if (httpResponseCode > 0) {
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
    } else {
      Serial.print("Error code: ");
      Serial.println(httpResponseCode);
    }

    http.end(); 
  } else {
    Serial.println("WiFi not connected");
  }
}

void loop() {
  delay(delayMS);

  sensors_event_t event;

  dht.temperature().getEvent(&event);
  float temperature = !isnan(event.temperature) ? event.temperature : 0.0;

  dht.humidity().getEvent(&event);
  float humidity = !isnan(event.relative_humidity) ? event.relative_humidity : 0.0;

  oled.print("Temp: ");
  oled.print(temperature);
  oled.println("°C");

  oled.print("Humidity: ");
  oled.print(humidity);
  oled.println("%");
  oled.display();

  if (digitalRead(BUTTON_PIN) == LOW) {  //If you press the A button 
    sendRoomData(temperature, humidity);
    delay(500); 
  }

  delay(100);
}
