#!/usr/bin/env bash

if [[ -z "${MAIL_SERVER_USERNAME}" ]]; then
  export MAIL_SERVER_USERNAME=`cat ${MAIL_SERVER_USERNAME_FILE}`
fi

if [[ -z "${MAIL_SERVER_PASSWORD}" ]]; then
  export MAIL_SERVER_PASSWORD=`cat ${MAIL_SERVER_PASSWORD_FILE}`
fi

java -cp app:app/lib/* info.szadkowski.contact.ContactServiceApplication
