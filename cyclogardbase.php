<?php
// Configurações do banco de dados
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "cyclogard";


$conn = new mysqli($servername, $username, $password, $dbname);


if ($conn->connect_error) {
    die("Falha na conexão com o banco de dados: " . $conn->connect_error);
}


$sql = "SELECT id, data, latitude, longitude FROM cyclogard";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    
    echo '<style>
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            text-align: left;
            padding: 8px;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>';

    
    echo "<table>";
    echo "<tr><th>ID</th><th>Data</th><th>Latitude</th><th>Longitude</th></tr>";

    while ($row = $result->fetch_assoc()) {
        echo "<tr>";
        echo "<td>" . $row["id"] . "</td>";
        echo "<td>" . $row["data"] . "</td>";
        echo "<td>" . $row["latitude"] . "</td>";
        echo "<td>" . $row["longitude"] . "</td>";
        echo "</tr>";
    }

    echo "</table>";
} else {
    echo "Nenhum resultado encontrado na tabela 'cyclogard'.";
}


$conn->close();
?>
