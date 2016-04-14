//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
