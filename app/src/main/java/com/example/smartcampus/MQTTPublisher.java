package com.example.smartcampus;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.eclipse.paho.client.mqttv3.*;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class MQTTPublisher {

    static String GUID = "742e7aa9-4583-4c4d-9160-6044b8e78e67";
    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    //iot.eclipse.org
    public static final String TOPIC_MESSAGE = "home/" + GUID;
    private MqttClient client;
    public String clientId;

    MQTTPublisher() {

        //We have to generate a unique Client id.
        clientId = getMACAddress() + "-pub";

        try {
            client = new MqttClient(BROKER_URL, clientId);
        } catch (MqttException e) {
            //e.printStackTrace();
            //System.exit(1);
        }
    }

    public void start(String message) {

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            //options.setWill(client.getTopic("home/LWT"), "I'm gone :(".getBytes(), 0, false);

            client.connect(options);

            //Publish data forever
            publishMessage(message);

        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    void publishMessage(String s) throws MqttException {
        final MqttTopic messageTopic = client.getTopic(TOPIC_MESSAGE);
        messageTopic.publish(new MqttMessage(s.getBytes()));

        //System.out.println("Published data. Topic1: " + brightnessTopic.getName() + "   Message: " + brigthness);
    }

    public String getMACAddress(){
        String result = "";

        try {
            for (NetworkInterface ni : Collections.list(
                    NetworkInterface.getNetworkInterfaces())) {
                byte[] hardwareAddress = ni.getHardwareAddress();

                if (hardwareAddress != null) {
                    for (int i = 0; i < hardwareAddress.length; i++)
                        result += String.format((i == 0 ? "" : "-") + "%02X", hardwareAddress[i]);

                    return result;
                }
            }

        } catch (SocketException e) {
            System.out.println("Could not find out MAC Adress. Exiting Application ");
            System.exit(1);
        }
        return result;
    }

}
