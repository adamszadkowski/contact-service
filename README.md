# Contact Service

![](https://img.shields.io/docker/pulls/klyman/contact-service.svg)
![](https://img.shields.io/docker/stars/klyman/contact-service.svg)

Contact-service has been created as a backend for a contact form on a website.
Messages are sent through mail.

## API

This service is exposing one REST endpoint:

```
POST /v1/message

{
  "subject": "this field will be used as a subject of a message",
  "content": "this will be body of the message"
}
```

 - Request:

   - Type: __JSON__
   - Fields:

     - "subject" - required field representing message subject
     - "content" - message field used in default message template (please take a look on [Templates](#templates) section for more information)

 - Success response:

   - Code: __200__
   - Content: __*empty*__

 - Error response:

   - Code: __429__
   - Content: __*empty*__

## Docker image

This service is exposed as a [klyman/contact-service](https://hub.docker.com/r/klyman/contact-service) image on [Docker HUB](https://hub.docker.com/).

### Configuration

Container is configured by environment variables.

#### Mail configuration

 - `MAIL_SERVER_DOMAIN` - should be set to mail server domain address
 - `MAIL_SERVER_PORT` - (by default 587) should be set to mail server port
 - `MAIL_SERVER_USERNAME` - username for mail server
 - `MAIL_SERVER_PASSWORD` - password for mail server
 - `MAIL_SERVER_USERNAME_FILE` - path to file containing mail username. This will be used only when `MAIL_SERVER_USERNAME` is empty 
 - `MAIL_SERVER_PASSWORD_FILE` - path to file containing mail password. This will be used only when `MAIL_SERVER_PASSWORD` is empty
 - `MAIL_RECIPIENT_MAIL` - mail address to which messages will be sent
 - `MAIL_SENDER_MAIL` - mail address which will be set as sender

#### Throttling configuration

 - `THROTTLING_IP_LIMIT` - (by default 5) message limit per IP configured with `THROTTLING_IP_WINDOW`
 - `THROTTLING_IP_WINDOW` - (by default 24h) time window in which ip limit will be applied provided as value and time unit (e.g. 4h)
 - `THROTTLING_ALL_LIMIT` - (by default 15) global message limit configured with `THROTTLING_ALL_WINDOW`
 - `THROTTLING_ALL_WINDOW` - (by default 24h) time window in which global limit will be applied provided as value and time unit (e.g. 4h)
 - `THROTTLING_CLEAR_EXPIRED_RATE` - (by default 24h) expired windows clearing rate 

### Example docker-compose

```yaml
version: '3.1'

services:
  contact-service:
    image: klyman/contact-service:latest
    environment:
      - MAIL_RECIPIENT_MAIL=recipient@mailserver.com
      - MAIL_SENDER_MAIL=sender@mailserver.com
      - MAIL_SERVER_DOMAIN=mailserver.com
      - MAIL_SERVER_USERNAME_FILE=/run/secrets/username
      - MAIL_SERVER_PASSWORD_FILE=/run/secrets/password
    volumes:
      - ./config:/run/secrets
      - ./config/template.mustache:/app/message.mustache
    ports:
      - "80:80"
```

## Templates

Messages can be personalized by using [Mustache](https://mustache.github.io/) templates. Default template
uses only `content` field:

```mustache
{{content}}
```

It is possible to inject custom message template by creating volume on `/app/message.mustache`
(please take a look on example in [Example docker-compose](#Example-docker-compose) section).

### Example

For following template:

```mustache
Phone Number: {{phone}}
Message: {{message}}
```

User should send request:

```
POST /v1/message

{
  "subject": "mysubject",
  "phone": "123 456 789",
  "message": "message"
}
```

It is also possible to include `subject` field in template:

```mustache
Subject: {{subject}}
Phone Number: {{phone}}
Message: {{message}}
```
