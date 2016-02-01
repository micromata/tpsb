package de.micromata.genome.junittools.wicket.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class WicketTestBuilderModelWrapper implements Serializable
{
  private List<ModelWrapper> wrappedList;

  public WicketTestBuilderModelWrapper()
  {
    wrappedList = new LinkedList<ModelWrapper>();
  }

  public WicketTestBuilderModelWrapper addModelWrapper(Class< ? extends Component> targetClass, String targetWicketPath)
  {
    wrappedList.add(new ModelWrapper(targetClass, targetWicketPath));
    return this;
  }

  public WicketTestBuilderModelWrapper addModelWrapper(Class< ? extends Component> targetClass, String targetWicketPath, Object modelValue)
  {
    wrappedList.add(new ModelWrapper(targetClass, targetWicketPath, modelValue));
    return this;
  }

  public int size()
  {
    return wrappedList.size();
  }

  public List<ModelWrapper> getWrappedList()
  {
    return wrappedList;
  }

  public static class ModelWrapper
  {
    Class< ? extends Component> targetClass;

    String targetWicketPath;

    Object modelValue;

    private ModelWrapper(Class< ? extends Component> targetClass, String targetWicketPath)
    {
      this.targetClass = targetClass;
      this.targetWicketPath = targetWicketPath;
    }

    private ModelWrapper(Class< ? extends Component> targetClass, String targetWicketPath, Object modelValue)
    {
      this.targetClass = targetClass;
      this.targetWicketPath = targetWicketPath;
      this.modelValue = modelValue;
    }

    public Class< ? extends Component> getTargetClass()
    {
      return targetClass;
    }

    public String getTargetWicketPath()
    {
      return targetWicketPath;
    }

    public Object getModelValue()
    {
      return modelValue;
    }
  }
}
