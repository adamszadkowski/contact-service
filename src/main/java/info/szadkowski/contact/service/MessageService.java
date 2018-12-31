package info.szadkowski.contact.service;

import info.szadkowski.contact.model.MessageRequest;

public interface MessageService {
  void send(MessageRequest content);
}
