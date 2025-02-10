package org.celeste;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONArray;

public class Main extends JFrame {

    private JTextField cityField;
    private JLabel temperatureLabel;
    private JLabel conditionLabel;
    private JLabel cityLabel;
    private String apiKey;

    public Main() {
        setTitle("Weather-App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // BorderLayout für bessere Anordnung

        // Panel für Eingabefelder und Button
        JPanel inputPanel = new JPanel(new FlowLayout());
        cityLabel = new JLabel("City::");
        inputPanel.add(cityLabel);
        cityField = new JTextField(15);
        inputPanel.add(cityField);
        JButton updateButton = new JButton("Refresh Weather");
        inputPanel.add(updateButton);
        add(inputPanel, BorderLayout.NORTH); // Oben im Fenster

        // Panel für Wetterinformationen
        JPanel infoPanel = new JPanel(new GridLayout(3, 1)); // 3 Zeilen, 1 Spalte
        temperatureLabel = new JLabel("N/A");
        conditionLabel = new JLabel("N/A");
        cityLabel = new JLabel("N/A");
        infoPanel.add(cityLabel);
        infoPanel.add(temperatureLabel);
        infoPanel.add(conditionLabel);
        add(infoPanel, BorderLayout.CENTER); // In der Mitte

        // API-Key Eingabe
        apiKey = JOptionPane.showInputDialog(this, "Please enter the API key:",
                "Enter API-Key", JOptionPane.QUESTION_MESSAGE);
        if (apiKey == null || apiKey.isEmpty()) {
            System.exit(0); // Beenden, wenn kein API-Key eingegeben wurde
        }

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText();
                try {
                    WeatherData data = fetchWeatherData(city, apiKey); // API-Key übergeben
                    updateWeatherInfo(data);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Main.this,
                            "Error while Getting Weather Data...: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pack();
        setVisible(true);
    }

    private WeatherData fetchWeatherData(String city, String apiKey) throws IOException {
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric&lang=de";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());
        JSONObject main = json.getJSONObject("main");
        double temp = main.getDouble("temp");

        JSONArray weather = json.getJSONArray("weather");
        JSONObject weatherData = weather.getJSONObject(0);
        String condition = weatherData.getString("description");

        return new WeatherData(temp, condition, city);
    }

    private void updateWeatherInfo(WeatherData data) {
        temperatureLabel.setText(String.format("Temperature: %.1f°C", data.getTemperature()));
        conditionLabel.setText("Condition: " + data.getCondition());
        cityLabel.setText("City: " + data.getCity());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

    private class WeatherData {
        private double temperature;
        private String condition;
        private String city;

        public WeatherData(double temperature, String condition, String city) {
            this.temperature = temperature;
            this.condition = condition;
            this.city = city;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getCondition() {
            return condition;
        }

        public String getCity() {
            return city;
        }
    }
}