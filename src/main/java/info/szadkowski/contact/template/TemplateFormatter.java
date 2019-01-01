package info.szadkowski.contact.template;

import java.util.Map;

public interface TemplateFormatter {
  String format(Map<String, String> message);
}
