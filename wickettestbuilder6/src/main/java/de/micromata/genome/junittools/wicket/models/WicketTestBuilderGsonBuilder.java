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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class WicketTestBuilderGsonBuilder
{
  private static GsonBuilder gsonBuilder;

  private static final Logger LOG = LoggerFactory.getLogger(WicketTestBuilderGsonBuilder.class);

  public static Gson getGson()
  {
    if (gsonBuilder == null) {
      gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapterFactory(new TypeAdapterFactory() {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
        {
          if (type.getRawType() == Class.class) {
            return new TypeAdapter<T>() {
              @Override
              public void write(JsonWriter out, T value) throws IOException
              {
                out.value(((Class) value).getName());
              }

              @Override
              public T read(JsonReader in) throws IOException
              {
                try {
                  return (T) Class.forName(in.nextString());
                } catch (ClassNotFoundException e) {
                  LOG.error("unable to transform " + in.nextString() + " to a reasonable class, please check this!");
                }
                return null;
              }
            };
          }
          return null;
        }
      });
    }
    return gsonBuilder.create();
  }
}
