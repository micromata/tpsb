package de.micromata.tpsb.doc.parser;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AnnotatedBase extends WithJavaDocBase implements AnnotatedInfo, WithValidationMessages, Serializable
{

  private static final long serialVersionUID = 8114928768884274629L;

  private List<AnnotationInfo> annotations;

  /**
   * Combination of reflect.Modifier
   */
  private int modifier;

  private transient List<String> validationMessages = null;

  public AnnotatedBase()
  {

  }

  public AnnotatedBase(AnnotatedBase other)
  {
    super(other);
    this.modifier = other.modifier;
    if (other.getAnnotations() != null) {
      this.annotations = new ArrayList<AnnotationInfo>();
      this.annotations.addAll(other.getAnnotations());
    }
  }

  public void addAnnotation(AnnotationInfo ai)
  {
    if (annotations == null) {
      annotations = new ArrayList<AnnotationInfo>();
    }
    annotations.add(ai);
  }

  @Override
  public boolean isValid()
  {
    return validationMessages == null || validationMessages.isEmpty();
  }

  @Override
  public void addValidationMessage(String message)
  {
    if (validationMessages == null) {
      validationMessages = new ArrayList<String>();
    }
    validationMessages.add(message);
  }

  @Override
  public List<String> getValidationMessages()
  {
    if (validationMessages == null) {
      validationMessages = new ArrayList<String>();
    }
    return validationMessages;
  }

  @Override
  public void collectValidationMessages(List<String> ret)
  {
    if (validationMessages != null) {
      ret.addAll(validationMessages);
    }
  }

  @Override
  public void clearValidationMessages()
  {
    validationMessages = null;
  }

  @Override
  public void clearValidationMessagesWithPrefix(String prefix)
  {
    if (validationMessages == null) {
      return;
    }
    for (Iterator<String> it = validationMessages.iterator(); it.hasNext();) {
      String t = it.next();
      if (t.startsWith(prefix) == true) {
        it.remove();
      }
    }
  }

  @Override
  public List<AnnotationInfo> getAnnotations()
  {
    return annotations;
  }

  public void setAnnotations(List<AnnotationInfo> annotations)
  {
    this.annotations = annotations;
  }

  public boolean isTpsbIgnore()
  {
    if ((modifier & Modifier.PUBLIC) != Modifier.PUBLIC) {
      return true;
    }
    if ((modifier & Modifier.STATIC) == Modifier.STATIC) {
      return true;
    }
    if (annotations != null) {
      for (AnnotationInfo ai : annotations) {
        if (ai.getName().equals("TpsbIgnore") == true) {
          return true;
        }
      }
    }
    return false;
  }

  public int getModifier()
  {
    return modifier;
  }

  public void setModifier(int modifier)
  {
    this.modifier = modifier;
  }
}
