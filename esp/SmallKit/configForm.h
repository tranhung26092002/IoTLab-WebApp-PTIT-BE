const char *configForm = R"html(
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>ESP32 IoT Device Configuration</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
        background-color: #f5f5f5;
      }
      .container {
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      }
      h1 {
        color: #333;
        text-align: center;
        margin-bottom: 30px;
      }
      .form-group {
        margin-bottom: 20px;
      }
      label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
        color: #555;
      }
      input[type="text"],
      input[type="password"],
      textarea {
        width: 100%;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 16px;
        box-sizing: border-box;
      }
      textarea {
        height: 80px;
        resize: vertical;
      }
      button {
        background-color: #007bff;
        color: white;
        padding: 12px 30px;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        cursor: pointer;
        width: 100%;
      }
      button:hover {
        background-color: #0056b3;
      }
      .info {
        background-color: #e7f3ff;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #007bff;
      }
      .device-info {
        background-color: #f8f9fa;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border: 1px solid #dee2e6;
      }
      .device-info strong {
        color: #495057;
      }
      .wifi-section {
        background-color: #fff3cd;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #ffc107;
      }
      .mqtt-section {
        background-color: #d1ecf1;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #17a2b8;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h1>ESP32 IoT Device Configuration</h1>

      <div class="info">
        <strong>Device Information:</strong><br />
        MAC Address: <span id="mac-address">Loading...</span><br />
        Device Type: <span id="device-type">Loading...</span><br />
        Active Code: <span id="active-code">Loading...</span>
      </div>

      <form id="config-form">
        <div class="wifi-section">
          <h3>WiFi Configuration</h3>
          <div class="form-group">
            <label for="ssid">WiFi Network Name (SSID):</label>
            <input type="text" id="ssid" name="ssid" required />
            <select id="ssid_list" onchange="document.getElementById('ssid').value=this.value">
              <option>-- Chọn WiFi --</option>
            </select>
          </div>

          <div class="form-group">
            <label for="password">WiFi Password:</label>
            <input type="password" id="password" name="password" required />
          </div>
        </div>

        <div class="device-info">
          <h3>Device Information</h3>
          <div class="form-group">
            <label for="device_name">Device Name:</label>
            <input
              type="text"
              id="device_name"
              name="device_name"
              placeholder="e.g., Temperature Sensor Lab A"
              required
            />
          </div>

          <div class="form-group">
            <label for="device_description">Device Description:</label>
            <textarea
              id="device_description"
              name="device_description"
              placeholder="e.g., Monitor temperature and humidity in Lab A"
            ></textarea>
          </div>
        </div>

        <div class="mqtt-section">
          <h3>MQTT Broker Configuration</h3>
          <div class="form-group">
            <label for="mqtt_server">MQTT Server Address:</label>
            <input
              type="text"
              id="mqtt_server"
              name="mqtt_server"
              placeholder="e.g., 14.225.255.177"
              required
            />
          </div>

          <div class="form-group">
            <label for="mqtt_port">MQTT Port:</label>
            <input
              type="text"
              id="mqtt_port"
              name="mqtt_port"
              placeholder="e.g., 1883"
              value="1883"
              required
            />
          </div>

          <div class="form-group">
            <label for="mqtt_username">MQTT Username:</label>
            <input
              type="text"
              id="mqtt_username"
              name="mqtt_username"
              placeholder="e.g., admin"
              value="admin"
            />
          </div>

          <div class="form-group">
            <label for="mqtt_password">MQTT Password:</label>
            <input
              type="password"
              id="mqtt_password"
              name="mqtt_password"
              placeholder="e.g., admin"
              value="admin"
            />
          </div>
        </div>

        <button type="submit">Save Configuration</button>
      </form>
    </div>

    <script>
      // Lấy thông tin thiết bị động từ ESP32
      fetch('/device-info.json')
        .then(res => res.json())
        .then(info => {
          document.getElementById("mac-address").textContent = info.mac_address;
          document.getElementById("device-type").textContent = info.device_type;
          document.getElementById("active-code").textContent = info.active_code;
          if(info.device_name) document.getElementById("device_name").value = info.device_name;
        });

      // Tự động quét WiFi và cập nhật dropdown
      window.onload = function() {
        fetch('/wifiscan.json')
          .then(res => res.json())
          .then(list => {
            var select = document.getElementById('ssid_list');
            list.forEach(ssid => {
              var opt = document.createElement('option');
              opt.value = ssid;
              opt.innerText = ssid;
              select.appendChild(opt);
            });
          });
      };

      // Handle form submission
      document
        .getElementById("config-form")
        .addEventListener("submit", function (e) {
          e.preventDefault();

          const formData = new FormData(this);

          fetch("/save-config", {
            method: "POST",
            body: formData,
          })
            .then((response) => response.text())
            .then((data) => {
              alert(data);
              if (data.includes("successfully")) {
                // Show restart message
                document.body.innerHTML = `
                        <div class=\"container\">\n                            <h1>Configuration Saved!</h1>\n                            <div class=\"info\">\n                                <p>Your device configuration has been saved successfully.</p>\n                                <p>The device will now restart and attempt to connect to the specified WiFi network.</p>\n                                <p>You can close this page and disconnect from the ESP32 WiFi network.</p>\n                            </div>\n                        </div>\n                    `;
              }
            })
            .catch((error) => {
              alert("Error saving configuration: " + error);
            });
        });
    </script>
  </body>
</html>
)html";
