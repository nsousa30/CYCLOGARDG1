<?php

$id = $_POST['ID'];
$data = $_POST['Data'];
$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];


$servername = "localhost";
$username = "CYCLOGARD";
$password = "1234";
$dbname = "cyclogard";

$conn = new mysqli($servername, $username, $password, $dbname);


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$sql = "INSERT INTO cyclogard (ID, Data, Latitude, Longitude) VALUES ('$id', '$data', '$latitude', '$longitude')";
if ($conn->query($sql) === TRUE) {
    echo "Data inserted successfully";
} else {
    echo "Error inserting data: " . $conn->error;
}


$conn->close();
?>



