# MS Consume Sam

MicroServicio que expone un servicio REST el cual envía un archivo xml a SAM

### Contrucción y ejecución

The example can be built with

    mvn clean install && mvn spring-boot:run

### Configuración

Se parametriza los datos para consumir el servicio

    ####### Properties de servicio sam ########
    
    rest.producer.method = POST
	rest.producer.host = api.finto.fi
	rest.producer.context = /rest/v1/search
	rest.producer.protocol = https
    

### Pruebas

Ejecución a través de CURL

    curl --location --request POST 'http://localhost:8082/sam/send_file' \
	--header 'operationname: sendfile' \
	--header 'id_file: 0000222000' \
	--header 'varname: test' \
	--header 'Content-Type: application/json' \
	--data-raw '{
	    "payload": "ok"
	}'

Response 200

    {
        "estado": "OK"
    }

Response 4XX
    
    {
    	"estado": "ERROR GENERAL"
	}

Response 5XX

    {
    	"estado": "ERROR GENERAL"
	}
