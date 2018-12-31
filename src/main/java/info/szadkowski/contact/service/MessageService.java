package info.szadkowski.contact.service;

import info.szadkowski.contact.model.MessageContent;

public interface MessageService {
  void send(MessageContent content);
}
