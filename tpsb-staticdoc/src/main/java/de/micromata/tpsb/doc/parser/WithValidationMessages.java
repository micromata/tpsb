package de.micromata.tpsb.doc.parser;

import java.util.List;

/**
 * Has validation messages
 * 
 * @author roger
 * 
 */
public interface WithValidationMessages
{
  public boolean isValid();

  public void addValidationMessage(String message);

  public List<String> getValidationMessages();

  public void collectValidationMessages(List<String> ret);

  public void clearValidationMessages();

  public void clearValidationMessagesWithPrefix(String prefix);
}
