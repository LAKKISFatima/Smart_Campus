package com.example.smartcampus;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class MQTTPublisher {

    //static String GUID = "742e7aa9-4583-4c4d-9160-6044b8e78e67";
    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String TOPIC_MESSAGE = "LebaneseUniversity/FacultyOfSciences/SmartCampus";
    public static final String TAG = "MQTT";
    private MqttAndroidClient client;
    public String clientId;
    public Context context;

    MQTTPublisher(Context context) {

        //We have to generate a unique Client id.
        //clientId = getMACAddress() + "-pub";
        clientId = MqttClient.generateClientId();
        this.context = context;

            //client = new MqttClient(BROKER_URL, clientId);
            client = new MqttAndroidClient(context.getApplicationContext(), BROKER_URL,
                            clientId);

    }

    public void start(String message) {

        //try {
            //MqttConnectOptions options = new MqttConnectOptions();
            //options.setCleanSession(false);
            //options.setWill(client.getTopic("home/LWT"), "I'm gone :(".getBytes(), 0, false);

            //client.connect(options);

            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                        String payload = message;
                        byte[] encodedPayload = new byte[0];
                        try {
                            encodedPayload = payload.getBytes("UTF-8");
                            MqttMessage mqttMessage = new MqttMessage(encodedPayload);
                            client.publish(TOPIC_MESSAGE, mqttMessage);
                            Toast.makeText(context, "Message published", Toast.LENGTH_LONG).show();
                        } catch (MqttException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }



            //Publish data forever
            //publishMessage(message);

        /*} catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }*/
    }

    void publishMessage(String s) throws MqttException {
        //final MqttTopic messageTopic = client.getTopic(TOPIC_MESSAGE);
        //messageTopic.publish(new MqttMessage(s.getBytes()));

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
