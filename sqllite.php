<?php
// Obter os dados enviados pela aplicação Android
$id = $_POST['ID'];
$data = $_POST['Data'];
$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];

// Conectar-se à base de dados MySQL
$servername = "localhost";
$username = "CYCLOGARD";
$password = "1234";
$dbname = "cyclogard";

$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar a conexão
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Inserir os dados na base de dados MySQL
$sql = "INSERT INTO cyclogard (ID, Data, Latitude, Longitude) VALUES ('$id', '$data', '$latitude', '$longitude')";
if ($conn->query($sql) === TRUE) {
    echo "Data inserted successfully";
} else {
    echo "Error inserting data: " . $conn->error;
}

// Fechar a conexão com o banco de dados
$conn->close();
?>



