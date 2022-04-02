Plataforma UCDrive
02/04/2022
Alunos:
    Joana Simões, 2019217013
    Samuel Carinhas, 2019217199

Informações:
    - Este projeto foi desenvolvido usando a versão dezassete do Java, por este motivo é altamente recomendado usar esta versão.

Como instalar e executar:
    Setup necessário:
        Para poder executar a aplicação servidor, é necessário duas pastas para simular os discos dos dois servidores.
        As pastas necessitam de conter uma pasta config com um ficheiro accounts.txt lá dentro.
        Este ficheiro contém a informação dos clientes, um por linhas. Além disso, cada linha deve ter o seguinte formato:
            {username};{password};{departamento};{universidade};{morada};{contacto telefónico};{nº do cartão de cidadão};{validade do cartão de cidadão};{diretoria atual (tem que ser /home por default)}
        Também é necessário um ficheiro com as configurações para cada servidor. Este ficheiro deve estar na mesma pasta que o ucDrive.jar e deve conter uma linha com o seguinte formato:
            {ip do servidor} {porto TCP do servidor} {porto UDP do servidor (para os pings)} {ip do outro servidor} {porto UDP do outro servidor (pings)} {porto UDP do servidor atual para sincronização} {porto UDP do outro servidor para sincronização} {nome da pasta para simular o disco (esta pasta tem de ser previamente criada)} {nº máximo de failovers} {tempo máximo de timeout (em milissegundos)}
        Exemplo:
            config_a.txt
                localhost 7000 6000 localhost 6002 6001 6003 disco_a 5 100
            config_b.txt
                localhost 7001 6002 localhost 6000 6003 6001 disco_b 5 100
    Como executar:
        Para executar o servidor, basta apenas correr o ficheiro ucDrive.jar pelo terminal fornecendo o ficheiro de configurações por argumento.
        Exemplo:
            java -jar ucDrive.jar config_a.txt
        Para executar o cliente, é necessario correr o ficheiro terminal.jar pelo terminal fornecendo os seguintes argumentos:
            {ip_server_a} {ip_server_b} {porto_tcp_server_a} {porto_tcp_server_b}
        Exemplo:
            java -jar terminal.jar localhost localhost 7000 7001
    Como ver os comandos possíveis do cliente:
        Executar os seguintes comandos na aplicação cliente:
            server-help # Comandos relacionados com o servidor
            local-help # Comandos 

Segue um exemplo de um setup das configurações referidas anteriormente na pasta ./exemplo
Para utilizar este exemplo, o ficheiro ucDrive.jar deve ser colocado nessa pasta e executado com o seguinte comando:
    Para o servidor A:
        java -jar ucDrive.jar config_a.txt
    Para o servidor B:
        java -jar ucDrive.jar config_b.txt
        
