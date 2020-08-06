

Formato das mensagens
=====================


Frame
-----

| tamanho   |   cabecalho    |      corpo          |
|-----------|----------------|---------------------|
| (2 bytes) |   (30 bytes)   |      (x bytes)      |


- tamanho: tamanho da mensagem completa (2 + 30 + x)
- cabecalho: informações sobre mensagem
- corpo: mensagem encriptada


Cabecalho
---------

| versao   | formato  | servico  | tipo     | formatoId | id         | reservado  |
|----------|----------|----------|----------|-----------|------------|------------|
| (1 byte) | (1 byte) | (1 byte) | (1 byte) | (1 byte)  | (12 bytes) | (13 bytes) |


- versao: 0x01
- formato: 0x01 - ISO8583, 0x02 - Texto (GFT)
- servico: 0x03 - GFT, 0x05 - Transacional (ATM)
- tipo: 0x01 - ping, 0x02 - pong, 0x03 - dados
- formatoId: 0x01
- id: string com IdTerminal em formato decimal, preenchido com '0' a esquerda
    (IdTerminal deve ter no máximo 7 digitos)
- reservado: preenchido com 0x00


Corpo (Depois de decriptado)
-----

 |        mensagem iso       |  mac (opcional)  |
 |---------------------------|------------------|
 |        (y bytes)          |  (32 bytes)      |


- mensagem iso: mensagem no formato ISO 8583
- mac: código hash "DES/ECB/PKCS5Padding" calculado sobre (cabecalho + corpo)<br>
  O mac está presente se (!bit01 && bit64) || (bit01 && bit128)


Mensagem iso
------------

|  mti        |  bitmap      |    fields                |
|-------------|--------------|--------------------------|
|  (4 bytes)  |  (16 bytes)  |    (y - 4 - 16 bytes)    |


- mti: Message Type Indicator, é um campo numérico de quatro digitos decimais
- bitmap: 16 caracteres hexadecimais no padrão ASCII
- fields: campos especificados no bitmap

Referência: <a href="https://en.wikipedia.org/wiki/ISO_8583">ISO-8583</a>
