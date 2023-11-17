# Assinador
Assinador de pdf's com certificados digitais instalado no windows (cartão inteligente está incluso) e arquivos .pfx.
# Requisitos Necessários
1. Ter instaldo uma versão do java 17 ou superior
2. Caso tenha mais versões do java instaladas colocar a mais recente como principal. Pode verificar a versão instalada usando 
```
java -version
```

## Build
1. Instalar uma versão do maven superior a 3.1, em sua maquina ou IDE. [Maven Download](https://maven.apache.org/download.cgi)
2. Executar um clean e forçar o download das dependencias do projeto em *pom.xml*.
```
mvn clean install -U
```
3. Com uso de ide. Ao selecionar as opções de execução do projeto use o comando do maven clean, update-project e maven-install. <br>
A aplicação usa o plugin *maven-shade-plugin*. Logo o artefato executavel é o *{nome_projeto}-{versao_projeto}-shaded.jar*

## Uso
Existe três opção de uso para essa aplicação:
### Assinatura local
Executa o assinador no modo de assinatura local. Onde é escolhido um ou mais pdf para assinar e todos são assinados de uma vez, note que o pdf selecionado sera sobreescrito com o pdf assinado. 
Para usa-lo nesse modo:
```
javaw -jar assinador.jar
```
### Assinatura local com especificação de arquivos
Executa o assinado no modo de assinatura por arquivos especificados como argumento da aplicação.<br>
O Argumento esperado é nomeado com <b>--datapath</b> e recebe uma string em base64 que representa um json no formato:
```
[
    {
        "pathLeitura": "C:\\Caminho\\para\\ler\\Modelo.pdf",
        "pathEscrita": "C:\\Caminho\\para\\escrever\\Modelo_novo.pdf"
    }
]
```
Caso o atributo "pathEscrita" não seja informado, então o arquivo informado para leitura será usado tabém para salvar o arquivo assinado. O comando final ficaria assim:
```
javaw -jar assinador.jar --datapath=WwogICAgewogICAgICAgICJwYXRoTGVpdHVyYSI6ICJDOlxcQ2FtaW5ob1xccGFyYVxcbGVyXFxNb2RlbG8ucGRmIiwKICAgIH0KXQ==
```
### Assinatura local com serviços web (Rest Api's)
Executa o assinado no modo de assinatura por arquivos acessíveis através de Rest Apis.<br>
O Argumento esperado é nomeado com <b>--dataservice</b> e recebe uma string em base64 que representa um json no formato:
```
{
    "urlPrincipal": "https://dominio.com.br/nome-do-recurso/%d",
    "parametros": [
        {
            "codigo": 140923,
            "url": "https://dominio.com.br/nome-do-recurso/%d",
        }
    ]
}
```
Com essas informações a aplicação ira fazer uma requisição GET na url informada trocando o "%d" pelo código identificador. É esperado que o endereço retorne os bytes de um arquivo pdf, e contenha o cabeçalho <b>Content-Disposition</b> com o valor <b>inline; filename=nome-do-arquivo</b>, para que seja apresentado em tela o nome real do pdf. <br>
Após a assinatura a aplicação agora efetua um POST na url informada trocando o "%d" pelo código identificador, enviando os bytes como <b>multipart/form-data</b>. O comando final ficaria assim
```
javaw -jar assinador.jar --dataservice=ewogICAgInVybFByaW5jaXBhbCI6ICJodHRwczovL2RvbWluaW8uY29tLmJyL25vbWUtZG8tcmVjdXJzby8lZCIsCiAgICAicGFyYW1ldHJvcyI6IFsKICAgICAgICB7CiAgICAgICAgICAgICJjb2RpZ28iOiAxNDA5MjMKICAgICAgICB9CiAgICBdCn0=
```

NOTE: Ao <b>não</b> informar o atributo de "url" individualmente para cada recurso, será assumido que o endereço para todos os recursos seja a "urlPrincipal". Dessa forma todos os códigos informados serão passado nessa mesma URL um por vez. Sempre que uma assinatura é efetuada no mesmo instante é executado o POST. Garantindo a ordem de assinatura. <br>




 
