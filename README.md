Este repositório contém o código-fonte de um projeto chamado CYCLOGARD, que consiste em um sistema de detecção de proximidade utilizando um sensor ultrassons e comunicação Bluetooth.



Funcionamento:
Arduino
O dispositivo mede a distância utilizando o sensor ultrassônico.
Se a distância for menor ou igual a 50 cm regista uma ocorrência e se o modo silencioso estiver desativado, o buzzer é acionado.
Os dados da distância são convertidos em uma string e enviados via Bluetooth.

Aplicativo Android
Este arquivo contém o código-fonte do aplicativo Android com todas as classes criadas. O aplicativo se conecta ao dispositivo Bluetooth e exibe os dados da distância medida. Além disso, permite visualizar o histórico de ocorrências, capturar fotos e enviar dados para um servidor remoto.

Funcionalidades
Conectar e desconectar do dispositivo Bluetooth.
Exibir o estado da conexão e a distância medida.
Alternar entre o modo silencioso.
Exibir o histórico de ocorrências numa lista.
Visualizar a localização das ocorrências num mapa.
Capturar fotos utilizando a câmera do dispositivo.
Enviar fotos para um servidor remoto via FTP.
Enviar dados para um servidor via PHP.
